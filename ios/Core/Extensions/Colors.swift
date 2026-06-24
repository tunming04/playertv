import SwiftUI

extension Color {
    // Primary - Yellow Accent (PlayBox style)
    static let yellow = Color(red: 1.0, green: 0.84, blue: 0.0)
    static let yellowDark = Color(red: 0.9, green: 0.78, blue: 0.0)
    
    // Background - Dark
    static let darkBackground = Color(red: 0.1, green: 0.1, blue: 0.18)
    static let darkSurface = Color(red: 0.09, green: 0.13, blue: 0.24)
    static let darkCard = Color(red: 0.06, green: 0.2, blue: 0.38)
    
    // Glass effect colors
    static let glassBackground = Color.white.opacity(0.15)
    static let glassBorder = Color.white.opacity(0.25)
    static let glassSurface = Color.white.opacity(0.08)
    
    // Text colors
    static let textPrimary = Color.white
    static let textSecondary = Color(white: 0.7)
    static let textMuted = Color(white: 0.5)
    
    // Status colors
    static let liveRed = Color(red: 0.9, green: 0.22, blue: 0.21)
    static let successGreen = Color(red: 0.3, green: 0.69, blue: 0.31)
    
    // Category card gradients
    static let liveTvGradient1 = Color(red: 0.1, green: 0.32, blue: 0.46)
    static let liveTvGradient2 = Color(red: 0.05, green: 0.23, blue: 0.37)
    static let moviesGradient1 = Color(red: 0.1, green: 0.24, blue: 0.2)
    static let moviesGradient2 = Color(red: 0.05, green: 0.16, blue: 0.13)
    static let seriesGradient1 = Color(red: 0.17, green: 0.1, blue: 0.24)
    static let seriesGradient2 = Color(red: 0.1, green: 0.05, blue: 0.16)
}
