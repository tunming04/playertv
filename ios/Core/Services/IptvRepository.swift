import Foundation
import Combine

class AppRepository: ObservableObject {
    @Published var playlists: [Playlist] = []
    @Published var channels: [Channel] = []
    @Published var favorites: Set<String> = []
    
    private let m3uParser = M3UParser()
    private let xtreamApi = XtreamApiService()
    private let stalkerApi = StalkerApiService()
    
    private let playlistsKey = "playlists"
    private let favoritesKey = "favorites"
    
    init() {
        loadPlaylists()
        loadFavorites()
    }
    
    // MARK: - Playlist Operations
    
    func importM3uFromUrl(_ url: String, name: String) async throws -> Playlist {
        let result = try await M3UParser.parse(url: url)
        let channels = result.channels
        guard !channels.isEmpty else {
            throw IptvError.noChannels
        }
        
        let playlist = Playlist(
            id: UUID().uuidString,
            name: name,
            url: url,
            filePath: nil,
            type: .m3u,
            channelCount: channels.count,
            lastUpdated: Date(),
            epgUrl: nil,
            username: nil,
            password: nil,
            serverUrl: nil
        )
        
        playlists.append(playlist)
        savePlaylists()
        
        return playlist
    }
    
    func importM3uFromFile(_ content: String, name: String) throws -> Playlist {
        let channels = M3UParser.parseContent(content)
        guard !channels.isEmpty else {
            throw IptvError.noChannels
        }
        
        let playlist = Playlist(
            id: UUID().uuidString,
            name: name,
            url: nil,
            filePath: nil,
            type: .m3uFile,
            channelCount: channels.count,
            lastUpdated: Date(),
            epgUrl: nil,
            username: nil,
            password: nil,
            serverUrl: nil
        )
        
        playlists.append(playlist)
        savePlaylists()
        
        return playlist
    }
    
    func importXtream(credentials: XtreamCredentials) async throws -> Playlist {
        guard let authResponse = try await xtreamApi.authenticate(
            serverUrl: credentials.serverUrl,
            username: credentials.username,
            password: credentials.password
        ) else {
            throw IptvError.authenticationFailed
        }
        
        guard authResponse.userInfo?.auth == 1 else {
            throw IptvError.invalidCredentials
        }
        
        let playlist = Playlist(
            id: UUID().uuidString,
            name: credentials.serverUrl.components(separatedBy: "://").last?.components(separatedBy: "/").first ?? "Xtream",
            url: nil,
            filePath: nil,
            type: .xtream,
            channelCount: 0,
            lastUpdated: Date(),
            epgUrl: nil,
            username: credentials.username,
            password: credentials.password,
            serverUrl: credentials.serverUrl
        )
        
        playlists.append(playlist)
        savePlaylists()
        
        return playlist
    }
    
    func importStalker(serverUrl: String, macAddress: String) async throws -> Playlist {
        guard let token = try await stalkerApi.handshake(
            serverUrl: serverUrl,
            macAddress: macAddress
        ) else {
            throw IptvError.authenticationFailed
        }
        
        let playlist = Playlist(
            id: UUID().uuidString,
            name: serverUrl.components(separatedBy: "://").last?.components(separatedBy: "/").first ?? "Stalker",
            url: nil,
            filePath: nil,
            type: .stalker,
            channelCount: 0,
            lastUpdated: Date(),
            epgUrl: nil,
            username: nil,
            password: nil,
            serverUrl: serverUrl
        )
        
        playlists.append(playlist)
        savePlaylists()
        
        return playlist
    }
    
    // MARK: - Channel Operations
    
    func getChannelsForPlaylist(_ playlistId: String) async throws -> [Channel] {
        guard let playlist = playlists.first(where: { $0.id == playlistId }) else {
            throw IptvError.playlistNotFound
        }
        
        switch playlist.type {
        case .m3u, .m3uFile:
            guard let url = playlist.url else { return [] }
            let result = try await M3UParser.parse(url: url)
            return result.channels
            
        case .xtream:
            guard let serverUrl = playlist.serverUrl,
                  let username = playlist.username,
                  let password = playlist.password else {
                return []
            }
            let channels = try await xtreamApi.getLiveStreams(
                serverUrl: serverUrl,
                username: username,
                password: password
            )
            return channels.map { channel in
                Channel(
                    id: "xtream_\(channel.streamId)",
                    name: channel.name,
                    url: xtreamApi.buildLiveStreamUrl(
                        serverUrl: serverUrl,
                        username: username,
                        password: password,
                        streamId: channel.streamId
                    ),
                    logo: channel.streamIcon.isEmpty ? nil : channel.streamIcon,
                    groupTitle: channel.categoryId,
                    tvgId: channel.epgChannelId,
                    tvgName: channel.name,
                    isRadio: false,
                    streamFormat: .hls,
                    streamType: .live
                )
            }
            
        case .stalker:
            guard let serverUrl = playlist.serverUrl else { return [] }
            // Stalker requires token - simplified for now
            return []
        }
    }
    
    // MARK: - Favorites
    
    func toggleFavorite(_ channelId: String) {
        if favorites.contains(channelId) {
            favorites.remove(channelId)
        } else {
            favorites.insert(channelId)
        }
        saveFavorites()
    }
    
    func isFavorite(_ channelId: String) -> Bool {
        favorites.contains(channelId)
    }
    
    // MARK: - Delete/Refresh
    
    func deletePlaylist(_ playlist: Playlist) {
        playlists.removeAll { $0.id == playlist.id }
        savePlaylists()
    }
    
    func refreshPlaylist(_ playlist: Playlist) async throws {
        // Simplified refresh
        _ = try await getChannelsForPlaylist(playlist.id)
    }
    
    // MARK: - Persistence
    
    private func savePlaylists() {
        if let data = try? JSONEncoder().encode(playlists) {
            UserDefaults.standard.set(data, forKey: playlistsKey)
        }
    }
    
    private func loadPlaylists() {
        if let data = UserDefaults.standard.data(forKey: playlistsKey),
           let decoded = try? JSONDecoder().decode([Playlist].self, from: data) {
            playlists = decoded
        }
    }
    
    private func saveFavorites() {
        if let data = try? JSONEncoder().encode(Array(favorites)) {
            UserDefaults.standard.set(data, forKey: favoritesKey)
        }
    }
    
    private func loadFavorites() {
        if let data = UserDefaults.standard.data(forKey: favoritesKey),
           let decoded = try? JSONDecoder().decode(Set<String>.self, from: data) {
            favorites = decoded
        }
    }
}

enum IptvError: Error, LocalizedError {
    case noChannels
    case authenticationFailed
    case invalidCredentials
    case playlistNotFound
    case networkError(Error)
    
    var errorDescription: String? {
        switch self {
        case .noChannels:
            return "Không tìm thấy kênh nào"
        case .authenticationFailed:
            return "Xác thực thất bại"
        case .invalidCredentials:
            return "Thông tin Ä‘Äƒng nhập không hợp lá»‡"
        case .playlistNotFound:
            return "Không tìm thấy danh sách phát"
        case .networkError(let error):
            return "Lá»—i mạng: \(error.localizedDescription)"
        }
    }
}
