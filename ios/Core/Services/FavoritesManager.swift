import Foundation
import CryptoKit

class FavoritesManager: ObservableObject {
    @Published var favorites: [Channel] = []
    private let defaults = UserDefaults.standard
    private let favoritesKey = "favorites_list_secure"
    private let symmetricKey: SymmetricKey
    
    // We use a fixed key derived from the APP_SECRET for local storage encryption
    init() {
        let secret = "app-secret-key-change-me"
        let data = Data(secret.utf8)
        let hash = SHA256.hash(data: data)
        self.symmetricKey = SymmetricKey(data: hash)
        loadFavorites()
    }
    
    private func encryptUrl(_ url: String) -> String {
        guard let data = url.data(using: .utf8) else { return url }
        do {
            let sealedBox = try AES.GCM.seal(data, using: symmetricKey)
            return sealedBox.combined?.base64EncodedString() ?? url
        } catch {
            return url
        }
    }
    
    private func decryptUrl(_ encrypted: String) -> String {
        guard let data = Data(base64Encoded: encrypted) else { return encrypted }
        do {
            let sealedBox = try AES.GCM.SealedBox(combined: data)
            let decryptedData = try AES.GCM.open(sealedBox, using: symmetricKey)
            return String(data: decryptedData, encoding: .utf8) ?? encrypted
        } catch {
            return encrypted
        }
    }
    
    private func loadFavorites() {
        guard let data = defaults.data(forKey: favoritesKey) else { return }
        do {
            let decoder = JSONDecoder()
            let encryptedChannels = try decoder.decode([Channel].self, from: data)
            // Decrypt URLs
            self.favorites = encryptedChannels.map { channel in
                var decChannel = channel
                decChannel.url = decryptUrl(channel.url)
                return decChannel
            }
        } catch {
            print("Failed to load favorites: \(error)")
        }
    }
    
    private func saveFavorites() {
        do {
            // Encrypt URLs before saving
            let encryptedChannels = favorites.map { channel in
                var encChannel = channel
                encChannel.url = encryptUrl(channel.url)
                return encChannel
            }
            let encoder = JSONEncoder()
            let data = try encoder.encode(encryptedChannels)
            defaults.set(data, forKey: favoritesKey)
        } catch {
            print("Failed to save favorites: \(error)")
        }
    }
    
    func isFavorite(url: String) -> Bool {
        return favorites.contains { $0.url == url }
    }
    
    func toggleFavorite(channel: Channel) {
        if let index = favorites.firstIndex(where: { $0.url == channel.url }) {
            favorites.remove(at: index)
        } else {
            favorites.append(channel)
        }
        saveFavorites()
    }
}
