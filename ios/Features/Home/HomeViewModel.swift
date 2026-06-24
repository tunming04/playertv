import Foundation
import Combine

class HomeViewModel: ObservableObject {
    @Published var stats = DashboardStats(liveChannels: 0, movies: 0, series: 0)
    @Published var recentChannels: [Channel] = []
    @Published var playlists: [Playlist] = []
    @Published var isLoading = false
    @Published var error: String?
    
    private let repository = AppRepository()
    private var cancellables = Set<AnyCancellable>()
    
    // Cloudflare Worker URL - update sau khi deploy
    private let defaultPlaylistUrl = "https://playertv-app.YOUR_SUBDOMAIN.workers.dev/playlist.m3u"
    
    init() {
        loadDefaultPlaylistIfNeeded()
        loadData()
    }
    
    private func loadDefaultPlaylistIfNeeded() {
        // Check if first launch
        if repository.playlists.isEmpty {
            Task {
                do {
                    _ = try await repository.importM3uFromUrl(defaultPlaylistUrl, name: "PlayerTV (Default)")
                } catch {
                    // Fallback to built-in defaults
                    importBuiltInDefaults()
                }
            }
        }
    }
    
    private func importBuiltInDefaults() {
        // Removed hardcoded channels
    }
    
    private func loadData() {
        repository.$playlists
            .receive(on: DispatchQueue.main)
            .sink { [weak self] playlists in
                self?.playlists = playlists
                let totalChannels = playlists.reduce(0) { $0 + $1.channelCount }
                self?.stats = DashboardStats(
                    liveChannels: totalChannels,
                    movies: 0,
                    series: 0
                )
            }
            .store(in: &cancellables)
    }
}
