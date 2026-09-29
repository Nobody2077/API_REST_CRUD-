@echo off
chcp 65001 >nul
title API REST CRUD - Productos
cd /d "%~dp0"

echo ============================================
echo   API REST CRUD de Productos (Laravel+MySQL)
echo ============================================
echo.

rem --- 1. Buscar PHP: primero el de XAMPP en cualquier unidad, luego el del PATH ---
set "PHP="
for %%d in (C D E F G H I J K) do (
    if not defined PHP if exist "%%d:\xampp\php\php.exe" set "PHP=%%d:\xampp\php\php.exe"
)
if not defined PHP (
    where php >nul 2>nul && set "PHP=php"
)
if not defined PHP (
    echo [ERROR] No se encontro PHP. Instala XAMPP ^(https://www.apachefriends.org^).
    goto fin
)
echo [OK] PHP: %PHP%

rem --- 2. Verificar MySQL y crear la base si no existe ---
"%PHP%" scripts\preparar-bd.php crear
if errorlevel 2 (
    echo.
    echo [ERROR] MySQL esta apagado. Abre el panel de XAMPP, pulsa Start en MySQL
    echo         y vuelve a ejecutar este archivo.
    goto fin
)
if errorlevel 1 goto fin

rem --- 3. Crear las tablas (si ya existen no hace nada) ---
"%PHP%" artisan config:clear >nul
"%PHP%" artisan migrate --force
if errorlevel 1 goto fin

rem --- 4. Cargar los 5 productos de ejemplo solo si la tabla esta vacia ---
"%PHP%" scripts\preparar-bd.php vacia
if not errorlevel 1 (
    echo Cargando productos de ejemplo...
    "%PHP%" artisan db:seed --force
)

rem --- 5. Levantar la API ---
echo.
echo ============================================
echo   API lista en: http://127.0.0.1:8000/api/productos
echo   Emulador Android:  http://10.0.2.2:8000/api/
echo   Celular en la misma Wi-Fi: http://^<IP-de-esta-PC^>:8000/api/
for /f "tokens=2 delims=:" %%i in ('ipconfig ^| findstr /c:"IPv4"') do echo      IP de esta PC:%%i
echo.
echo   Para detener la API cierra esta ventana o pulsa Ctrl+C.
echo ============================================
echo.
"%PHP%" artisan serve --host=0.0.0.0 --port=8000

:fin
echo.
pause
