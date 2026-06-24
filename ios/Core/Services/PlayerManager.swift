import Foundation
import AVKit
import Combine

class PlayerManager: ObservableObject {
    @Published var currentChannel: Channel?
    @Published var isPlaying: Bool = false
    @Published var isLoading: Bool = false
    
    let player = AVPlayer()
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        NotificationCenter.default.addObserver(
            forName: .AVPlayerItemDidPlayToEndTime,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.isPlaying = false
            self?.isLoading = false
        }
    }
    
    func loadChannel(_ channel: Channel) {
        guard let url = URL(string: channel.url) else { return }
        DispatchQueue.main.async {
            self.isLoading = true
            self.isPlaying = false
            self.currentChannel = channel
        }
        let headers: [String: String] = [
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        ]
        let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
        let item = AVPlayerItem(asset: asset)
        player.replaceCurrentItem(with: item)
        
        // Observe when player is ready
        let observer = item.observe(\.status) { [weak self] item, _ in
            DispatchQueue.main.async {
                if item.status == .readyToPlay {
                    self?.player.play()
                    self?.isPlaying = true
                    self?.isLoading = false
                } else if item.status == .failed {
                    self?.isLoading = false
                    self?.isPlaying = false
                }
            }
        }
        // Store observer lifecycle with the item
        objc_setAssociatedObject(item, "statusObserver", observer, .OBJC_ASSOCIATION_RETAIN)
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
