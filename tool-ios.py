#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RapPhim iOS Tools B — quản lý dự án native iOS (Swift / SwiftUI).

Repo:    https://github.com/tunming04/RPIOS
Backend: https://rapphim.online (giữ nguyên — không tool nào ở đây sửa BE)
Build:   GitHub Actions (macOS runner) — Windows chỉ edit + git + trigger.

Chức năng:
  - Git: status / push / pull / log / push-full / reset-init
  - GitHub: list workflows + runs + upload secrets + trigger build
  - Version: đọc/set MARKETING_VERSION trong project.yml
  - Validate: project.yml syntax + Info.plist required keys + GoogleService-Info
  - XcodeGen: in lệnh regen project (chỉ chạy được trên Mac)
  - Release: tạo CHANGELOG.md từ git log
"""
from __future__ import annotations

import io
import json
import os
import plistlib
import re
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

if sys.platform == "win32":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", errors="replace")

try:
    import requests
except ImportError:
    print("⚠️  Cần cài: pip install requests")
    sys.exit(1)

# ════════════════════════════════════════════════════════════════════════════
#  CẤU HÌNH
# ════════════════════════════════════════════════════════════════════════════

PROJECT_DIR = Path(__file__).resolve().parent
PROJECT_YML = PROJECT_DIR / "project.yml"
INFO_PLIST = PROJECT_DIR / "Resources" / "Info.plist"
ENTITLEMENTS = PROJECT_DIR / "Resources" / "RapPhim.entitlements"
GOOGLE_SERVICES = PROJECT_DIR / "Resources" / "GoogleService-Info.plist"

# GitHub
OWNER = "tunming04"
REPO = "playertv"
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN", "").strip()
GITHUB_API = "https://api.github.com"
DEFAULT_BRANCH = "main"
DEFAULT_WORKFLOW = "ios-build.yml"

# iOS bundle
BUNDLE_ID = "com.rapphim.mobile"

GIT_EXCLUDED = [
    "MOBILE_SYNC_NOTES.md",
    "SYSTEM_DESIGN.md",
    "tool.py",
    "tool-ios.py",
    "tool-ios-b.py",
    "docs/",
    "build/",
    "DerivedData/",
    "RapPhim.xcodeproj/",
    ".build/",
    ".swiftpm/",
    "Package.resolved",
    "*.ipa",
    "*.xcarchive",
    "_check_repo.py",
]


# ════════════════════════════════════════════════════════════════════════════
#  UTILS
# ════════════════════════════════════════════════════════════════════════════

def run(cmd, cwd: Path | None = None, capture: bool = True) -> tuple[bool, str, str]:
    try:
        result = subprocess.run(
            cmd if isinstance(cmd, list) else cmd,
            capture_output=capture,
            text=True,
            encoding="utf-8",
            errors="replace",
            cwd=str(cwd or PROJECT_DIR),
            shell=isinstance(cmd, str),
        )
        return result.returncode == 0, (result.stdout or "").strip(), (result.stderr or "").strip()
    except Exception as e:
        return False, "", str(e)


def header(title: str) -> None:
    print("\n" + "═" * 64)
    print(f"  {title}")
    print("═" * 64)

def ok(msg: str) -> None:
    print(f"✅ {msg}")

def err(msg: str) -> None:
    print(f"❌ {msg}")

def info(msg: str) -> None:
    print(f"ℹ️  {msg}")

def warn(msg: str) -> None:
    print(f"⚠️  {msg}")


# ════════════════════════════════════════════════════════════════════════════
#  GIT
# ════════════════════════════════════════════════════════════════════════════

def get_branch() -> str:
    success, out, _ = run("git branch --show-current")
    return out.strip() if success and out else DEFAULT_BRANCH

def get_upstream() -> str:
    success, out, _ = run("git rev-parse --abbrev-ref --symbolic-full-name @{u}")
    return out.strip() if success and out else ""

def has_unpushed_commits() -> bool:
    upstream = get_upstream()
    if upstream:
        success, out, _ = run(f"git log {upstream}..HEAD --oneline")
        return bool(success and out.strip())
    success, out, _ = run("git rev-list --count HEAD")
    return bool(success and out.strip().isdigit() and int(out.strip()) > 0)

def ensure_git_identity() -> bool:
    success_n, name, _ = run("git config --local --get user.name")
    success_e, email, _ = run("git config --local --get user.email")
    if name and email:
        return True
    warn("Repo chưa có user.name / user.email")
    default_name = os.getenv("USERNAME", "tunming04")
    default_email = os.getenv("GIT_AUTHOR_EMAIL", f"{default_name}@users.noreply.github.com")
    entered_name = input(f"👤 user.name [{default_name}]: ").strip() or default_name
    entered_email = input(f"📧 user.email [{default_email}]: ").strip() or default_email
    success_a, _, _ = run(["git", "config", "--local", "user.name", entered_name])
    success_b, _, _ = run(["git", "config", "--local", "user.email", entered_email])
    if success_a and success_b:
        ok(f"Set: {entered_name} <{entered_email}>")
        return True
    err("Set git identity thất bại")
    return False

def remove_excluded_from_index() -> None:
    for path in GIT_EXCLUDED:
        if "*" in path:
            _, matched, _ = run(f"git ls-files --cached -- {path}")
            for f in matched.splitlines():
                f = f.strip()
                if f:
                    run(["git", "rm", "-r", "--cached", "--ignore-unmatch", f])
        else:
            run(["git", "rm", "-r", "--cached", "--ignore-unmatch", path])

def git_status() -> bool:
    header("Git Status")
    success, out, _ = run("git status -s")
    if out:
        print(out)
    else:
        ok("Working tree clean")
    return success

def git_push(message: str | None = None) -> bool:
    header("Git Push")
    branch = get_branch()
    _, dirty, _ = run("git status -s")
    has_changes = bool(dirty.strip())
    has_unpushed = has_unpushed_commits()
    if not has_changes and not has_unpushed:
        ok("Không có gì để push")
        return True

    if has_changes:
        print(f"📝 Thay đổi:\n{dirty}\n")
        if not message:
            message = input("💬 Commit message (Enter = auto): ").strip()
            if not message:
                message = f"chore: update {datetime.now():%Y-%m-%d %H:%M}"
        run("git add -A")
        remove_excluded_from_index()
        _, staged, _ = run("git diff --cached --name-only")
        if not staged.strip():
            warn("Nothing staged (có thể bị .gitignore)")
            return True
        if not ensure_git_identity():
            return False
        success, out, error = run(["git", "commit", "-m", message])
        combined = (out + " " + error).strip()
        if success:
            ok(f"Commit: {out or 'OK'}")
        elif "nothing to commit" in combined:
            warn("Không có thay đổi mới")
        else:
            err(f"Commit fail: {combined}")
            return False
    elif has_unpushed:
        info(f"Có commit chưa push trên branch '{branch}'")

    success, _, error = run(f"git push -u origin {branch}")
    if success:
        ok("Push thành công")
        return True
    if "fetch first" in error or "rejected" in error:
        warn("Remote ahead — pull --rebase rồi push lại...")
        success_pull, _, error_pull = run(f"git pull --rebase origin {branch}")
        if not success_pull:
            err(f"Pull rebase fail: {error_pull}")
            return False
        success_push, _, error_push = run(f"git push origin {branch}")
        if success_push:
            ok("Push sau rebase thành công")
        else:
            err(f"Push fail: {error_push}")
        return success_push
    err(f"Push fail: {error}")
    return False

def git_pull() -> bool:
    header("Git Pull")
    branch = get_branch()
    success, out, error = run(f"git pull origin {branch}")
    if success:
        ok(out or "Up to date")
    else:
        err(error)
    return success

def git_log(n: int = 10) -> bool:
    header(f"{n} commit gần nhất")
    success, out, _ = run(f"git log -{n} --oneline --decorate")
    print(out or "(no commits)")
    return success

def git_push_full() -> bool:
    header("Git Push FULL (xóa cache, add all)")
    branch = get_branch()
    if input("❓ Xác nhận xóa git cache + add all? (y/N): ").strip().lower() != "y":
        warn("Hủy")
        return False
    message = input("💬 Commit message (Enter = auto): ").strip() \
        or f"chore: full sync {datetime.now():%Y-%m-%d %H:%M}"
    run("git rm -r --cached .")
    run("git add .")
    remove_excluded_from_index()
    _, staged, _ = run("git diff --cached --name-only")
    if not staged.strip():
        warn("Nothing staged")
        return False
    if not ensure_git_identity():
        return False
    success, out, error = run(["git", "commit", "-m", message])
    if not success and "nothing to commit" not in (out + error):
        err(f"Commit fail: {error}")
        return False
    success_push, _, error_push = run(f"git push -u origin {branch}")
    if success_push:
        ok("Push full thành công")
    else:
        err(error_push)
    return success_push

def git_reset_and_reinit() -> bool:
    header("Git RESET + INIT lại (NGUY HIỂM)")
    _, remote, _ = run("git remote get-url origin")
    if not remote:
        err("Không có remote origin")
        return False
    print(f"📡 Remote: {remote}")
    if input("❓ Xác nhận xóa toàn bộ .git? (y/N): ").strip().lower() != "y":
        warn("Hủy")
        return False
    if input("❓ Xác nhận lần 2 — KHÔNG hoàn tác được! (y/N): ").strip().lower() != "y":
        warn("Hủy")
        return False
    branch = get_branch()
    message = input("💬 Commit message (Enter = auto): ").strip() \
        or f"chore: re-init {datetime.now():%Y-%m-%d %H:%M}"
    git_dir = PROJECT_DIR / ".git"
    if git_dir.exists():
        if sys.platform == "win32":
            subprocess.run(f'rd /s /q "{git_dir}"', shell=True, cwd=str(PROJECT_DIR))
        else:
            import shutil as _sh
            _sh.rmtree(git_dir, ignore_errors=True)
        if git_dir.exists():
            err("Không xóa được .git — close Xcode/VSCode rồi thử lại")
            return False
    run("git init")
    run(f"git remote add origin {remote}")
    run(f"git checkout -b {branch}")
    run("git add .")
    remove_excluded_from_index()
    if not ensure_git_identity():
        return False
    success, out, error = run(["git", "commit", "-m", message])
    if not success and "nothing to commit" not in (out + error):
        err(f"Commit fail: {error}")
        return False
    success_push, _, error_push = run(f"git push -f origin {branch}")
    if success_push:
        ok("Reset + force-push thành công")
    else:
        err(error_push)
    return success_push


# ════════════════════════════════════════════════════════════════════════════
#  GITHUB API
# ════════════════════════════════════════════════════════════════════════════

def github_headers() -> dict:
    headers = {
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
    }
    if GITHUB_TOKEN:
        headers["Authorization"] = f"Bearer {GITHUB_TOKEN}"
    return headers

def github_workflows() -> bool:
    header(f"GitHub Workflows — {OWNER}/{REPO}")
    if not GITHUB_TOKEN:
        warn("GITHUB_TOKEN chưa set (chỉ list được public)")
    try:
        r = requests.get(
            f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/workflows",
            headers=github_headers(), timeout=15,
        )
        r.raise_for_status()
        for w in r.json().get("workflows", []):
            icon = "🟢" if w.get("state") == "active" else "⚪"
            print(f"{icon} [{w['id']}] {w['name']}  ({w.get('path', '')})")
        return True
    except Exception as e:
        err(str(e))
        return False

def github_runs(n: int = 10) -> bool:
    header(f"GitHub Runs — {n} mới nhất")
    try:
        r = requests.get(
            f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/runs",
            headers=github_headers(), params={"per_page": n}, timeout=15,
        )
        r.raise_for_status()
        icons = {"success": "✅", "failure": "❌", "cancelled": "⏹️",
                 "in_progress": "🔄", "queued": "⏳"}
        for run_obj in r.json().get("workflow_runs", []):
            status = run_obj.get("conclusion") or run_obj.get("status", "?")
            icon = icons.get(status, "❓")
            print(f"{icon} [{run_obj['id']}] {run_obj['name']} — {run_obj['head_branch']} — {status}")
        return True
    except Exception as e:
        err(str(e))
        return False

def github_open_repo() -> None:
    url = f"https://github.com/{OWNER}/{REPO}"
    info(f"Mở: {url}")
    if sys.platform == "win32":
        os.startfile(url)
    elif sys.platform == "darwin":
        subprocess.run(["open", url])
    else:
        subprocess.run(["xdg-open", url])


# ════════════════════════════════════════════════════════════════════════════
#  GITHUB ACTIONS — BUILD
# ════════════════════════════════════════════════════════════════════════════

def github_build(prompt: bool = True) -> bool:
    """Trigger GitHub Actions workflow ios-build.yml."""
    header("GitHub Actions — iOS Build")
    if not GITHUB_TOKEN:
        err("Thiếu GITHUB_TOKEN")
        info("Set: $env:GITHUB_TOKEN = 'ghp_...'")
        return False

    branch = DEFAULT_BRANCH
    if prompt:
        entered = input(f"🌿 Branch (Enter = '{branch}'): ").strip()
        if entered:
            branch = entered

    info(f"Trigger workflow '{DEFAULT_WORKFLOW}' on branch '{branch}'")
    try:
        r = requests.post(
            f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/workflows/{DEFAULT_WORKFLOW}/dispatches",
            headers=github_headers(),
            json={"ref": branch},
            timeout=15,
        )
        r.raise_for_status()
        ok("Triggered ✓")
        info(f"Theo dõi: https://github.com/{OWNER}/{REPO}/actions")
        return True
    except Exception as e:
        err(f"Lỗi trigger: {e}")
        return False


def github_check_logs(run_id: str | None = None) -> bool:
    """Xem log của GitHub Actions run — hiển thị errors/warnings.

    Resolution:
      1. arg run_id
      2. Run mới nhất
    """
    header("GitHub Actions — Check Logs")
    if not GITHUB_TOKEN:
        err("Thiếu GITHUB_TOKEN")
        return False

    if not run_id:
        try:
            r = requests.get(
                f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/runs",
                headers=github_headers(), params={"per_page": 1}, timeout=15,
            )
            r.raise_for_status()
            runs = r.json().get("workflow_runs", [])
            if not runs:
                err("Không có run nào.")
                return False
            run_id = str(runs[0]["id"])
        except Exception as e:
            err(f"Fetch latest run fail: {e}")
            return False

    info(f"Run ID: {run_id}")
    info(f"URL: https://github.com/{OWNER}/{REPO}/actions/runs/{run_id}")

    # Get run detail
    try:
        r = requests.get(
            f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/runs/{run_id}",
            headers=github_headers(), timeout=15,
        )
        r.raise_for_status()
        run_data = r.json()
    except Exception as e:
        err(f"Fetch run detail fail: {e}")
        return False

    status = run_data.get("status", "?")
    conclusion = run_data.get("conclusion", "")
    print(f"📊 Status: {status}  |  Conclusion: {conclusion}")

    # Get jobs
    try:
        r = requests.get(
            f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/runs/{run_id}/jobs",
            headers=github_headers(), timeout=15,
        )
        r.raise_for_status()
        jobs = r.json().get("jobs", [])
    except Exception as e:
        err(f"Fetch jobs fail: {e}")
        return False

    icons = {"success": "✅", "failure": "❌", "cancelled": "⏹️",
             "in_progress": "🔄", "queued": "⏳", "skipped": "⏭️"}

    print(f"\n📋 Jobs ({len(jobs)}):")
    for job in jobs:
        j_status = job.get("conclusion") or job.get("status", "?")
        icon = icons.get(j_status, "❓")
        print(f"  {icon} {job['name']}  ({j_status})")

        # Get steps for failed jobs
        if j_status == "failed":
            for step in job.get("steps", []):
                s_status = step.get("conclusion") or step.get("status", "?")
                if s_status == "failed":
                    print(f"    ❌ {step['name']}  ({s_status})")

    # Fetch log for failed jobs
    found_failed = False
    for job in jobs:
        if (job.get("conclusion") or job.get("status")) != "failed":
            continue
        found_failed = True
        job_id = job["id"]
        print(f"\n{'='*64}")
        print(f"  FAILED JOB: {job['name']}")
        print(f"{'='*64}")

        try:
            r = requests.get(
                f"https://api.github.com/repos/{OWNER}/{REPO}/actions/jobs/{job_id}/logs",
                headers=github_headers(), timeout=120,
            )
            if r.status_code == 200:
                log_text = r.text
                print(f"  Log fetched ({len(log_text):,} bytes)")

                # Save log
                artifacts_dir = PROJECT_DIR / "artifacts"
                artifacts_dir.mkdir(exist_ok=True)
                log_path = artifacts_dir / f"gh-run-{run_id}-job-{job_id}.log"
                try:
                    log_path.write_text(log_text, encoding="utf-8", errors="replace")
                    ok(f"Log saved: {log_path.relative_to(PROJECT_DIR)}")
                except Exception:
                    pass

                # Parse errors
                seen = set()
                count_e, count_w = 0, 0
                print("\n=== ERRORS ===")
                for raw in log_text.splitlines():
                    line = raw.strip()
                    if not line or line in seen:
                        continue
                    lo = line.lower()
                    if ("error:" in lo or "fatal error:" in lo
                            or "ld: error" in lo
                            or ("swift" in lo and "error" in lo)):
                        print(f"  {line}")
                        seen.add(line)
                        count_e += 1
                        if count_e >= 50:
                            print("  ... (truncated)")
                            break

                print("\n=== WARNINGS ===")
                for raw in log_text.splitlines():
                    line = raw.strip()
                    if not line or line in seen:
                        continue
                    lo = line.lower()
                    if ".swift:" in lo and "warning:" in lo:
                        print(f"  {line}")
                        seen.add(line)
                        count_w += 1
                        if count_w >= 20:
                            print("  ... (truncated)")
                            break

                print(f"\n→ {count_e} errors, {count_w} warnings")
            else:
                warn(f"Log fetch returned {r.status_code}")
        except Exception as e:
            warn(f"Log fetch fail: {e}")

    if not found_failed:
        if conclusion == "success":
            ok("Build SUCCESS — không có job nào failed.")
        else:
            warn(f"Conclusion: {conclusion} — không tìm được failed job.")

    return conclusion == "success"


def github_upload_secrets() -> bool:
    """Upload secrets cần thiết cho GitHub Actions."""
    header("Upload GitHub Secrets")
    if not GITHUB_TOKEN:
        err("Thiếu GITHUB_TOKEN")
        info("Set: $env:GITHUB_TOKEN = 'ghp_...'")
        return False

    try:
        from base64 import b64encode
        from nacl import public
    except ImportError:
        err("Cần: pip install pynacl")
        return False

    candidates = [
        ("CM_TOKEN", os.getenv("CM_TOKEN", "")),
        ("CM_APP_ID", os.getenv("CM_APP_ID", "6a3b40e843c913cb9f2e959a")),
    ]
    secrets_to_upload = [(n, v) for n, v in candidates if v.strip()]
    if not secrets_to_upload:
        warn("Không có env var nào để upload. Set trước:")
        for n, _ in candidates:
            info(f"  $env:{n} = '...'")
        return False

    try:
        r = requests.get(
            f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/secrets/public-key",
            headers=github_headers(), timeout=15,
        )
        r.raise_for_status()
        data = r.json()
        key_id, public_key = data["key_id"], data["key"]
    except Exception as e:
        err(f"Get public key fail: {e}")
        return False

    public_key_obj = public.PublicKey(b64encode(public_key.encode()), encoder=__import__("nacl.encoding", fromlist=["Base64Encoder"]).Base64Encoder)
    sealed_box = public.SealedBox(public_key_obj)

    success_count = 0
    for name, value in secrets_to_upload:
        encrypted = b64encode(sealed_box.encrypt(value.encode())).decode()
        try:
            r = requests.put(
                f"{GITHUB_API}/repos/{OWNER}/{REPO}/actions/secrets/{name}",
                headers=github_headers(),
                json={"encrypted_value": encrypted, "key_id": key_id},
                timeout=15,
            )
            r.raise_for_status()
            ok(f"{name} ✓")
            success_count += 1
        except Exception as e:
            err(f"{name} ✗ — {e}")

    print(f"\n→ {success_count}/{len(secrets_to_upload)} uploaded")
    info(f"Xem: https://github.com/{OWNER}/{REPO}/settings/secrets/actions")
    return success_count == len(secrets_to_upload)


# ════════════════════════════════════════════════════════════════════════════
#  iOS PROJECT VALIDATION
# ════════════════════════════════════════════════════════════════════════════

def parse_project_yml() -> dict:
    if not PROJECT_YML.exists():
        return {}
    result: dict[str, str] = {}
    keys_of_interest = {"MARKETING_VERSION", "CURRENT_PROJECT_VERSION", "PRODUCT_BUNDLE_IDENTIFIER"}
    lines = PROJECT_YML.read_text(encoding="utf-8").splitlines()
    for index, raw in enumerate(lines):
        stripped = raw.strip()
        if ":" not in stripped:
            continue
        key, _, value = stripped.partition(":")
        key = key.strip()
        value = value.strip().strip('"').strip("'").strip()
        if key in keys_of_interest and value and key not in result:
            result[key] = value
        if key == "deploymentTarget" and not value:
            if index + 1 < len(lines):
                next_line = lines[index + 1].strip()
                if next_line.lower().startswith("ios:"):
                    _, _, ios_value = next_line.partition(":")
                    result["deploymentTarget"] = ios_value.strip().strip('"').strip("'")
    return result

def validate_project() -> bool:
    header("Validate iOS Project")
    files_to_check = [
        ("project.yml", PROJECT_YML),
        ("Resources/Info.plist", INFO_PLIST),
        ("Resources/RapPhim.entitlements", ENTITLEMENTS),
        ("Resources/GoogleService-Info.plist", GOOGLE_SERVICES),
    ]
    all_present = True
    for name, path in files_to_check:
        if path.exists():
            ok(f"{name}")
        else:
            err(f"{name} MISSING")
            all_present = False

    if INFO_PLIST.exists():
        try:
            with INFO_PLIST.open("rb") as f:
                plist = plistlib.load(f)
            required_keys = [
                "CFBundleDisplayName", "CFBundleURLTypes", "GIDClientID",
                "NSAppTransportSecurity", "UIBackgroundModes",
                "NSCameraUsageDescription", "NSLocalNetworkUsageDescription",
            ]
            missing = [k for k in required_keys if k not in plist]
            if missing:
                err(f"Info.plist thiếu key: {', '.join(missing)}")
                all_present = False
            else:
                ok("Info.plist required keys ✓")
        except Exception as e:
            err(f"Info.plist parse fail: {e}")
            all_present = False

    if GOOGLE_SERVICES.exists():
        try:
            with GOOGLE_SERVICES.open("rb") as f:
                gs = plistlib.load(f)
            bundle = gs.get("BUNDLE_ID", "")
            if bundle == BUNDLE_ID:
                ok(f"GoogleService-Info BUNDLE_ID = {bundle}")
            else:
                err(f"GoogleService-Info BUNDLE_ID = {bundle} (expected {BUNDLE_ID})")
                all_present = False
        except Exception as e:
            err(f"GoogleService-Info parse fail: {e}")

    project_meta = parse_project_yml()
    if project_meta:
        print()
        info(f"MARKETING_VERSION       = {project_meta.get('MARKETING_VERSION', '?')}")
        info(f"CURRENT_PROJECT_VERSION = {project_meta.get('CURRENT_PROJECT_VERSION', '?')}")
        info(f"BUNDLE_ID               = {project_meta.get('PRODUCT_BUNDLE_IDENTIFIER', '?')}")
        info(f"iOS deploymentTarget    = {project_meta.get('deploymentTarget', '?')}")

    return all_present


# ════════════════════════════════════════════════════════════════════════════
#  VERSION
# ════════════════════════════════════════════════════════════════════════════

def get_version() -> tuple[str, str]:
    project_meta = parse_project_yml()
    return (
        project_meta.get("MARKETING_VERSION", "1.0.0"),
        project_meta.get("CURRENT_PROJECT_VERSION", "1"),
    )

def set_version(version: str | None = None, build: str | None = None) -> tuple[str, str]:
    current_v, current_b = get_version()
    new_v = version or current_v
    new_b = build or current_b
    if not re.match(r"^\d+\.\d+\.\d+$", new_v):
        err(f"Version sai format: '{new_v}' (cần major.minor.patch)")
        return current_v, current_b

    text = PROJECT_YML.read_text(encoding="utf-8")
    text = re.sub(r'(MARKETING_VERSION:\s*)"?[^"\n]+"?', rf'\1"{new_v}"', text)
    text = re.sub(r'(CURRENT_PROJECT_VERSION:\s*)"?[^"\n]+"?', rf'\1"{new_b}"', text)
    PROJECT_YML.write_text(text, encoding="utf-8")
    ok(f"Version updated: {new_v}+{new_b}  ->  project.yml")
    return new_v, new_b

def prompt_version() -> tuple[str, str]:
    current_v, current_b = get_version()
    print(f"\n📌 Hiện tại: {current_v}+{current_b}")
    print("📝 Cách nhập:")
    print(f"   • Enter        → giữ nguyên")
    print(f"   • bump         → tự bump (.9 rollover)")
    print(f"   • 1.0.1        → set version, giữ build")
    print(f"   • 1.0.1+5      → set cả version + build")
    print(f"   • +5           → giữ version, bump build = 5")
    choice = input("👉 Lựa chọn: ").strip()
    if not choice:
        return current_v, current_b
    if choice.lower() == "bump":
        return bump_version()
    if choice.startswith("+"):
        return set_version(current_v, choice[1:])
    if "+" in choice:
        v, b = choice.split("+", 1)
        return set_version(v, b)
    return set_version(choice, current_b)

def bump_version() -> tuple[str, str]:
    current_v, current_b = get_version()
    parts = current_v.split(".")
    if len(parts) != 3:
        err(f"Version không hợp lệ: {current_v}")
        return current_v, current_b
    try:
        major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
    except ValueError:
        err(f"Version không hợp lệ: {current_v}")
        return current_v, current_b
    if patch < 9:
        patch += 1
    elif minor < 9:
        minor += 1
        patch = 0
    else:
        major += 1
        minor = 0
        patch = 0
    new_v = f"{major}.{minor}.{patch}"
    try:
        new_b = str(int(current_b) + 1)
    except ValueError:
        new_b = current_b
    return set_version(new_v, new_b)


# ════════════════════════════════════════════════════════════════════════════
#  XCODEGEN (chỉ chạy trên Mac)
# ════════════════════════════════════════════════════════════════════════════

def xcodegen_regen() -> bool:
    header("XcodeGen — regenerate Xcode project")
    if sys.platform != "darwin":
        warn("Chỉ chạy được trên Mac.")
        info("Trên Mac: brew install xcodegen && xcodegen generate")
        return False
    success_check, _, _ = run("which xcodegen")
    if not success_check:
        warn("Chưa có xcodegen. Cài: brew install xcodegen")
        return False
    success, out, error = run("xcodegen generate", capture=False)
    if success:
        ok("Generated RapPhim.xcodeproj")
    else:
        err(error)
    return success


# ════════════════════════════════════════════════════════════════════════════
#  CHANGELOG
# ════════════════════════════════════════════════════════════════════════════

def generate_changelog() -> Path | None:
    header("Generate CHANGELOG")
    version, build = get_version()
    artifacts_dir = PROJECT_DIR / "artifacts"
    artifacts_dir.mkdir(exist_ok=True)

    success, commits, _ = run("git log -15 --pretty=format:- %s (%an)")
    success_sha, sha, _ = run("git rev-parse HEAD")
    short_sha = (sha or "")[:7] if success_sha else "?"

    content = f"""# CHANGELOG — RapPhim iOS v{version}

