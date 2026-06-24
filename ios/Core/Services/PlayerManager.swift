import Foundation
import AVKit
import Combine

class PlayerManager: ObservableObject {
    @Published var currentChannel: Channel?
    @Published var isPlaying: Bool = false
    
    let player = AVPlayer()
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        NotificationCenter.default.addObserver(
            forName: .AVPlayerItemDidPlayToEndTime,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.isPlaying = false
        }
    }
    
    func loadChannel(_ channel: Channel) {
        guard let url = URL(string: channel.url) else { return }
        let headers: [String: String] = [
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        ]
        let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
        let item = AVPlayerItem(asset: asset)
        player.replaceCurrentItem(with: item)
        player.play()
        DispatchQueue.main.async {
            self.currentChannel = channel
            self.isPlaying = true
        }
    }
    
    func togglePlayPause() {
        if isPlaying {
            player.pause()
            isPlaying = false
        } else {
            player.play()
            isPlaying = true
        }
    }
    
    func stop() {
        player.pause()
        player.replaceCurrentItem(with: nil)
        currentChannel = nil
        isPlaying = false
    }
}
