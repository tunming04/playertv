import urllib.request
import json
import urllib.error
import urllib.parse
from urllib.error import URLError, HTTPError
import ssl

ssl._create_default_https_context = ssl._create_unverified_context

hosts = [
    ("http://primestreams.tv:826", "anto.j", "c9yJDcXyPe"),
    ("http://falcontv.top", "0990183718", "6298570159"),
    ("http://wickediptv.xyz", "Randall123", "Randall321"),
    ("http://wx78.vpn-cloud.icu", "p3v23bcwj2", "j9iyghyczo"),
    ("http://10k.lucastv.pro", "2c99755ae255", "gJnMAT2"),
    ("http://802017341873.plus-26.com", "w4gzmeoiw6", "kyul5gyvkp")
]

working_streams = []

def test_host(host, user, pwd):
    base_url = f"{host}/player_api.php?username={user}&password={pwd}"
    try:
        req = urllib.request.Request(base_url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=10) as response:
            data = json.loads(response.read().decode())
            if 'user_info' in data and data['user_info'].get('status') == 'Active':
                print(f"[+] SUCCESS: {host}")
                
                # Get categories
                cat_url = f"{base_url}&action=get_live_categories"
                cat_req = urllib.request.Request(cat_url, headers={'User-Agent': 'Mozilla/5.0'})
                with urllib.request.urlopen(cat_req, timeout=10) as cat_res:
                    categories = json.loads(cat_res.read().decode())
                    
                    # Filter for Vietnam/Sports/News
                    vn_cats = []
                    for c in categories:
                        name = c.get('category_name', '').lower()
                        if 'vn' in name or 'viet' in name or 'vtv' in name or 'sport' in name or 'news' in name:
                            vn_cats.append(c)
                    
                    if not vn_cats and categories:
                        # Fallback to first few categories if no VN found
                        vn_cats = categories[:2]
                        
                    print(f"    Found {len(vn_cats)} relevant categories.")
                    
                    # Get streams for those categories
                    for cat in vn_cats:
                        cat_id = cat.get('category_id')
                        cat_name = cat.get('category_name')
                        str_url = f"{base_url}&action=get_live_streams&category_id={cat_id}"
                        str_req = urllib.request.Request(str_url, headers={'User-Agent': 'Mozilla/5.0'})
                        with urllib.request.urlopen(str_req, timeout=10) as str_res:
                            streams = json.loads(str_res.read().decode())
                            for s in streams[:20]: # Take up to 20 streams per category
                                stream_id = s.get('stream_id')
                                stream_name = s.get('name')
                                stream_icon = s.get('stream_icon') or "null"
                                if stream_icon != "null":
                                    stream_icon = f'"{stream_icon}"'
                                
                                play_url = f"{host}/live/{user}/{pwd}/{stream_id}.m3u8"
                                working_streams.append(f'["{stream_name}", "{cat_name}", "{play_url}"]')
                                
            else:
                print(f"[-] FAILED (Inactive): {host}")
    except Exception as e:
        print(f"[-] FAILED (Error): {host} - {str(e)}")

for h in hosts:
    test_host(h[0], h[1], h[2])

print(f"\nFound {len(working_streams)} working streams!")
if working_streams:
    with open("streams_dump.txt", "w", encoding="utf-8") as f:
        f.write(",\n".join(working_streams))