**Build:** {build}
**Date:** {datetime.now():%Y-%m-%d %H:%M}
**Commit:** {short_sha}
**Repo:** https://github.com/{OWNER}/{REPO}

## Recent commits
{commits if (success and commits) else "- (no commits)"}

## Install — Sideload (AltStore / Sideloadly)
1. Tải file IPA từ GitHub Actions artifact.
2. Mở AltStore (hoặc Sideloadly) trên Mac/PC.
3. Sign bằng Apple ID free hoặc Apple Dev account.

## Install — App Store (sau khi enroll)
1. Search "Rạp Phim" trên App Store.
2. Hoặc TestFlight invite.

---
Generated by `tool-ios-b.py`.
"""
    output = artifacts_dir / f"CHANGELOG-v{version}.md"
    output.write_text(content, encoding="utf-8")
    ok(f"Generated: {output.relative_to(PROJECT_DIR)}")
    return output


# ════════════════════════════════════════════════════════════════════════════
#  MENU + CLI
# ════════════════════════════════════════════════════════════════════════════

def safe_input(prompt: str) -> str | None:
    try:
        return input(prompt)
    except (KeyboardInterrupt, EOFError):
        print("\n👋 Bye!")
        return None

def main_menu() -> None:
    while True:
        print("\n" + "═" * 64)
        print(f"  🎬  RapPhim iOS Tools B — {OWNER}/{REPO}")
        print("═" * 64)
        print(f"""
