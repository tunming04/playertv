# Admin Panel

Quản lý danh sách kênh IPTV.

## Cách sá»­ dụng

### 1. With Cloudflare Worker
Truy cập: `https://playertv-app.YOUR_SUBDOMAIN.workers.dev/admin`

### 2. Local (chá»‰ xem)
Má»Ÿ file `index.html` trực tiếp trong browser.

## Tính nÄƒng

- âœ… Thêm kênh má»›i (tên, nhóm, URL)
- âœ… Sửa URL stream
- âœ… Xóa kênh
- âœ… Import tá»« M3U
- âœ… Tìm kiếm kênh
- âœ… Thá»‘ng kê tá»•ng quan

## API Reference

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/admin/channels` | GET | Lấy danh sách kênh |
| `/api/admin/channels` | POST | Thêm kênh má»›i |
| `/api/admin/channels/:id` | PUT | Cập nhật kênh |
| `/api/admin/channels/:id` | DELETE | Xóa kênh |

## Deploy Admin Panel

### Option 1: Cloudflare Pages
```bash
cd admin-panel
wrangler pages deploy . --project-name=app-admin
```

### Option 2: Static Hosting
Upload `index.html` lên bất kỳ static host nào (Netlify, Vercel, GitHub Pages...)

## Security

Thêm Basic Auth cho production:

```javascript
// Trong Cloudflare Worker
const ADMIN_PASSWORD = 'your-secret';

if (url.pathname.includes('/admin')) {
  const auth = request.headers.get('Authorization');
  if (auth !== `Basic ${btoa('admin:' + ADMIN_PASSWORD)}`) {
    return new Response('Unauthorized', { status: 401 });
  }
}
```
