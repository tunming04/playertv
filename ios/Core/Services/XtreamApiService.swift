import Foundation

class XtreamApiService {
    
    private let decoder = JSONDecoder()
    
    struct XtreamResponse: Codable {
        let userInfo: UserInfo?
        let serverInfo: ServerInfo?
        
        enum CodingKeys: String, CodingKey {
            case userInfo = "user_info"
            case serverInfo = "server_info"
        }
    }
    
    struct UserInfo: Codable {
        let username: String
        let password: String
        let message: String
        let auth: Int
        let status: String
        let expDate: String?
        
        enum CodingKeys: String, CodingKey {
            case username, password, message, auth, status
            case expDate = "exp_date"
        }
    }
    
    struct ServerInfo: Codable {
        let url: String
        let port: String
        let timezone: String
    }
    
    struct XtreamCategory: Codable {
        let categoryId: String
        let categoryName: String
        
        enum CodingKeys: String, CodingKey {
            case categoryId = "category_id"
            case categoryName = "category_name"
        }
    }
    
    struct XtreamChannel: Codable {
        let num: Int
        let name: String
        let streamType: String
        let streamId: Int
        let streamIcon: String
        let epgChannelId: String?
        let categoryId: String
        
        enum CodingKeys: String, CodingKey {
            case num, name
            case streamType = "stream_type"
            case streamId = "stream_id"
            case streamIcon = "stream_icon"
            case epgChannelId = "epg_channel_id"
            case categoryId = "category_id"
        }
    }
    
    func authenticate(serverUrl: String, username: String, password: String) async throws -> XtreamResponse? {
        let urlString = "\(serverUrl)/player_api.php?username=\(username)&password=\(password)"
        guard let url = URL(string: urlString) else { return nil }
        
        let (data, _) = try await URLSession.shared.data(from: url)
        return try decoder.decode(XtreamResponse.self, from: data)
    }
    
    func getLiveCategories(serverUrl: String, username: String, password: String) async throws -> [XtreamCategory] {
        let urlString = "\(serverUrl)/player_api.php?username=\(username)&password=\(password)&action=get_live_categories"
        guard let url = URL(string: urlString) else { return [] }
        
        let (data, _) = try await URLSession.shared.data(from: url)
        return try decoder.decode([XtreamCategory].self, from: data)
    }
    
    func getLiveStreams(serverUrl: String, username: String, password: String, categoryId: String? = nil) async throws -> [XtreamChannel] {
        var urlString = "\(serverUrl)/player_api.php?username=\(username)&password=\(password)&action=get_live_streams"
        if let categoryId = categoryId {
            urlString += "&category_id=\(categoryId)"
        }
        guard let url = URL(string: urlString) else { return [] }
        
        let (data, _) = try await URLSession.shared.data(from: url)
        return try decoder.decode([XtreamChannel].self, from: data)
    }
    
    func getVodCategories(serverUrl: String, username: String, password: String) async throws -> [XtreamCategory] {
        let urlString = "\(serverUrl)/player_api.php?username=\(username)&password=\(password)&action=get_vod_categories"
        guard let url = URL(string: urlString) else { return [] }
        
        let (data, _) = try await URLSession.shared.data(from: url)
        return try decoder.decode([XtreamCategory].self, from: data)
    }
    
    func getVodStreams(serverUrl: String, username: String, password: String, categoryId: String? = nil) async throws -> [XtreamChannel] {
        var urlString = "\(serverUrl)/player_api.php?username=\(username)&password=\(password)&action=get_vod_streams"
        if let categoryId = categoryId {
            urlString += "&category_id=\(categoryId)"
        }
        guard let url = URL(string: urlString) else { return [] }
        
        let (data, _) = try await URLSession.shared.data(from: url)
        return try decoder.decode([XtreamChannel].self, from: data)
    }
    
    func buildLiveStreamUrl(serverUrl: String, username: String, password: String, streamId: Int) -> String {
        return "\(serverUrl)/live/\(username)/\(password)/\(streamId).m3u8"
    }
    
    func buildVodStreamUrl(serverUrl: String, username: String, password: String, streamId: Int, extension: String = "mp4") -> String {
        return "\(serverUrl)/movie/\(username)/\(password)/\(streamId).\(extension)"
    }
}
