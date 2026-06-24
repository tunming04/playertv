import SwiftUI
import AVKit

struct PlayerView: View {
    let channel: Channel
    @StateObject private var favoritesManager = FavoritesManager()
    @State private var player: AVPlayer?
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ZStack(alignment: .topLeading) {
            Color.black.ignoresSafeArea()
            
            if let player = player {
                VideoPlayer(player: player)
                    .ignoresSafeArea()
                    .onAppear {
                        player.play()
                    }
                    .onDisappear {
                        player.pause()
                    }
            } else {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
            
            VStack(alignment: .leading) {
                Button(action: {
                    dismiss()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.title2)
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.black.opacity(0.5))
                        .clipShape(Circle())
                }
                .padding()
                
                HStack {
                    Text(channel.name)
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding(.horizontal)
                        .padding(.vertical, 8)
                        .background(Color.black.opacity(0.5))
                        .cornerRadius(8)
                    
                    Spacer()
                    
                    Button(action: {
                        favoritesManager.toggleFavorite(channel: channel)
                    }) {
                        Image(systemName: favoritesManager.isFavorite(url: channel.url) ? "heart.fill" : "heart")
                            .font(.title2)
                            .foregroundColor(favoritesManager.isFavorite(url: channel.url) ? .yellow : .white)
                            .padding()
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal)
            }
        }
        .navigationBarHidden(true)
        .onAppear {
            if let url = URL(string: channel.url) {
                let headers: [String: String] = [
                    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
                ]
                let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
                let item = AVPlayerItem(asset: asset)
                self.player = AVPlayer(playerItem: item)
            }
        }
    }
}
