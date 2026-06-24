#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
IPTV Vietnam Android Tools — Build & manage IPTV Vietnam Android app.

Usage:
    python tool-android.py             # Interactive menu
    python tool-android.py build       # assembleRelease
    python tool-android.py build-debug # assembleDebug
    python tool-android.py install     # installDebug + launch + live logcat
    python tool-android.py clean       # gradlew clean
    python tool-android.py lint
    python tool-android.py version             # Show app version
    python tool-android.py version 1.0.0 1     # Set versionName + versionCode
"""

from __future__ import annotations

import io
import json
import os
import re
import subprocess
import sys
from datetime import datetime
from pathlib import Path

# --- Force UTF-8 stdio on Windows ---
if sys.platform == "win32":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", errors="replace")

# ==================== CONFIG ===============================================

# IPTV Vietnam project path
PROJECT_DIR = Path(r"E:\Code\iptv-cap\android")

PACKAGE_NAME = "com.player.tv"
APP_NAME = "PlayerTV"

# --- Android paths ---
APP_BUILD_GRADLE = PROJECT_DIR / "app" / "build.gradle.kts"
RELEASE_DIR = PROJECT_DIR / "release"

# ==================== UTILS =================================================

def run_command(cmd, cwd: Path | None = None, capture: bool = True, env: dict | None = None):
    """Run a shell command and return `(ok, stdout, stderr)`."""
    try:
        result = subprocess.run(
            cmd if isinstance(cmd, list) else cmd,
            capture_output=capture,
            text=True,
            encoding="utf-8",
            errors="replace",
            cwd=str(cwd or PROJECT_DIR),
            shell=isinstance(cmd, str),
            env=env,
        )
        out = (result.stdout or "").strip()
        err = (result.stderr or "").strip()
        return result.returncode == 0, out, err
    except Exception as exc:
        return False, "", str(exc)


def print_header(title: str) -> None:
    print("\n" + "=" * 60)
    print(f"  {title}")
    print("=" * 60 + "\n")


def print_success(msg: str) -> None:
    print(f"✅ {msg}")


def print_error(msg: str) -> None:
    print(f"❌ {msg}")


def print_info(msg: str) -> None:
    print(f"ℹ️  {msg}")


def print_warning(msg: str) -> None:
    print(f"⚠️  {msg}")


# ==================== GRADLE ================================================

def gradle_wrapper() -> str:
    return ".\\gradlew.bat" if sys.platform == "win32" else "./gradlew"


def gradle_env() -> dict:
    env = os.environ.copy()
    return env


def _gradle(task_args: list[str], capture: bool = False, log_file=None) -> bool:
    """Run a Gradle task."""
    cmd = f"{gradle_wrapper()} {' '.join(task_args)} --console=plain"
    env = gradle_env()
    if log_file is not None:
        return _stream_process(cmd, cwd=PROJECT_DIR, log_file=log_file, env=env)
    ok, _, _ = run_command(cmd, capture=capture, env=env)
    return ok


def _stream_process(cmd, *, cwd: Path | None = None, log_file=None, env: dict | None = None) -> bool:
    """Run cmd, streaming output to both stdout and log_file."""
    proc = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        cwd=str(cwd or PROJECT_DIR),
        shell=isinstance(cmd, str),
        bufsize=0,
        env=env,
    )
    try:
        assert proc.stdout is not None
        fd = proc.stdout.fileno()
        while True:
            try:
                chunk = os.read(fd, 8192)
            except OSError:
                break
            if not chunk:
                break
            text = chunk.decode("utf-8", errors="replace")
            sys.stdout.write(text)
            sys.stdout.flush()
            if log_file is not None:
                log_file.write(text)
                log_file.flush()
        proc.wait()
    except KeyboardInterrupt:
        try:
            proc.terminate()
            proc.wait(timeout=3)
        except Exception:
            try:
                proc.kill()
            except Exception:
                pass
    return proc.returncode == 0


# ==================== VERSION ==============================================

_VERSION_NAME_RE = re.compile(r'(versionName\s*=\s*")([^"]+)(")')
_VERSION_CODE_RE = re.compile(r"(versionCode\s*=\s*)(\d+)")


def get_current_version() -> tuple[str, int]:
    if not APP_BUILD_GRADLE.exists():
        return "0.0.0", 0
    text = APP_BUILD_GRADLE.read_text(encoding="utf-8")
    name_match = _VERSION_NAME_RE.search(text)
    code_match = _VERSION_CODE_RE.search(text)
    name = name_match.group(2) if name_match else "0.0.0"
    code = int(code_match.group(2)) if code_match else 0
    return name, code


def set_version(version_name: str | None = None, version_code: int | None = None) -> tuple[str, int]:
    if not APP_BUILD_GRADLE.exists():
        print_error(f"Không tìm thấy {APP_BUILD_GRADLE}")
        return "0.0.0", 0
    current_name, current_code = get_current_version()
    new_name = version_name or current_name
    new_code = version_code if version_code is not None else current_code

    text = APP_BUILD_GRADLE.read_text(encoding="utf-8")
    text = _VERSION_NAME_RE.sub(rf'\g<1>{new_name}\g<3>', text)
    text = _VERSION_CODE_RE.sub(rf"\g<1>{new_code}", text)
    APP_BUILD_GRADLE.write_text(text, encoding="utf-8")
    print_success(f"Version đã cập nhật: {new_name} (code {new_code})")
    return new_name, new_code


# ==================== BUILD FUNCTIONS ======================================

def gradle_clean() -> bool:
    print_header("Gradle clean")
    return _gradle([":app:clean"], capture=False)


def gradle_assemble_debug() -> bool:
    print_header("Build APK (debug)")
    ok = _gradle([":app:assembleDebug"], capture=False)
    if ok:
        _copy_apk_outputs(variant="debug")
    return ok


def gradle_assemble_release() -> bool:
    print_header("Build APK (release)")
    ok = _gradle([":app:assembleRelease"], capture=False)
    if ok:
        _copy_apk_outputs(variant="release")
    return ok


def gradle_lint() -> bool:
    print_header("Gradle lint")
    return _gradle([":app:lint"], capture=False)


def _copy_apk_outputs(variant: str) -> None:
    """Copy APK to release/ folder."""
    apk_dir = PROJECT_DIR / "app" / "build" / "outputs" / "apk" / variant
    if not apk_dir.exists():
        print_error(f"Không tìm thấy thư mục APK: {apk_dir}")
        return
    apks = sorted(apk_dir.glob("*.apk"))
    if not apks:
        print_error("Không có file APK output.")
        return

    RELEASE_DIR.mkdir(parents=True, exist_ok=True)
    name, code = get_current_version()
    suffix = "-debug" if variant == "debug" else ""

    target = RELEASE_DIR / f"playertv-v{name}{suffix}.apk"
    import shutil
    shutil.copy2(apks[0], target)
    size_mb = target.stat().st_size / (1024 * 1024)

    print(f"\n📦 OUTPUT (variant={variant}):")
    print(f"   📂 Folder: {RELEASE_DIR}")
    print(f"   📄 APK:    {target.name} ({size_mb:.1f} MB)")


# ==================== ADB / INSTALL ========================================

def get_adb_path() -> str:
    home = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    if home:
        candidate = Path(home) / "platform-tools" / ("adb.exe" if sys.platform == "win32" else "adb")
        if candidate.exists():
            return str(candidate)
    if sys.platform == "win32":
        candidate = (
            Path(os.path.expandvars("%LOCALAPPDATA%"))
            / "Android" / "Sdk" / "platform-tools" / "adb.exe"
        )
        if candidate.exists():
            return str(candidate)
    return "adb"


ADB_PATH = get_adb_path()


def list_connected_devices() -> list[str]:
    ok, out, _ = run_command([ADB_PATH, "devices"])
    if not ok:
        return []
    out_lines = out.splitlines()[1:]
    devices = []
    for line in out_lines:
        parts = line.split()
        if len(parts) >= 2 and parts[1] == "device":
            devices.append(parts[0])
    return devices


def gradle_install_debug() -> bool:
    """Build → install → launch → live logcat."""
    print_header("Debug + install + chạy + live logs")
    devices = list_connected_devices()
    if not devices:
        print_error("Không thấy thiết bị/emulator. Kết nối USB hoặc bật emulator.")
        return False
    for d in devices:
        print(f"  • {d}")
    device = devices[0]

    if len(devices) > 1:
        print_warning(f"Có {len(devices)} thiết bị — dùng '{device}'.")

    RELEASE_DIR.mkdir(parents=True, exist_ok=True)
    log_path = RELEASE_DIR / "error.log"
    log_file = open(log_path, "w", encoding="utf-8", errors="replace")

    def _log_line(text: str) -> None:
        print(text)
        log_file.write(text + "\n")
        log_file.flush()

    try:
        _log_line("=" * 60)
        _log_line(f"IPTV Vietnam Debug Run — {datetime.now():%Y-%m-%d %H:%M:%S}")
        _log_line(f"Device: {device}")
        _log_line("=" * 60)

        # Build
        _log_line("[BUILD] gradle :app:assembleDebug ...")
        if not _gradle([":app:assembleDebug"], log_file=log_file):
            _log_line("\n❌ BUILD FAILED")
            return False
        _log_line("\n✅ BUILD thành công.")

        _copy_apk_outputs(variant="debug")

        # Find APK
        name, _ = get_current_version()
        apk = RELEASE_DIR / f"iptvvietnam-v{name}-debug.apk"
        if not apk.exists():
            apk_dir = PROJECT_DIR / "app" / "build" / "outputs" / "apk" / "debug"
            candidates = sorted(apk_dir.glob("*.apk")) if apk_dir.exists() else []
            apk = candidates[0] if candidates else apk
        if not apk.exists():
            _log_line(f"\n❌ Không tìm thấy APK: {apk}")
            return False

        # Install
        _log_line(f"\n[INSTALL] {apk.name} → {device} ...")
        ok_inst, out_inst, err_inst = run_command(
            [ADB_PATH, "-s", device, "install", "-r", str(apk)],
        )
        combined = (out_inst + " " + err_inst).lower()
        if ok_inst and "success" in combined:
            _log_line("✅ INSTALL thành công.")
        else:
            needs_clean = any(m in combined for m in ("update_incompatible", "inconsistent_certificates", "version_downgrade"))
            if needs_clean:
                _log_line("⚠️ Signature mismatch — uninstall old app...")
                run_command([ADB_PATH, "-s", device, "uninstall", PACKAGE_NAME])
                ok2, out2, err2 = run_command([ADB_PATH, "-s", device, "install", str(apk)])
                if ok2 and "success" in (out2 + err2).lower():
                    _log_line("✅ INSTALL thành công sau khi xoá app cũ.")
                else:
                    _log_line(f"\n❌ INSTALL thất bại: {(out2 + err2).strip()}")
                    return False
            else:
                _log_line(f"\n❌ INSTALL thất bại: {(out_inst + err_inst).strip()}")
                return False

        # Launch
        _log_line(f"\n[LAUNCH] {PACKAGE_NAME} trên {device} ...")
        run_command([ADB_PATH, "-s", device, "shell", "monkey", "-p", PACKAGE_NAME, "-c", "android.intent.category.LAUNCHER", "1"])

        # Get PID
        import time
        pid = ""
        for _ in range(25):
            time.sleep(0.2)
            ok_p, out_p, _ = run_command([ADB_PATH, "-s", device, "shell", "pidof", PACKAGE_NAME])
            if ok_p and out_p.strip().isdigit():
                pid = out_p.strip()
                break
        if pid:
            _log_line(f"✅ App đã chạy, PID={pid}")
        else:
            _log_line("⚠️ Không lấy được PID.")

        # Logcat
        run_command([ADB_PATH, "-s", device, "logcat", "-c"])
        _log_line("")
        _log_line("📡 Streaming logcat — Ctrl+C để dừng.")
        _log_line(f"📄 Log file: {log_path}")
        _log_line("-" * 60)
        sys.stdout.flush()

        log_args = [ADB_PATH, "-s", device, "logcat", "-v", "threadtime"]
        if pid:
            log_args.append(f"--pid={pid}")
        _stream_process(log_args, cwd=PROJECT_DIR, log_file=log_file)

    except KeyboardInterrupt:
        _log_line("\n— Đã dừng bởi Ctrl+C.")
    finally:
        _log_line(f"\n— Session kết thúc: {datetime.now():%Y-%m-%d %H:%M:%S}")
        log_file.close()
        print()
        print_info(f"📄 Log đầy đủ: {log_path}")
    return True


# ==================== MENU =================================================

def prompt_menu_input(prompt: str) -> str | None:
    try:
        return input(prompt)
    except (KeyboardInterrupt, EOFError):
        print("\n👋 Tạm biệt!")
        return None


def main_menu() -> None:
    while True:
        print("\n" + "=" * 60)
        print(f"  📺 {APP_NAME} Android Tools")
        print(f"  Project: {PROJECT_DIR}")
        print("=" * 60)
        name, code = get_current_version()
        print(f"  Version: {name} (code {code})")
        print(
            """
