@echo off
echo ========================================
echo   IPTV Vietnam - Deployment Script
echo ========================================
echo.

echo [1/4] Checking Wrangler...
where wrangler >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing Wrangler...
    npm install -g wrangler
)

echo [2/4] Login to Cloudflare (if needed)...
wrangler whoami >nul 2>&1
if %errorlevel% neq 0 (
    wrangler login
)

echo [3/4] Creating KV Namespace...
wrangler kv:namespace create "PLAYLIST" 2>nul
echo.
echo Copy the KV ID above and update wrangler.toml if needed!
echo.

echo [4/4] Deploying Worker...
cd cloudflare-worker
wrangler deploy
cd ..

echo.
echo ========================================
echo   Deploy Complete!
echo ========================================
echo.
echo Worker URL: https://playertv-app.YOUR_SUBDOMAIN.workers.dev
echo Admin Panel: https://playertv-app.YOUR_SUBDOMAIN.workers.dev/admin
echo.
echo Next steps:
echo 1. Check API_BASE_URL in android/app/.../util/Constants.kt and ios/Core/Services/Constants.swift
echo 2. Rebuild apps
echo.
pause
