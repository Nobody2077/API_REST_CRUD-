# Desarrollo de una API REST CRUD – Gestión de productos

```
Android (Kotlin, Retrofit)  →  HTTP / JSON  →  API REST (Laravel 12 + PHP 8.2)  →  MySQL (XAMPP)
```

| Carpeta | Contenido | Detalles |
|---------|-----------|----------|
| `api-productos/` | API REST en Laravel | [api-productos/LEEME.md](api-productos/LEEME.md) |
| `app-android-productos/` | App Android cliente | [app-android-productos/LEEME.md](app-android-productos/LEEME.md) |

## Cómo hacer la demostración
1. Abrir XAMPP → **Start** en MySQL.
2. Levantar la API: doble clic en **`api-productos/iniciar.bat`**.
   Busca el PHP de XAMPP, crea la base `productos_db` si no existe, crea las tablas,
   carga los 5 productos de ejemplo si la tabla está vacía y deja la API corriendo en el puerto 8000.
   (Equivale a hacerlo a mano: `cd api-productos` y `php artisan serve --host=0.0.0.0 --port=8000`.)
3. **Postman**: importar `api-productos/postman/API_Productos.postman_collection.json` y ejecutar las 7 peticiones.
4. **Android**: abrir `app-android-productos` en Android Studio, arrancar el emulador y pulsar ▶ Run.
5. Hacer el CRUD desde la app y mostrar los cambios en phpMyAdmin (`http://localhost/phpmyadmin` → `productos_db` → `productos`).

Para volver a los 5 productos de ejemplo: `php artisan migrate:fresh --seed` (dentro de `api-productos`).

## Acceso desde internet (túnel con ngrok)
Permite usar la API desde otra red (por ejemplo, desde la universidad) mientras corre en la PC de casa.

1. En la PC donde corre la API: XAMPP → **Start** en MySQL y doble clic en `api-productos/iniciar.bat`.
2. Doble clic en **`api-productos/tunel.bat`** y dejar la ventana abierta.
   Verifica que ngrok tenga el authtoken registrado y que la API esté corriendo, y abre el túnel.
3. Desde cualquier lugar:
   - API: `https://pureness-outrank-scanning.ngrok-free.dev/api/productos`
     (en el navegador, ngrok muestra un aviso la primera vez → **Visit Site**).
   - App Android: compilar con esa URL e instalar el APK en el celular:
     ```bash
     cd app-android-productos
     gradlew assembleDebug -PapiBaseUrl=https://pureness-outrank-scanning.ngrok-free.dev/api/
     ```
     El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Requisitos (solo una vez): descargar ngrok en `%LOCALAPPDATA%\ngrok\` y registrar el token de la cuenta
(dashboard.ngrok.com → *Your Authtoken*) con `ngrok config add-authtoken <token>`.

⚠️ Mientras el túnel está abierto la API es pública y no tiene autenticación: cerrar `tunel.bat` al terminar.
Los datos se guardan en el MySQL de la PC donde corre la API.

## Endpoints
| Método | Ruta | Acción |
|--------|------|--------|
| GET | /api/productos | Listado de productos |
| GET | /api/productos/{id} | Un único producto |
| POST | /api/productos | Registrar producto |
| PUT | /api/productos/{id} | Modificar producto |
| DELETE | /api/productos/{id} | Eliminar producto |
