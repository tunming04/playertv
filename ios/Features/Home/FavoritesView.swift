import SwiftUI

struct FavoritesView: View {
    @StateObject private var favoritesManager = FavoritesManager()
    @State private var selectedChannel: Channel? = nil
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.darkBackground.ignoresSafeArea()
                
                VStack(alignment: .leading, spacing: 16) {
                    Text("Kênh Yêu Thích")
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.textPrimary)
                        .padding(.horizontal, 24)
                        .padding(.top, 24)
                    
                    if favoritesManager.favorites.isEmpty {
                        VStack {
                            Spacer()
                            Text("Chưa có kênh yêu thích nào")
                                .foregroundColor(.textSecondary)
                            Spacer()
                        }
                        .frame(maxWidth: .infinity)
                    } else {
                        List(favoritesManager.favorites) { channel in
                            Button(action: {
                                selectedChannel = channel
                            }) {
                                HStack {
                                    if let logoUrl = channel.logo, let url = URL(string: logoUrl) {
                                        AsyncImage(url: url) { phase in
                                            if let image = phase.image {
                                                image.resizable().aspectRatio(contentMode: .fit)
                                            } else {
                                                Color.black
                                            }
                                        }
                                        .frame(width: 40, height: 40)
                                        .cornerRadius(4)
                                    }
                                    VStack(alignment: .leading) {
                                        Text(channel.name)
                                            .font(.body)
                                            .fontWeight(.medium)
                                            .foregroundColor(.textPrimary)
                                        if let group = channel.groupTitle {
                                            Text(group)
                                                .font(.caption)
                                                .foregroundColor(.textSecondary)
                                        }
                                    }
                                }
                            }
                            .listRowBackground(Color.glassSurface)
                        }
                        .scrollContentBackground(.hidden)
                    }
                }
            }
            .navigationDestination(item: $selectedChannel) { channel in
                PlayerView(channel: channel)
            }
        }
        .onAppear {
            // Reload favorites when view appears
            // It automatically happens via @StateObject, but we can force update if needed
        }
    }
}
