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

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    @State private var selectedTab = 0
    
    var body: some View {
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
        // Ensure TabBar is dark to match the theme
        .onAppear {
            let appearance = UITabBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = UIColor(Color.darkBackground)
            UITabBar.appearance().standardAppearance = appearance
            if #available(iOS 15.0, *) {
                UITabBar.appearance().scrollEdgeAppearance = appearance
            }
        }
    }
}
