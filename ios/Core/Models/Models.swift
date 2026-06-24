import Foundation

struct Channel: Identifiable, Codable {
    let id: String
    let name: String
    var url: String
    let logo: String?
    let groupTitle: String?
    let tvgId: String?
    let tvgName: String?
    let isRadio: Bool
    let streamFormat: StreamFormat
    let streamType: StreamType
    
    init(id: String, name: String, url: String, logo: String? = nil, groupTitle: String? = nil, tvgId: String? = nil, tvgName: String? = nil, isRadio: Bool = false, streamFormat: StreamFormat = .hls, streamType: StreamType = .live) {
        self.id = id
        self.name = name
        self.url = url
        self.logo = logo
        self.groupTitle = groupTitle
        self.tvgId = tvgId
        self.tvgName = tvgName
        self.isRadio = isRadio
        self.streamFormat = streamFormat
        self.streamType = streamType
    }
}

enum StreamFormat: String, Codable {
    case hls, dash, mpegts, rtmp, other
}

enum StreamType: String, Codable {
    case live, vod, series
}

struct Playlist: Identifiable, Codable {
    let id: String
    var name: String
    let url: String?
    let filePath: String?
    let type: PlaylistType
    var channelCount: Int
    var lastUpdated: Date
    var epgUrl: String?
    // Xtream
    var username: String?
    var password: String?
    var serverUrl: String?
}

enum PlaylistType: String, Codable {
    case m3u, m3uFile, xtream, stalker
}

struct EpgProgram: Identifiable, Codable {
    let id: String
    let channelId: String
    let channelName: String
    let title: String
    let description: String?
    let startTime: Date
    let endTime: Date
    let icon: String?
}

struct Category: Identifiable, Codable {
    let id: String
    let name: String
    let channelCount: Int
}

struct DashboardStats {
    let liveChannels: Int
    let movies: Int
    let series: Int
}

struct XtreamCredentials {
    let serverUrl: String
    let username: String
    let password: String
}

struct StalkerCredentials {
    let serverUrl: String
    let macAddress: String?
}
