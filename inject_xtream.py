import re

with open('streams_dump.txt', 'r', encoding='utf-8') as f:
    lines = f.read().strip().split(',\n')

new_channels = []
for line in lines:
    if not line: continue
    # Parse ["Name", "Group", "URL"]
    parts = line.strip('[]').split('", "')
    if len(parts) == 3:
        name = parts[0].strip(' "')
        group = parts[1].strip(' "')
        url = parts[2].strip(' "')
        new_channels.append(f'    {{ name: "{name}", group: "XTREAM {group}", url: "{url}" }}')

with open('cloudflare-worker/index.js', 'r', encoding='utf-8') as f:
    content = f.read()

inject_str = ",\n".join(new_channels) + ",\n"

# Replace inside channels: [ ... ]
# We will just append them after the first '['
new_content = re.sub(r'channels:\s*\[\s*', 'channels: [\n' + inject_str, content, count=1)

with open('cloudflare-worker/index.js', 'w', encoding='utf-8') as f:
    f.write(new_content)

print(f"Injected {len(new_channels)} streams!")
