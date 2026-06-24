import Foundation
import Combine

class SettingsViewModel: ObservableObject {
    @Published var playlists: [Playlist] = []
    @Published var isLoading = false
    @Published var error: String?
    
    private let repository = AppRepository()
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        repository.$playlists
            .receive(on: DispatchQueue.main)
            .assign(to: &$playlists)
    }
    
    func importM3uFromUrl(_ url: String, name: String) {
        isLoading = true
        Task {
            do {
                _ = try await repository.importM3uFromUrl(url, name: name)
                isLoading = false
            } catch {
                self.error = error.localizedDescription
                isLoading = false
            }
        }
    }
    
    func importXtream(serverUrl: String, username: String, password: String) {
        isLoading = true
        Task {
            do {
                _ = try await repository.importXtream(
                    credentials: XtreamCredentials(
                        serverUrl: serverUrl,
                        username: username,
                        password: password
                    )
                )
                isLoading = false
            } catch {
                self.error = error.localizedDescription
                isLoading = false
            }
        }
    }
    
    func importStalker(serverUrl: String, macAddress: String) {
        isLoading = true
        Task {
            do {
                _ = try await repository.importStalker(
                    serverUrl: serverUrl,
                    macAddress: macAddress
                )
                isLoading = false
            } catch {
                self.error = error.localizedDescription
                isLoading = false
            }
        }
    }
    
    func deletePlaylist(_ playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
    
    func refreshPlaylist(_ playlist: Playlist) {
        Task {
            try? await repository.refreshPlaylist(playlist)
        }
    }
}
