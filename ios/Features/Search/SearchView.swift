import SwiftUI

struct SearchView: View {
    @State private var searchText = ""
    
    var body: some View {
        NavigationView {
            ZStack {
                Color.darkBackground
                    .ignoresSafeArea()
                
                VStack(spacing: 16) {
                    // Search bar
                    HStack {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.textMuted)
                        
                        TextField("Tìm kiếm kênh...", text: $searchText)
                            .foregroundColor(.textPrimary)
                    }
                    .padding()
                    .background(Color.glassBackground)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal)
                    
                    if searchText.isEmpty {
                        Spacer()
                        
                        VStack(spacing: 16) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 48))
                                .foregroundColor(.textMuted)
                            
                            Text("Tìm kiếm kênh")
                                .font(.headline)
                                .foregroundColor(.textSecondary)
                            
                            Text("Nhập tên kênh để tìm kiếm")
                                .font(.subheadline)
                                .foregroundColor(.textMuted)
                        }
                        
                        Spacer()
                    } else {
                        // Search results
                        List {
                            ForEach(0..<5) { _ in
                                HStack {
                                    Circle()
                                        .fill(Color.glassSurface)
                                        .frame(width: 48, height: 48)
                                    
                                    VStack(alignment: .leading) {
                                        Text("Kênh mẫu")
                                            .foregroundColor(.textPrimary)
                                        Text("Nhóm")
                                            .font(.caption)
                                            .foregroundColor(.textSecondary)
                                    }
                                    
                                    Spacer()
                                }
                                .listRowBackground(Color.glassBackground)
                            }
                        }
                        .listStyle(.plain)
                    }
                }
                .navigationTitle("Tìm kiếm")
                .navigationBarTitleDisplayMode(.inline)
            }
        }
    }
}

#Preview {
    SearchView()
}
