import Foundation

class M3UParser {
    struct ParseResult {
        let epgUrl: String?
        let channels: [Channel]
    }

    static func parseContent(_ content: String) -> [Channel] {
        var channels: [Channel] = []
        let lines = content.components(separatedBy: .newlines)
        var currentTvgName: String?
        var currentTvgId: String?
        var currentLogo: String?
        var currentGroup: String?

        for line in lines {
            let trimmed = line.trimmingCharacters(in: .whitespacesAndNewlines)
            if trimmed.hasPrefix("#EXTINF:") {
                if let nameRange = trimmed.range(of: "tvg-name=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[nameRange])
                    currentTvgName = match.replacingOccurrences(of: "tvg-name=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                if let idRange = trimmed.range(of: "tvg-id=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[idRange])
                    currentTvgId = match.replacingOccurrences(of: "tvg-id=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                if let logoRange = trimmed.range(of: "tvg-logo=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[logoRange])
                    currentLogo = match.replacingOccurrences(of: "tvg-logo=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                if let groupRange = trimmed.range(of: "group-title=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[groupRange])
                    currentGroup = match.replacingOccurrences(of: "group-title=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                if currentTvgName == nil, let commaIndex = trimmed.lastIndex(of: ",") {
                    currentTvgName = String(trimmed[trimmed.index(after: commaIndex)...]).trimmingCharacters(in: .whitespaces)
                }
            } else if !trimmed.isEmpty && !trimmed.hasPrefix("#") {
                if let name = currentTvgName {
                    channels.append(Channel(
                        id: UUID().uuidString, name: name, url: trimmed,
                        logo: currentLogo, groupTitle: currentGroup,
                        tvgId: currentTvgId, tvgName: currentTvgName,
                        isRadio: false,
                        streamFormat: trimmed.hasSuffix(".m3u8") ? .hls : .other,
                        streamType: .live
                    ))
                }
                currentTvgName = nil; currentTvgId = nil; currentLogo = nil; currentGroup = nil
            }
        }
        return channels
    }

    static func parse(url: String) async throws -> ParseResult {
        guard let urlObj = URL(string: url) else {
            return ParseResult(epgUrl: nil, channels: [])
        }
        
        let (data, _) = try await URLSession.shared.data(from: urlObj)
        guard let content = String(data: data, encoding: .utf8) else {
            return ParseResult(epgUrl: nil, channels: [])
        }
        
        var channels: [Channel] = []
        var epgUrl: String? = nil
        let lines = content.components(separatedBy: .newlines)
        
        var currentTvgName: String?
        var currentTvgId: String?
        var currentLogo: String?
        var currentGroup: String?
        
        for line in lines {
            let trimmed = line.trimmingCharacters(in: .whitespacesAndNewlines)
            
            if trimmed.hasPrefix("#EXTM3U") {
                if let urlRange = trimmed.range(of: "x-tvg-url=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[urlRange])
                    epgUrl = match.replacingOccurrences(of: "x-tvg-url=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
            } else if trimmed.hasPrefix("#EXTINF:") {
                // Parse attributes using simple regex/string search
                if let nameRange = trimmed.range(of: "tvg-name=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[nameRange])
                    currentTvgName = match.replacingOccurrences(of: "tvg-name=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                
                if let idRange = trimmed.range(of: "tvg-id=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[idRange])
                    currentTvgId = match.replacingOccurrences(of: "tvg-id=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                
                if let logoRange = trimmed.range(of: "tvg-logo=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[logoRange])
                    currentLogo = match.replacingOccurrences(of: "tvg-logo=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                
                if let groupRange = trimmed.range(of: "group-title=\"([^\"]+)\"", options: .regularExpression) {
                    let match = String(trimmed[groupRange])
                    currentGroup = match.replacingOccurrences(of: "group-title=\"", with: "").replacingOccurrences(of: "\"", with: "")
                }
                
                if currentTvgName == nil, let commaIndex = trimmed.lastIndex(of: ",") {
                    currentTvgName = String(trimmed[trimmed.index(after: commaIndex)...]).trimmingCharacters(in: .whitespaces)
                }
                
            } else if !trimmed.isEmpty && !trimmed.hasPrefix("#") {
                if let name = currentTvgName {
                    let channel = Channel(
                        id: UUID().uuidString,
                        name: name,
                        url: trimmed,
                        logo: currentLogo,
                        groupTitle: currentGroup,
                        tvgId: currentTvgId,
                        tvgName: currentTvgName,
                        isRadio: false,
                        streamFormat: trimmed.hasSuffix(".m3u8") ? .hls : .other,
                        streamType: .live
                    )
                    channels.append(channel)
                }
                
                currentTvgName = nil
                currentTvgId = nil
                currentLogo = nil
                currentGroup = nil
            }
        }
        
        return ParseResult(epgUrl: epgUrl, channels: channels)
    }
}
