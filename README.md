# PlayerTV

Ứng dụng Player native cho Android (Kotlin) vÃ  iOS (Swift), há»— trá»£ Ä‘ầy Ä‘á»§:
- **M3U/M3U8** - Import playlist tá»« URL hoặc file
- **Xtream Code API** - Login, browse live/VOD/series
- **Stalker Portal** - Há»— trá»£ Stalker middleware
- **EPG** - Electronic Program Guide

## Tính nÄƒng chính

### Phase 1: MVP
- [x] Import M3U/M3U8 tá»« URL vÃ  file
- [x] Channel list vá»›i category filter
- [x] Video player (HLS/DASH)
- [x] Dark/Light theme
- [x] Tiếng Viá»‡t localization

### Phase 2: Advanced
- [x] Xtream Code API support
- [x] Stalker Portal support
- [ ] EPG integration
- [ ] Catchup/Timeshift
- [ ] Playlist management

### Phase 3: Premium
- [ ] Picture-in-Picture
- [ ] Download offline
- [ ] Subtitle support
- [ ] Audio track selection
- [ ] Parental control

## Cấu trúc dá»± án

```
playertv-app/
â”œâ”€â”€ android/          # Android app (Kotlin + Jetpack Compose)
â”‚   â”œâ”€â”€ app/
â”‚   â”‚   â””â”€â”€ src/main/
â”‚   â”‚       â”œâ”€â”€ kotlin/com/iptv/vietnam/
â”‚   â”‚       â”‚   â”œâ”€â”€ data/       # Room DB, API services
â”‚   â”‚       â”‚   â”œâ”€â”€ domain/     # Models
â”‚   â”‚       â”‚   â””â”€â”€ ui/         # Compose UI
â”‚   â”‚       â””â”€â”€ res/            # Resources
â”‚   â””â”€â”€ build.gradle.kts
â”‚
â”œâ”€â”€ ios/              # iOS app (Swift + SwiftUI)
â”‚   â”œâ”€â”€ PlayerTV/
â”‚   â”‚   â”œâ”€â”€ App/              # Entry point
â”‚   â”‚   â”œâ”€â”€ Core/             # Services, Models
â”‚   â”‚   â”œâ”€â”€ Features/         # UI screens
â”‚   â”‚   â””â”€â”€ Components/       # Reusable components
â”‚   â””â”€â”€ PlayerTV.xcodeproj
â”‚
â””â”€â”€ docs/             # Documentation
```

## Technology Stack

### Android
- Kotlin 2.0+
- Jetpack Compose + Material 3
- Hilt (DI)
- Room (Local DB)
- ExoPlayer/Media3 (Player)
- Coil (Image loading)
- Retrofit + OkHttp (Networking)

### iOS
- Swift 5.9+
- SwiftUI
- AVPlayer (Player)
- URLSession (Networking)
- Kingfisher (Image loading)

## Setup

### Android
```bash
cd android
./gradlew assembleDebug
```

### iOS
```bash
cd ios
xcodebuild -scheme PlayerTV -destination 'platform=iOS Simulator,name=iPhone 15'
```

## Há»— trá»£ formats

| Format | Status | Notes |
|--------|--------|-------|
| M3U/M3U8 | âœ… | Full support |
| Xtream Code | âœ… | Auth + Live/VOD/Series |
| Stalker Portal | âœ… | Auth + Live/VOD |
| EPG (XMLTV) | ðŸš§ | In progress |
| Catchup | ðŸ“‹ | Planned |

## Localization

- [x] Tiếng Viá»‡t (default)
- [ ] English

## License

MIT License
