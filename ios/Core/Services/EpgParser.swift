import Foundation

class EpgParser: NSObject, XMLParserDelegate {
    
    private var programs: [EpgProgram] = []
    
    private var currentElement = ""
    private var currentChannelId = ""
    private var currentStartTimeStr = ""
    private var currentEndTimeStr = ""
    private var currentTitle = ""
    private var currentDesc = ""
    private var currentIcon: String?
    
    // Format: 20240321123000 +0000
    private lazy var dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyyMMddHHmmss Z"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        return formatter
    }()
    
    static func parse(url: String) async throws -> [EpgProgram] {
        guard let urlObj = URL(string: url) else { return [] }
        let (data, _) = try await URLSession.shared.data(from: urlObj)
        let parser = EpgParser()
        return parser.parse(data: data)
    }
    
    private func parse(data: Data) -> [EpgProgram] {
        let parser = XMLParser(data: data)
        parser.delegate = self
        parser.parse()
        return programs
    }
    
    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String] = [:]) {
        currentElement = elementName
        
        if elementName == "programme" {
            currentChannelId = attributeDict["channel"] ?? ""
            currentStartTimeStr = attributeDict["start"] ?? ""
            currentEndTimeStr = attributeDict["stop"] ?? ""
            currentTitle = ""
            currentDesc = ""
            currentIcon = nil
        } else if elementName == "icon" {
            currentIcon = attributeDict["src"]
        }
    }
    
    func parser(_ parser: XMLParser, foundCharacters string: String) {
        let trimmed = string.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty { return }
        
        if currentElement == "title" {
            currentTitle += string
        } else if currentElement == "desc" {
            currentDesc += string
        }
    }
    
    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        if elementName == "programme" {
            let startTime = dateFormatter.date(from: currentStartTimeStr) ?? Date()
            let endTime = dateFormatter.date(from: currentEndTimeStr) ?? Date()
            
            let program = EpgProgram(
                id: UUID().uuidString,
                channelId: currentChannelId,
                channelName: "",
                title: currentTitle.trimmingCharacters(in: .whitespacesAndNewlines),
                description: currentDesc.trimmingCharacters(in: .whitespacesAndNewlines),
                startTime: startTime,
                endTime: endTime,
                icon: currentIcon
            )
            programs.append(program)
        }
    }
}
