@echo off
chcp 65001 >nul
title Tunel ngrok - API Productos
cd /d "%~dp0"

rem Dominio fijo gratuito de ngrok (dashboard.ngrok.com -> Domains)
set "DOMINIO=pureness-outrank-scanning.ngrok-free.dev"

echo ============================================
echo   Tunel publico para la API (ngrok)
echo ============================================
echo.

rem --- 1. Buscar ngrok ---
set "NGROK="
if exist "%LOCALAPPDATA%\ngrok\ngrok.exe" set "NGROK=%LOCALAPPDATA%\ngrok\ngrok.exe"
if not defined NGROK (
    where ngrok >nul 2>nul && set "NGROK=ngrok"
)
if not defined NGROK (
    echo [ERROR] No se encontro ngrok. Descargalo de https://ngrok.com/download
    goto fin
)

rem --- 2. Verificar que el authtoken este registrado ---
"%NGROK%" config check >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Falta registrar tu authtoken. Copialo de dashboard.ngrok.com ^(Your Authtoken^) y ejecuta:
    echo     "%NGROK%" config add-authtoken TU_TOKEN
    goto fin
)

rem --- 3. Verificar que la API este corriendo (iniciar.bat) ---
curl -s -o nul http://127.0.0.1:8000/api/productos
if errorlevel 1 (
    echo [ERROR] La API no esta corriendo. Primero ejecuta iniciar.bat y deja esa ventana abierta.
    goto fin
)

echo   API publica en: https://%DOMINIO%/api/productos
echo   App Android:    https://%DOMINIO%/api/
echo.
echo   Deja esta ventana abierta mientras presentas.
echo   Para cerrar el tunel pulsa Ctrl+C o cierra la ventana.
echo ============================================
echo.
"%NGROK%" http --url=%DOMINIO% 8000

:fin
echo.
pause
