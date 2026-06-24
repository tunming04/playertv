import Foundation

class StalkerApiService {
    
    private let decoder = JSONDecoder()
    
    struct StalkerChannel: Codable {
        let id: Int
        let name: String
        let cmd: String
        let tvGenreId: Int
        let number: String
        let logo: String?
        
        enum CodingKeys: String, CodingKey {
            case id, name, cmd, number, logo
            case tvGenreId = "tv_genre_id"
        }
    }
    
    struct StalkerGenre: Codable {
        let id: Int
        let name: String
    }
    
    struct StalkerMovie: Codable {
        let id: Int
        let name: String
        let cmd: String
        let year: String?
        let time: String?
        let description: String?
        let logo: String?
    }
    
    struct StalkerOrderedListResponse: Codable {
        let js: StalkerJs?
    }
    
    struct StalkerJs: Codable {
        let data: [StalkerChannel]?
        let totalItems: Int?
        
        enum CodingKeys: String, CodingKey {
            case data
            case totalItems = "total_items"
        }
    }
    
    func handshake(serverUrl: String, macAddress: String, deviceId: String = "", stbType: String = "STB") async throws -> String? {
        let urlString = "\(serverUrl)/stalker_portal/server/load.php?action=handshake&type=stb&token=&JsHttpRequest=1&ver=7.4.7&device_id=\(deviceId)&device_id2=\(macAddress)&stb_type=\(stbType)"
        guard let url = URL(string: urlString) else { return nil }
        
        var request = URLRequest(url: url)
        request.setValue("mac=\(macAddress)", forHTTPHeaderField: "Cookie")
        request.setValue("PortalForStb", forHTTPHeaderField: "X-User-Agent")
        
        let (data, _) = try await URLSession.shared.data(for: request)
        let response = try decoder.decode([String: AnyCodable].self, from: data)
        
        if let js = response["js"]?.value as? [String: Any],
           let token = js["token"] as? String {
            return token
        }
        
        return nil
    }
    
    func getLiveChannels(serverUrl: String, token: String, macAddress: String, categoryId: Int? = nil, page: Int = 1, perPage: Int = 50) async throws -> [StalkerChannel] {
        var urlString = "\(serverUrl)/stalker_portal/server/load.php?type=itv&retession_hours=24&action=get_ordered_list&p=\(page)&c=\(perPage)"
        if let categoryId = categoryId {
            urlString += "&genre=\(categoryId)"
        }
        guard let url = URL(string: urlString) else { return [] }
        
        var request = URLRequest(url: url)
        request.setValue("mac=\(macAddress);stb_token=\(token)", forHTTPHeaderField: "Cookie")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("PortalForStb", forHTTPHeaderField: "X-User-Agent")
        
        let (data, _) = try await URLSession.shared.data(for: request)
        let response = try decoder.decode(StalkerOrderedListResponse.self, from: data)
        
        return response.js?.data ?? []
    }
    
    func getLiveCategories(serverUrl: String, token: String, macAddress: String) async throws -> [StalkerGenre] {
        let urlString = "\(serverUrl)/stalker_portal/server/load.php?type=itv&action=get_genres"
        guard let url = URL(string: urlString) else { return [] }
        
        var request = URLRequest(url: url)
        request.setValue("mac=\(macAddress);stb_token=\(token)", forHTTPHeaderField: "Cookie")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("PortalForStb", forHTTPHeaderField: "X-User-Agent")
        
        let (data, _) = try await URLSession.shared.data(for: request)
        let response = try decoder.decode([String: AnyCodable].self, from: data)
        
        if let js = response["js"]?.value as? [[String: Any]] {
            let jsonData = try JSONSerialization.data(withJSONObject: js)
            return try decoder.decode([StalkerGenre].self, from: jsonData)
        }
        
        return []
    }
    
