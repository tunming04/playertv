import Foundation
import CryptoKit

struct Constants {
    static let apiBaseUrl = "https://playertv-app.buoisangvatoi.workers.dev"
    private static let appSecret = "app-secret-key-change-me"
    
    static func getPlaylistUrl() -> String {
        // Calculate milliseconds since 1970
        let now = Date().timeIntervalSince1970 * 1000
        let hour = Int64(now) / 3600000
        let message = "\(appSecret):\(hour)"
        
        let data = Data(message.utf8)
        let hash = SHA256.hash(data: data)
        let token = hash.compactMap { String(format: "%02x", $0) }.joined()
        
        return "\(apiBaseUrl)/api/playlist?token=\(token)"
    }
}
