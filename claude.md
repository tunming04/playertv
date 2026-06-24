# IPTV Player Project - Security & Encryption Architecture

Tài liệu này (`claude.md`) mô tả chi tiết cơ chế bảo mật và mã hóa (encryption/obfuscation) được áp dụng trên toàn bộ hệ thống IPTV Player, bao gồm Admin Panel, Cloudflare Worker, ứng dụng Android và ứng dụng iOS.

Mục tiêu chính: **Không để lộ link IPTV gốc (m3u8) dưới dạng plaintext trong mã nguồn ứng dụng, trên đường truyền (một phần), và khi lưu trữ tại thiết bị người dùng.**

---

## 1. Cloudflare Worker & Admin Panel
**Thư mục:** `cloudflare-worker/` và `admin-panel/`

### 1.1. Xác thực API (Time-based Token)
Ứng dụng không gọi API lấy danh sách kênh một cách công khai. Mọi request tới `/api/playlist` hoặc `/api/channels` đều yêu cầu một `token`.
- **Cơ chế:** Token là mã băm `SHA-256` của chuỗi `<APP_SECRET>:<hour>`.
- **Bảo mật:** Tránh bị replay attack diện rộng vì token sẽ tự động hết hạn sau mỗi giờ. Người dùng lấy được link API cũng không thể dùng lại vào ngày hôm sau.

### 1.2. Mã hóa đường truyền (Transit Obfuscation)
Khi Worker trả về JSON danh sách kênh, trường `url` của từng kênh không được để dạng `http://...`.
- Worker tự động obfuscate (làm mờ) URL bằng thuật toán Base64 (`btoa`).
- Ứng dụng client khi nhận được JSON sẽ tự động parse (có fallback xử lý Base64/M3U).

---

## 2. Ứng dụng Android (Kotlin)
**Thư mục:** `android/`

### 2.1. Không Hardcode (Zero Hardcoding)
Toàn bộ mảng `Constants.DEFAULT_CHANNELS` đã bị xóa bỏ. Ứng dụng mặc định khởi chạy bằng một `LaunchedEffect` để gọi API lấy danh sách từ Cloudflare Worker.

### 2.2. Mã hóa dữ liệu lưu trữ (AES Encryption at Rest)
Bảo vệ link do người dùng nhập (User Input) hoặc link từ Admin Panel khi lưu vào **Kênh Yêu Thích (Favorites)**:
- **Thuật toán:** AES (Advanced Encryption Standard).
- **Vị trí xử lý:** `Constants.encryptUrl()` và `Constants.decryptUrl()`.
- **Lưu trữ:** Trong `SharedPreferences` (JSON thông qua Gson). Trước khi chuỗi JSON được ghi xuống disk, tất cả các thuộc tính `url` của object `Channel` đều bị mã hóa thành chuỗi Base64-AES lộn xộn. Kẻ gian dù có root máy mở file XML Preferences cũng không lấy được link gốc.

---

## 3. Ứng dụng iOS (Swift)
**Thư mục:** `ios/`

### 3.1. Không Hardcode (Zero Hardcoding)
Mảng `channels` trong `HomeView.swift` được khởi tạo rỗng `[]`. Khi View xuất hiện (`.task`), ứng dụng tự động tính toán mã SHA-256 để fetch API playlist an toàn.

### 3.2. Mã hóa dữ liệu lưu trữ (AES-GCM Encryption at Rest)
Tương tự Android, iOS bảo mật dữ liệu lưu trữ (Kênh Yêu Thích) ở mức cao nhất:
- **Thuật toán:** AES-GCM (Galois/Counter Mode) cung cấp bởi thư viện `CryptoKit` của Apple.
- **Vị trí xử lý:** `FavoritesManager.swift`.
- **Lưu trữ:** Lưu vào `UserDefaults` dưới dạng mảng JSON. Trước khi encode bằng `JSONEncoder`, trường `url` được niêm phong (seal) bằng một khóa đối xứng (`SymmetricKey`) tạo ra từ `APP_SECRET`. Bất kỳ link nào người dùng dán vào ô Text Input, nếu bấm "Yêu thích", đều sẽ được mã hóa chuẩn hóa này trước khi ghi vào máy.

---

## Tóm tắt Luồng Xử Lý Link (User Input & Admin List)
1. **Admin Panel:** Admin nhập `http://example/phim.m3u8` -> Lưu trữ tại Cloudflare KV.
2. **Worker API:** Fetch KV -> Base64 Obfuscate -> Trả về Client.
3. **App Fetch:** Client nhận JSON -> Decode Base64 -> Nạp vào RAM (Memory) để play.
4. **User Input:** Người dùng tự copy một link `http://custom/phim.m3u8` dán vào ô TextField -> Nạp vào RAM để play.
5. **Lưu Yêu Thích (Save to Favorites):** Nhấn nút ❤️ -> Link trong RAM (cả Admin lẫn User Input) bị mã hóa AES (Android) hoặc AES-GCM (iOS) -> Ghi xuống bộ nhớ đệm (Preferences/UserDefaults).
6. **Load Yêu Thích:** Đọc từ ổ cứng -> Giải mã AES -> Trả về RAM để play.

Mô hình này đảm bảo sự cân bằng giữa **Tốc độ (nhập link là xem ngay)** và **Bảo mật (link luôn được mã hóa khi nằm yên trên ổ cứng)**.