🔨 BUILD:
  [1]  📱 Debug + cài + chạy + live logs
  [2]  📦 APK debug (chỉ build, không cài)
  [3]  📦 APK release (signed)

📁 UTILS:
  [4]  Clean
  [5]  Lint
  [6]  Set version

  [0]  Thoát
""",
        )
        raw = prompt_menu_input("👉 Chọn: ")
        if raw is None:
            break
        choice = raw.strip().lower()
        if not choice:
            continue
        if choice == "0":
            print("\n👋 Tạm biệt!")
            break
        elif choice == "1":
            gradle_install_debug()
        elif choice == "2":
            gradle_assemble_debug()
        elif choice == "3":
            gradle_assemble_release()
        elif choice == "4":
            gradle_clean()
        elif choice == "5":
            gradle_lint()
        elif choice == "6":
            name, code = get_current_version()
            print(f"Version hiện tại: {name} (code {code})")
            new_name = input("Version mới (Enter = giữ nguyên): ").strip()
            if new_name:
                set_version(new_name)
        else:
            print_error("Lựa chọn không hợp lệ.")
            continue

        if prompt_menu_input("\n⏎ Enter để tiếp tục...") is None:
            break


# ==================== CLI ENTRY ============================================

def main(argv: list[str]) -> int:
    if not argv:
        if not sys.stdin.isatty():
            print(__doc__ or "")
            return 0
        main_menu()
        return 0

    cmd = argv[0]
    rest = argv[1:]

    if cmd in ("-h", "--help", "help"):
        print(__doc__ or "")
        return 0
    elif cmd == "build":
        return 0 if gradle_assemble_release() else 1
    elif cmd == "build-debug":
        return 0 if gradle_assemble_debug() else 1
    elif cmd == "install":
        return 0 if gradle_install_debug() else 1
    elif cmd == "clean":
        return 0 if gradle_clean() else 1
    elif cmd == "lint":
        return 0 if gradle_lint() else 1
    elif cmd == "version":
        if not rest:
            name, code = get_current_version()
            print(f"versionName = {name}")
            print(f"versionCode = {code}")
            return 0
        if len(rest) >= 1:
            new_name = rest[0]
            new_code = int(rest[1]) if len(rest) >= 2 and rest[1].isdigit() else None
            set_version(new_name, new_code)
            return 0

    print(__doc__ or "")
    return 2


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
