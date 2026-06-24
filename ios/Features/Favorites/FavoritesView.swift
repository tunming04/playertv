import SwiftUI

struct FavoritesView: View {
    var body: some View {
        NavigationView {
            ZStack {
                Color.darkBackground
                    .ignoresSafeArea()
                
                VStack {
                    Text("Yêu thích")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.textPrimary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding()
                    
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Image(systemName: "heart.slash")
                            .font(.system(size: 48))
                            .foregroundColor(.textMuted)
                        
                        Text("Chưa có kênh yêu thích")
                            .font(.headline)
                            .foregroundColor(.textSecondary)
                        
                        Text("Thêm kênh vào yêu thích để xem nhanh")
                            .font(.subheadline)
                            .foregroundColor(.textMuted)
                    }
                    
                    Spacer()
                }
            }
        }
    }
}

#Preview {
    FavoritesView()
}
