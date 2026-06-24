# Cloudflare Worker - Playlist Server

Host default M3U playlist trên Cloudflare Workers.

## Setup

### 1. Install Wrangler CLI
```bash
npm install -g wrangler
```

### 2. Login Cloudflare
```bash
wrangler login
```

### 3. Tạo KV Namespace (Ä‘á»ƒ store playlist)
```bash
wrangler kv:namespace create "PLAYLIST"
```

Copy ID tá»« output vÃ  cập nhật vào `wrangler.toml`:
```toml
kv_namespaces = [
  { binding = "KV", id = "YOUR_KV_NAMESPACE_ID" }
]
```

### 4. Deploy
```bash
cd cloudflare-worker
wrangler deploy
```

Worker sẽ available tại: `https://playertv-app.YOUR_SUBDOMAIN.workers.dev`

## API Endpoints

### Public Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Download M3U file |
| `/playlist.m3u` | GET | Download M3U file |
| `/api/playlist` | GET | Get playlist JSON |
| `/api/channels` | GET | Get channels list |

### Admin Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/admin` | GET | Admin panel UI |
| `/api/admin/channels` | GET | Get all channels (with IDs) |
| `/api/admin/channels` | POST | Add new channel |
| `/api/admin/channels/:id` | PUT | Update channel |
| `/api/admin/channels/:id` | DELETE | Delete channel |

## Admin Panel

Truy cập `/admin` Ä‘á»ƒ quản lý channels:
- Thêm kênh má»›i
- Sửa URL stream
- Xóa kênh

## App Integration

Trong app, dùng URL Ä‘á»ƒ import default playlist:
```
https://playertv-app.YOUR_SUBDOMAIN.workers.dev/playlist.m3u
```

Hoặc fetch JSON:
```
https://playertv-app.YOUR_SUBDOMAIN.workers.dev/api/playlist
```

## Development

```bash
# Local development
wrangler dev

# Test locally
curl http://localhost:8787/playlist.m3u
curl http://localhost:8787/api/channels
```

## Security (Optional)

Thêm basic auth cho admin endpoints:

```javascript
// Trong index.js, thêm check:
const ADMIN_PASSWORD = env.ADMIN_PASSWORD || 'your-secret-password';

// Check auth cho admin endpoints
if (url.pathname.startsWith('/api/admin') || url.pathname === '/admin') {
  const auth = request.headers.get('Authorization');
  if (auth !== `Basic ${btoa('admin:' + ADMIN_PASSWORD)}`) {
    return new Response('Unauthorized', { 
      status: 401,
      headers: { 'WWW-Authenticate': 'Basic realm="Admin"' }
    });
  }
}
```

## Default Playlist

Khi chưa có data trong KV, worker sẽ serve default playlist (24 kênh VTV tá»« FPT).
