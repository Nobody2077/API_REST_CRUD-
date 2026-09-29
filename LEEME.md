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

## Endpoints
| Método | Ruta | Acción |
|--------|------|--------|
| GET | /api/productos | Listado de productos |
| GET | /api/productos/{id} | Un único producto |
| POST | /api/productos | Registrar producto |
| PUT | /api/productos/{id} | Modificar producto |
| DELETE | /api/productos/{id} | Eliminar producto |
