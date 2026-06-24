import SwiftUI
import UIKit

class AppDelegate: NSObject, UIApplicationDelegate {
    static var orientationLock = UIInterfaceOrientationMask.portrait
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return AppDelegate.orientationLock
    }
}

@main
struct PlayerTVApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @StateObject private var playerManager = PlayerManager()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(playerManager)
        }
    }
}

struct ContentView: View {
    @EnvironmentObject var playerManager: PlayerManager
    @State private var selectedTab = 0
    
    var body: some View {
        ZStack(alignment: .bottom) {
            TabView(selection: $selectedTab) {
                HomeView()
                    .tag(0)
                    .tabItem {
                        Image(systemName: "house.fill")
                        Text("Home")
                    }
                
                FavoritesView()
                    .tag(1)
                    .tabItem {
                        Image(systemName: "heart.fill")
                        Text("Yêu thích")
                    }
                    
                AddPlaylistView()
                    .tag(2)
                    .tabItem {
                        Image(systemName: "plus.circle.fill")
                        Text("Nhập")
                    }
                    
                SettingsView()
                    .tag(3)
                    .tabItem {
                        Image(systemName: "person.fill")
                        Text("Cá nhân")
                    }
            }
            .tint(.yellow)
            .onAppear {
                let appearance = UITabBarAppearance()
                appearance.configureWithOpaqueBackground()
                appearance.backgroundColor = UIColor(Color.darkBackground)
                UITabBar.appearance().standardAppearance = appearance
                if #available(iOS 15.0, *) {
                    UITabBar.appearance().scrollEdgeAppearance = appearance
                }
            }
            
            // Mini-player khi đang play và không ở Home tab
            if playerManager.currentChannel != nil && playerManager.isPlaying && selectedTab != 0 {
                MiniPlayerView()
                    .environmentObject(playerManager)
                    .padding(.horizontal, 12)
                    .padding(.bottom, 4)
            }
        }
    }
}

struct MiniPlayerView: View {
    @EnvironmentObject var playerManager: PlayerManager
    
    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.yellow.opacity(0.15))
                    .frame(width: 36, height: 36)
                Image(systemName: "play.fill")
                    .foregroundColor(.yellow)
                    .font(.system(size: 14))
            }
            
            VStack(alignment: .leading, spacing: 2) {
                Text("Đang phát: \(playerManager.currentChannel?.name ?? "")")
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .foregroundColor(.white)
                    .lineLimit(1)
            }
            
            Spacer()
            
            Image(systemName: "chevron.up")
                .foregroundColor(.textSecondary)
                .font(.caption)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .background(Color.darkSurface)
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.3), radius: 4, y: 2)
    }
}
