import SwiftUI
import AVKit

struct AddPlaylistView: View {
    @State private var inputUrl: String = ""
    @State private var playingUrl: String? = nil
    
    // Player states
    @State private var player: AVPlayer = AVPlayer()
    @State private var showControls: Bool = false
    @State private var isMuted: Bool = false
    @State private var isFullScreen: Bool = false
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.darkBackground.ignoresSafeArea()
                
                VStack(spacing: 24) {
                    // Input Area
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            Image(systemName: "link")
                                .foregroundColor(.textMuted)
                            TextField("Dán link M3U8 vào đây...", text: $inputUrl)
                                .foregroundColor(.textPrimary)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                        }
                        .padding()
                        .background(Color.glassSurface)
                        .cornerRadius(12)
                        
                        Button(action: {
                            if !inputUrl.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                                playingUrl = inputUrl.trimmingCharacters(in: .whitespacesAndNewlines)
                            }
                        }) {
                            HStack {
                                Image(systemName: "play.fill")
                                Text("Phát Video")
                                    .fontWeight(.bold)
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.yellow)
                            .foregroundColor(.darkBackground)
                            .cornerRadius(12)
                        }
                    }
                    .padding(.horizontal)
                    
                    // Player Area
                    if let _ = playingUrl {
                        ZStack(alignment: .topTrailing) {
                            CustomVideoPlayer(player: player)
                                .aspectRatio(16/9, contentMode: .fit)
                                .background(Color.black)
                                .onTapGesture {
                                    withAnimation {
                                        showControls.toggle()
                                    }
                                    if showControls {
                                        hideControlsAfterDelay()
                                    }
                                }
                            
                            // Overlay Controls
                            if showControls {
                                VStack {
                                    HStack {
                                        Spacer()
                                        Button(action: {
                                            isMuted.toggle()
                                            player.isMuted = isMuted
                                        }) {
                                            Image(systemName: isMuted ? "speaker.slash.fill" : "speaker.wave.3.fill")
                                                .foregroundColor(.white)
                                                .padding(12)
                                                .background(Color.black.opacity(0.5))
                                                .clipShape(Circle())
                                        }
                                        
                                        Button(action: {
                                            isFullScreen.toggle()
                                        }) {
                                            Image(systemName: "pip.enter")
                                                .foregroundColor(.white)
                                                .padding(12)
                                                .background(Color.black.opacity(0.5))
                                                .clipShape(Circle())
                                        }
                                        
                                        Button(action: {
                                            isFullScreen.toggle()
                                        }) {
                                            Image(systemName: isFullScreen ? "arrow.down.right.and.arrow.up.left" : "arrow.up.left.and.arrow.down.right")
                                                .foregroundColor(.white)
                                                .padding(12)
                                                .background(Color.black.opacity(0.5))
                                                .clipShape(Circle())
                                        }
                                    }
                                    .padding(8)
                                    
                                    Spacer()
                                }
                                .background(LinearGradient(gradient: Gradient(colors: [Color.black.opacity(0.7), Color.clear]), startPoint: .top, endPoint: .bottom))
                            }
                        }
                    } else {
                        VStack(spacing: 16) {
                            Image(systemName: "tv")
                                .font(.system(size: 48))
                                .foregroundColor(.textMuted)
                            Text("Trình phát sẽ hiển thị ở đây")
                                .foregroundColor(.textMuted)
                        }
                        .frame(maxWidth: .infinity)
                        .aspectRatio(16/9, contentMode: .fit)
                        .background(Color.darkSurface)
                        .cornerRadius(12)
                        .padding(.horizontal)
                    }
                    
                    Spacer()
                }
                .padding(.top)
            }
            .navigationTitle("Phát Link M3U8")
            .navigationBarTitleDisplayMode(.inline)
            .fullScreenCover(isPresented: $isFullScreen) {
                if let urlString = playingUrl, let url = URL(string: urlString) {
                    // Quick full screen fallback, assuming PlayerView might need a Channel
                    // Let's just use AVPlayerViewController for simple M3U8 input full screen
                    FullScreenPlayer(player: player)
                        .ignoresSafeArea()
                }
            }
            .onChange(of: playingUrl) { newUrlString in
                if let newUrlString = newUrlString, let url = URL(string: newUrlString) {
                    let headers: [String: String] = [
                        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
                    ]
                    let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
                    let item = AVPlayerItem(asset: asset)
                    player.replaceCurrentItem(with: item)
                    player.play()
                }
            }
        }
    }
    
    private func hideControlsAfterDelay() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            withAnimation {
                if showControls {
                    showControls = false
                }
            }
        }
    }
}

struct FullScreenPlayer: UIViewControllerRepresentable {
    var player: AVPlayer
    
    func makeUIViewController(context: Context) -> AVPlayerViewController {
        let controller = AVPlayerViewController()
        controller.player = player
        controller.showsPlaybackControls = true
        return controller
    }
    
    func updateUIViewController(_ uiViewController: AVPlayerViewController, context: Context) {
        uiViewController.player = player
    }
}