    func getVodMovies(serverUrl: String, token: String, macAddress: String, categoryId: Int? = nil, page: Int = 1, perPage: Int = 50) async throws -> [StalkerMovie] {
        var urlString = "\(serverUrl)/stalker_portal/server/load.php?type=vod&retession_hours=24&action=get_ordered_list&p=\(page)&c=\(perPage)"
        if let categoryId = categoryId {
            urlString += "&genre=\(categoryId)"
        }
        guard let url = URL(string: urlString) else { return [] }
        
        var request = URLRequest(url: url)
        request.setValue("mac=\(macAddress);stb_token=\(token)", forHTTPHeaderField: "Cookie")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("PortalForStb", forHTTPHeaderField: "X-User-Agent")
        
        let (data, _) = try await URLSession.shared.data(for: request)
        let response = try decoder.decode([String: AnyCodable].self, from: data)
        
        if let js = response["js"]?.value as? [String: Any],
           let dataArray = js["data"] as? [[String: Any]] {
            let jsonData = try JSONSerialization.data(withJSONObject: dataArray)
            return try decoder.decode([StalkerMovie].self, from: jsonData)
        }
        
        return []
    }
    
    func createLiveLink(serverUrl: String, token: String, macAddress: String, channelId: Int) async throws -> String? {
        let urlString = "\(serverUrl)/stalker_portal/server/load.php?type=itv&action=create_link&ch_id=\(channelId)"
        guard let url = URL(string: urlString) else { return nil }
        
        var request = URLRequest(url: url)
        request.setValue("mac=\(macAddress);stb_token=\(token)", forHTTPHeaderField: "Cookie")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("PortalForStb", forHTTPHeaderField: "X-User-Agent")
        
        let (data, _) = try await URLSession.shared.data(for: request)
        let response = try decoder.decode([String: AnyCodable].self, from: data)
        
        if let js = response["js"]?.value as? [String: Any],
           let cmd = js["cmd"] as? String {
            return cmd
        }
        
        return nil
    }
    
    func convertToChannel(_ stalkerChannel: StalkerChannel) -> Channel {
        return Channel(
            id: "stalker_\(stalkerChannel.id)",
            name: stalkerChannel.name,
            url: "",
            logo: stalkerChannel.logo,
            groupTitle: nil,
            tvgId: nil,
            tvgName: stalkerChannel.name,
            isRadio: false,
            streamFormat: .hls,
            streamType: .live
        )
    }
    
    func convertToChannel(_ stalkerMovie: StalkerMovie) -> Channel {
        return Channel(
            id: "stalker_vod_\(stalkerMovie.id)",
            name: stalkerMovie.name,
            url: "",
            logo: stalkerMovie.logo,
            groupTitle: nil,
            tvgId: nil,
            tvgName: stalkerMovie.name,
            isRadio: false,
            streamFormat: .hls,
            streamType: .vod
        )
    }
}

// Helper for decoding dynamic JSON
struct AnyCodable: Codable {
    let value: Any
    
    init(_ value: Any) {
        self.value = value
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if let intVal = try? container.decode(Int.self) {
            value = intVal
        } else if let doubleVal = try? container.decode(Double.self) {
            value = doubleVal
        } else if let boolVal = try? container.decode(Bool.self) {
            value = boolVal
        } else if let stringVal = try? container.decode(String.self) {
            value = stringVal
        } else if let arrayVal = try? container.decode([AnyCodable].self) {
            value = arrayVal.map { $0.value }
        } else if let dictVal = try? container.decode([String: AnyCodable].self) {
            value = dictVal.mapValues { $0.value }
        } else {
            value = NSNull()
        }
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        if let intVal = value as? Int {
            try container.encode(intVal)
        } else if let doubleVal = value as? Double {
            try container.encode(doubleVal)
        } else if let boolVal = value as? Bool {
            try container.encode(boolVal)
        } else if let stringVal = value as? String {
            try container.encode(stringVal)
        } else {
            try container.encodeNil()
        }
    }
}