📁 Git:
  [1]  Status
  [2]  Push (commit + push)
  [2f] Push FULL
  [3]  Pull
  [4]  Log

🐙 GitHub:
  [5]  List workflows
  [6]  List runs
  [6r] Mở repo trên browser
  [7]  Upload secrets

🔧 iOS Project:
  [8]  Validate
  [9]  Set version + build #
  [10] Generate Xcode project (Mac only)

🚀 GitHub Actions Build:
  [11] Trigger BUILD (iOS build workflow)
  [12] Check logs (failed run)
  [15] Watch latest run

📝 Release:
  [14] Generate CHANGELOG.md

  [0]  Thoát
""")
        raw = safe_input("👉 Chọn: ")
        if raw is None:
            return
        choice = raw.strip().lower()
        if not choice:
            continue

        actions = {
            "0": lambda: ("exit", None),
            "1": git_status,
            "2": git_push,
            "2f": git_push_full,
            "2r": git_reset_and_reinit,
            "3": git_pull,
            "4": git_log,
            "5": github_workflows,
            "6": github_runs,
            "6r": github_open_repo,
            "7": github_upload_secrets,
            "8": validate_project,
            "9": prompt_version,
            "10": xcodegen_regen,
            "11": lambda: github_build(prompt=False),
            "12": lambda: github_check_logs(),
            "14": generate_changelog,
            "15": lambda: github_check_logs(),
        }
        action = actions.get(choice)
        if action is None:
            err("Lựa chọn không hợp lệ")
            continue
        if choice == "0":
            print("\n👋 Bye!")
            return
        try:
            action()
        except Exception as e:
            err(f"Unhandled error: {e}")
        if safe_input("\n⏎ Enter để tiếp tục...") is None:
            return


def print_usage() -> None:
    print(f"""
