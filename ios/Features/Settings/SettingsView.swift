import SwiftUI

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @State private var showAddPlaylist = false
    
    var body: some View {
        NavigationView {
            ZStack {
                Color.darkBackground
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 16) {
                        // Playlist Management
                        Section {
                            ForEach(viewModel.playlists) { playlist in
                                PlaylistRow(playlist: playlist, onDelete: {
                                    viewModel.deletePlaylist(playlist)
                                }, onRefresh: {
                                    viewModel.refreshPlaylist(playlist)
                                })
                            }
                            
                            Button(action: { showAddPlaylist = true }) {
                                HStack {
                                    Image(systemName: "plus.circle.fill")
                                        .foregroundColor(.yellow)
                                    
                                    Text("Thêm danh sách phát")
                                        .foregroundColor(.textPrimary)
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.glassBackground)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                            }
                        } header: {
                            SectionHeader(title: "Quản lý danh sách phát")
                        }
                        
                        // Theme
                        Section {
                            SettingsRow(icon: "moon.fill", title: "Giao diện tối", subtitle: "Đang sử dụng")
                        } header: {
                            SectionHeader(title: "Giao diện")
                        }
                        
                        // About
                        Section {
                            SettingsRow(icon: "info.circle.fill", title: "PlayerTV", subtitle: "Phiên bản 1.0.0")
                        } header: {
                            SectionHeader(title: "Về ứng dụng")
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle("Cài đặt")
            .sheet(isPresented: $showAddPlaylist) {
                AddPlaylistView()
            }
        }
    }
}

struct SectionHeader: View {
    let title: String
    
    var body: some View {
        HStack {
            Text(title)
                .font(.subheadline)
                .fontWeight(.medium)
                .foregroundColor(.yellow)
            
            Spacer()
        }
    }
}

struct PlaylistRow: View {
    let playlist: Playlist
    let onDelete: () -> Void
    let onRefresh: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: playlistIcon)
                .foregroundColor(.yellow)
                .frame(width: 24, height: 24)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(playlist.name)
                    .font(.body)
                    .fontWeight(.medium)
                    .foregroundColor(.textPrimary)
                
                Text("\(playlist.channelCount) kênh • \(playlist.type.rawValue.uppercased())")
                    .font(.caption)
                    .foregroundColor(.textSecondary)
            }
            
            Spacer()
            
            Menu {
                Button(action: onRefresh) {
                    Label("Làm mới", systemImage: "arrow.clockwise")
                }
                
                Button(role: .destructive, action: onDelete) {
                    Label("Xóa", systemImage: "trash")
                }
            } label: {
                Image(systemName: "ellipsis.circle")
                    .foregroundColor(.textSecondary)
            }
        }
        .padding()
        .background(Color.glassBackground)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
    
    private var playlistIcon: String {
        switch playlist.type {
        case .m3u, .m3uFile:
            return "list.bullet"
        case .xtream:
            return "tv"
        case .stalker:
            return "gearshape"
        }
    }
}

struct SettingsRow: View {
    let icon: String
    let title: String
    let subtitle: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(.yellow)
                .frame(width: 24, height: 24)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.body)
                    .foregroundColor(.textPrimary)
                
                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.textSecondary)
            }
            
            Spacer()
        }
        .padding()
        .background(Color.glassBackground)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    SettingsView()
}
