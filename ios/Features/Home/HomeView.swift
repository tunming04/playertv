import SwiftUI
import AVKit

struct HomeView: View {
    @State private var channels: [Channel] = []
    @State private var filteredChannels: [Channel] = []
    @State private var epgMapping: [String: EpgProgram] = [:]
    @State private var isLoading: Bool = false
    @State private var searchQuery: String = ""
    @State private var selectedGroup: String = "Tất cả"
    @State private var playingChannel: Channel? = nil
    
    var groups: [String] {
        var set = Set(channels.compactMap { $0.groupTitle }.filter { !$0.isEmpty })
        var sorted = Array(set).sorted()
        sorted.insert("Tất cả", at: 0)
        return sorted
    }
    
    // Player states
    @State private var player: AVPlayer = AVPlayer()
    @State private var showControls: Bool = false
    @State private var isMuted: Bool = false
    @State private var showDrawer: Bool = false
    @State private var isFullScreen: Bool = false
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.darkBackground.ignoresSafeArea()
                
                VStack(spacing: 0) {
                    // 1. Logo - TĂ¬m Kiáº¿m
                    HStack {
                        Image("app_logo_new")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 40, height: 40)
                            .cornerRadius(8)
                            
                        HStack {
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.textMuted)
                            TextField("Tìm kiếm kênh...", text: $searchQuery)
                                .foregroundColor(.textPrimary)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                                .onChange(of: searchQuery) { _ in
                                    filterChannels()
                                }
                        }
                        .padding(12)
                        .background(Color.glassSurface)
                        .cornerRadius(12)
                        .padding(.leading, 8)
                    }
                    .padding()
                    
                    // 2. Báº¡n Ä‘ang xem kĂªnh
                    if let playing = playingChannel {
                        HStack {
                            Text("Bạn đang xem kênh: \(playing.name)")
                                .font(.headline)
                                .foregroundColor(.yellow)
                            Spacer()
                        }
                        .padding(.horizontal)
                        .padding(.bottom, 8)
                    }
                    
                    // 3. Videoplayer Ä‘ang phĂ¡t
                    ZStack(alignment: .topTrailing) {
                        CustomVideoPlayer(player: player)
                            .aspectRatio(16/9, contentMode: .fit)
                            .background(Color.black)
                            .onTapGesture {
                                withAnimation {
                                    showControls.toggle()
                                    showDrawer = false
                                }
                                if showControls {
                                    hideControlsAfterDelay()
                                }
                            }
                        
                        // Overlay Controls
                        if showControls {
                            VStack {
                                // Top Bar
                                HStack {
                                    Spacer()
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
                                        withAnimation {
                                            showDrawer.toggle()
                                        }
                                    }) {
                                        Image(systemName: "list.bullet")
                                            .foregroundColor(.white)
                                            .padding(12)
                                            .background(Color.black.opacity(0.5))
                                            .clipShape(Circle())
                                    }
                                }
                                .padding(8)
                                
                                Spacer()
                                
                                // Bottom Bar
                                HStack {
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
                                    Spacer()
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
                            }
                            .background(LinearGradient(gradient: Gradient(colors: [Color.black.opacity(0.7), Color.clear, Color.black.opacity(0.7)]), startPoint: .top, endPoint: .bottom))
                        }
                        
                        // Drawer Overlay
                        if showDrawer {
                            HStack {
                                Spacer()
                                VStack {
                                    HStack {
                                        Text("Danh sách kênh")
                                            .font(.headline)
                                            .foregroundColor(.yellow)
                                        Spacer()
                                    }
                                    .padding()
                                    
                                    ScrollView {
                                        LazyVStack {
                                            ForEach(filteredChannels) { channel in
                                                Button(action: {
                                                    playingChannel = channel
                                                    withAnimation {
                                                        showDrawer = false
                                                        showControls = false
                                                    }
                                                }) {
                                                    HStack {
                                                        Text(channel.name)
                                                            .font(.subheadline)
                                                            .foregroundColor(playingChannel?.id == channel.id ? .yellow : .white)
                                                        Spacer()
                                                    }
                                                    .padding(.horizontal)
                                                    .padding(.vertical, 8)
                                                }
                                            }
                                        }
                                    }
                                }
                                .frame(width: UIScreen.main.bounds.width * 0.5)
                                .background(Color.black.opacity(0.85))
                            }
                            .transition(.move(edge: .trailing))
                        }
                    }
                    
                    // 4. List kênh chia cột và Group filter
                    if isLoading {
                        Spacer()
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .yellow))
                        Spacer()
                    } else {
                        if groups.count > 1 {
                            ScrollView(.horizontal, showsIndicators: false) {
                                LazyHStack(spacing: 8) {
                                    ForEach(groups, id: \.self) { group in
                                        Button(action: {
                                            selectedGroup = group
                                            filterChannels()
                                        }) {
                                            Text(group)
                                                .font(.subheadline)
                                                .padding(.horizontal, 16)
                                                .padding(.vertical, 8)
                                                .background(selectedGroup == group ? Color.yellow : Color.darkSurface)
                                                .foregroundColor(selectedGroup == group ? .darkBackground : .textPrimary)
                                                .cornerRadius(20)
                                                .overlay(
                                                    RoundedRectangle(cornerRadius: 20)
                                                        .stroke(selectedGroup == group ? Color.yellow : Color.clear, lineWidth: 1)
                                                )
                                        }
                                    }
                                }
                                .padding(.horizontal, 16)
                                .padding(.bottom, 8)
                            }
                        }
                        
                        ScrollView {
                            LazyVGrid(
                                columns: Array(repeating: GridItem(.flexible(), spacing: 12), count: 3),
                                spacing: 12
                            ) {
                                ForEach(filteredChannels) { channel in
                                    Button(action: {
                                        playingChannel = channel
                                    }) {
                                        ChannelCardView(
                                            channel: channel,
                                            epg: channel.tvgId.flatMap { epgMapping[$0] },
                                            isPlaying: playingChannel?.id == channel.id
                                        )
                                    }
                                    .buttonStyle(PlainButtonStyle())
                                }
                            }
                            .padding(.horizontal, 16)
                            .padding(.top, 16)
                            .padding(.bottom, 24)
                        }
                    }
                }
            }
            .navigationBarHidden(true)
            .fullScreenCover(isPresented: $isFullScreen) {
                if let playingChannel = playingChannel {
                    PlayerView(channel: playingChannel)
                        .onAppear {
                            AppDelegate.orientationLock = .landscape
                            UIDevice.current.setValue(UIInterfaceOrientation.landscapeRight.rawValue, forKey: "orientation")
                            UIViewController.attemptRotationToDeviceOrientation()
                        }
                        .onDisappear {
                            AppDelegate.orientationLock = .portrait
                            UIDevice.current.setValue(UIInterfaceOrientation.portrait.rawValue, forKey: "orientation")
                            UIViewController.attemptRotationToDeviceOrientation()
                        }
                }
            }
            .onChange(of: playingChannel) { newChannel in
                if let newChannel = newChannel, let url = URL(string: newChannel.url) {
                    let headers: [String: String] = [
                        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
                    ]
                    let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
                    let item = AVPlayerItem(asset: asset)
                    player.replaceCurrentItem(with: item)
                    player.play()
                }
            }
            .task {
                isLoading = true
                do {
                    let adminUrl = "https://playertv-app.buoisangvatoi.workers.dev/api/channels"
                    guard let url = URL(string: adminUrl) else { return }
                    let (data, _) = try await URLSession.shared.data(from: url)
                    
                    let parsedChannels = await Task.detached { () -> [Channel] in
                        var channels: [Channel] = []
                        if let channelsArray = try? JSONSerialization.jsonObject(with: data, options: []) as? [[String: Any]] {
                            for c in channelsArray {
                                if let name = c["name"] as? String,
                                   let encodedUrlString = c["url"] as? String {
                                    var decodedUrlString = encodedUrlString
                                    if let decodedData = Data(base64Encoded: encodedUrlString),
                                       let decodedString = String(data: decodedData, encoding: .utf8) {
                                        decodedUrlString = decodedString
                                    }
                                    let group = c["group"] as? String
                                    let logo = c["logo"] as? String
                                    channels.append(Channel(id: String(name.hashValue), name: name, url: decodedUrlString, logo: logo, groupTitle: group))
                                }
                            }
                        }
                        return channels
                    }.value
                    
                    self.channels = parsedChannels
                    self.filteredChannels = parsedChannels
                    if let first = parsedChannels.first {
                        self.playingChannel = first
                    }
                } catch {
                    print("Failed to fetch admin playlist: \(error)")
                }
                isLoading = false
            }
        }
    }
    
    private func filterChannels() {
        var result = channels
        if selectedGroup != "Tất cả" {
            result = result.filter { $0.groupTitle == selectedGroup }
        }
        
        if searchQuery.isEmpty {
            filteredChannels = result
        } else {
            filteredChannels = result.filter {
                $0.name.localizedCaseInsensitiveContains(searchQuery) ||
                ($0.groupTitle?.localizedCaseInsensitiveContains(searchQuery) == true)
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

struct CustomVideoPlayer: UIViewRepresentable {
    var player: AVPlayer
    
    func makeUIView(context: Context) -> PlayerUIView {
        let view = PlayerUIView()
        view.player = player
        return view
    }
    
    func updateUIView(_ uiView: PlayerUIView, context: Context) {
        uiView.player = player
    }
}

class PlayerUIView: UIView {
    var player: AVPlayer? {
        get { playerLayer.player }
        set { playerLayer.player = newValue }
    }
    
    var playerLayer: AVPlayerLayer {
        return layer as! AVPlayerLayer
    }
    
    override class var layerClass: AnyClass {
        return AVPlayerLayer.self
    }
}

struct ChannelCardView: View {
    let channel: Channel
    let epg: EpgProgram?
    let isPlaying: Bool
    
    var progress: Double {
        guard let epg = epg else { return 0 }
        let now = Date().timeIntervalSince1970
        let start = epg.startTime.timeIntervalSince1970
        let end = epg.endTime.timeIntervalSince1970
        if now >= end { return 1.0 }
        if now <= start { return 0.0 }
        return (now - start) / (end - start)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top) {
                if let logoUrl = channel.logo, let url = URL(string: logoUrl) {
                    AsyncImage(url: url) { phase in
                        if let image = phase.image {
                            image.resizable().aspectRatio(contentMode: .fit)
                        } else {
                            Color.white.opacity(0.1)
                        }
                    }
                    .frame(width: 36, height: 36)
                    .cornerRadius(8)
                    .background(Color.white.opacity(0.1).cornerRadius(8))
                } else {
                    ZStack {
                        Color.white.opacity(0.05).cornerRadius(8)
                        Image(systemName: "tv")
                            .foregroundColor(.textMuted)
                    }
                    .frame(width: 36, height: 36)
                }
                
                Spacer()
            }
            .padding(12)
            
            Spacer()
            
            VStack(alignment: .leading, spacing: 4) {
                Text(channel.name)
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(isPlaying ? .yellow : .textPrimary)
                    .lineLimit(1)
                
                if let epg = epg {
                    Text(epg.title)
                        .font(.caption)
                        .foregroundColor(.textSecondary)
                        .lineLimit(1)
                    
                    GeometryReader { geometry in
                        ZStack(alignment: .leading) {
                            RoundedRectangle(cornerRadius: 2)
                                .fill(Color.white.opacity(0.1))
                                .frame(height: 4)
                            RoundedRectangle(cornerRadius: 2)
                                .fill(Color.yellow)
                                .frame(width: geometry.size.width * CGFloat(progress), height: 4)
                        }
                    }
                    .frame(height: 4)
                    .padding(.top, 2)
                } else {
                    Text("Không có thông tin EPG")
                        .font(.caption)
                        .foregroundColor(.textMuted)
                        .lineLimit(1)
                }
            }
            .padding(8)
        }
        .frame(height: 110)
        .background(isPlaying ? Color.yellow.opacity(0.1) : Color.darkSurface)
        .cornerRadius(12)
    }
}
