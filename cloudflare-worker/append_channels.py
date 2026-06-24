import re

new_channels = [
  {"name": "VTV1 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv1/live247-hls-avc/index.m3u8"},
  {"name": "VTV2 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv2/live247-hls-avc/index.m3u8"},
  {"name": "VTV3 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv3/live247-hls-avc/index.m3u8"},
  {"name": "VTV4 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv4/live247-hls-avc/index.m3u8"},
  {"name": "VTV5 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv5/live247-hls-avc/index.m3u8"},
  {"name": "VTV6 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv6/live247-hls-avc/index.m3u8"},
  {"name": "VTV7 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv7/live247-hls-avc/index.m3u8"},
  {"name": "VTV8 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv8/live247-hls-avc/index.m3u8"},
  {"name": "VTV9 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv9/live247-hls-avc/index.m3u8"},
  {"name": "VTV10 FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv10/live247-hls-avc/index.m3u8"},
  {"name": "VTV5TN FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv5tn/live-hls-avc/index.m3u8"},
  {"name": "VTV5TNB FHD (ALT)","group": "FPT [ALT]","url": "https://live-a.fptplay53.net/live/media/vtv5tnb/live-hls-avc/index.m3u8"},
  {"name": "VTV1 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv1/live247-hls-avc/index.m3u8"},
  {"name": "VTV2 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv2/live247-hls-avc/index.m3u8"},
  {"name": "VTV3 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv3/live247-hls-avc/index.m3u8"},
  {"name": "VTV4 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv4/live247-hls-avc/index.m3u8"},
  {"name": "VTV5 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv5/live247-hls-avc/index.m3u8"},
  {"name": "VTV6 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv6/live247-hls-avc/index.m3u8"},
  {"name": "VTV7 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv7/live247-hls-avc/index.m3u8"},
  {"name": "VTV8 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv8/live247-hls-avc/index.m3u8"},
  {"name": "VTV9 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv9/live247-hls-avc/index.m3u8"},
  {"name": "VTV10 FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv10/live247-hls-avc/index.m3u8"},
  {"name": "VTV5TN FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv5tn/live247-hls-avc/index.m3u8"},
  {"name": "VTV5TNB FHD (Vip)","group": "FPT [VIP]","url": "https://vips-livecdn.fptplay.net/live/media/vtv5tnb/live247-hls-avc/index.m3u8"},
]

with open('index.js', 'r', encoding='utf-8') as f:
    content = f.read()

# Build the string to inject
inject_str = ""
for c in new_channels:
    inject_str += f'    {{ name: "{c["name"]}", group: "{c["group"]}", url: "{c["url"]}" }},\n'

# Find channels: [ and inject right after
new_content = re.sub(r'channels:\s*\[\s*', 'channels: [\n' + inject_str, content, count=1)

with open('index.js', 'w', encoding='utf-8') as f:
    f.write(new_content)

print("Injected!")