RapPhim iOS Tools B  —  https://github.com/{OWNER}/{REPO}

  python tool-ios-b.py                          Menu interactive
  python tool-ios-b.py status                   Git status
  python tool-ios-b.py push [message]           Git commit + push
  python tool-ios-b.py pull                     Git pull
  python tool-ios-b.py log [n]                  Last N commits

  python tool-ios-b.py validate                 Check project files
  python tool-ios-b.py version                  Show version
  python tool-ios-b.py version 1.0.1            Set version
  python tool-ios-b.py bump                     Auto bump

  python tool-ios-b.py build [--no-prompt]      Trigger GitHub Actions build
  python tool-ios-b.py logs [run_id]            Check failed run logs
  python tool-ios-b.py workflows                List GitHub workflows
  python tool-ios-b.py runs [n]                 List GitHub Actions runs

  python tool-ios-b.py changelog                Generate CHANGELOG
  python tool-ios-b.py secrets                  Upload GitHub secrets

ENV vars:
  GITHUB_TOKEN        — GitHub PAT (scope: repo)
""")


def main_cli(args: list[str]) -> None:
    if not args:
        main_menu()
        return

    cmd = args[0].lower()
    rest = args[1:]
    no_prompt = "--no-prompt" in rest or "-y" in rest

    if cmd in ("help", "-h", "--help"):
        print_usage()
    elif cmd == "status":
        git_status()
    elif cmd == "push":
        message = " ".join(arg for arg in rest if not arg.startswith("-")) or None
        git_push(message)
    elif cmd == "push-full":
        git_push_full()
    elif cmd == "pull":
        git_pull()
    elif cmd == "log":
        n = int(rest[0]) if rest and rest[0].isdigit() else 10
        git_log(n)
    elif cmd == "validate":
        validate_project()
    elif cmd == "version":
        positionals = [arg for arg in rest if not arg.startswith("-")]
        if not positionals:
            v, b = get_version()
            print(f"{v}+{b}")
        elif len(positionals) == 1:
            set_version(positionals[0], None)
        else:
            set_version(positionals[0], positionals[1])
    elif cmd == "bump":
        bump_version()
    elif cmd == "xcodegen":
        xcodegen_regen()
    elif cmd in ("build", "ios-build"):
        github_build(prompt=not no_prompt)
    elif cmd == "logs":
        positionals = [arg for arg in rest if not arg.startswith("-")]
        github_check_logs(positionals[0] if positionals else None)
    elif cmd == "runs":
        n = int(rest[0]) if rest and rest[0].isdigit() else 10
        github_runs(n)
    elif cmd == "workflows":
        github_workflows()
    elif cmd == "secrets":
        github_upload_secrets()
    elif cmd == "changelog":
        generate_changelog()
    elif cmd == "repo":
        github_open_repo()
    else:
        err(f"Unknown command: {cmd}")
        print_usage()


if __name__ == "__main__":
    main_cli(sys.argv[1:])
