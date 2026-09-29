# API REST CRUD de Productos (Laravel 12 + MySQL)

## Requisitos
- XAMPP con **MySQL encendido**
- PHP 8.2 (el de XAMPP) y Composer

## Cómo levantar la API
```bash
cd "G:\Desarrollo\Tareas\Desarrollo de una API REST CRUD\api-productos"
php artisan serve --host=0.0.0.0 --port=8000
```
La API queda en `http://127.0.0.1:8000/api/productos`.

Para reiniciar la base con los 5 productos de ejemplo:
```bash
php artisan migrate:fresh --seed
```

## Endpoints
| Método | Ruta                  | Acción                 | Respuesta OK |
|--------|-----------------------|------------------------|--------------|
| GET    | /api/productos        | Listado de productos   | 200          |
| GET    | /api/productos/{id}   | Un solo producto       | 200 / 404    |
| POST   | /api/productos        | Registrar producto     | 201 / 422    |
| PUT    | /api/productos/{id}   | Modificar producto     | 200 / 404 / 422 |
| DELETE | /api/productos/{id}   | Eliminar producto      | 200 / 404    |

Cuerpo JSON para POST y PUT:
```json
{ "nombre": "Mouse Logitech", "descripcion": "Inalámbrico", "precio": 59.90, "stock": 50 }
```
- `nombre`: obligatorio, máximo 100 caracteres
- `descripcion`: opcional
- `precio`: obligatorio, número >= 0
- `stock`: obligatorio, entero >= 0

## Formato de las respuestas
Éxito (un producto; en el listado `data` es un arreglo):
```json
{ "data": { "id": 1, "nombre": "Mouse Logitech", "descripcion": "Inalámbrico", "precio": 59.9, "stock": 50,
            "creado_en": "2026-09-28T23:57:43+00:00", "actualizado_en": "2026-09-28T23:57:43+00:00" } }
```
Eliminación: `{ "mensaje": "Producto eliminado correctamente." }`

Errores:
- 404 → `{ "mensaje": "Producto no encontrado." }`
- 422 → `{ "mensaje": "Los datos enviados no son válidos.", "errores": { "precio": ["El precio no puede ser negativo."] } }`
- 405 → `{ "mensaje": "Método no permitido para esta ruta." }`

## Pruebas en Postman / Thunder Client
Importar `postman/API_Productos.postman_collection.json`.
La petición "3. Registrar producto" guarda el `id` creado, así "4. Modificar" y "5. Eliminar" actúan sobre ese producto.

## Tests automáticos
```bash
php artisan test
```
Usan SQLite en memoria (ver `phpunit.xml`), no modifican la base MySQL.

## Estructura (cada capa en su carpeta)
| Archivo | Responsabilidad |
|---------|-----------------|
| `routes/api.php` | Rutas: `Route::apiResource('productos', ...)` |
| `app/Http/Controllers/ProductoController.php` | Controlador delgado: coordina cada operación CRUD |
| `app/Http/Requests/ProductoRequest.php` | Validación de entrada y mensajes en español (Form Request) |
| `app/Http/Resources/ProductoResource.php` | Formato JSON de salida (API Resource) |
| `app/Models/Producto.php` | Modelo Eloquent (`$fillable`, casts) |
| `bootstrap/app.php` | Manejo global de errores de la API (404, 405, 422) en JSON |
| `database/migrations/*_create_productos_table.php` | Estructura de la tabla |
| `database/factories/ProductoFactory.php` | Datos falsos para tests |
| `database/seeders/ProductoSeeder.php` | Datos de ejemplo |
| `tests/Feature/ProductoApiTest.php` | Tests de los 5 endpoints |

Buenas prácticas aplicadas: controlador delgado, validación fuera del controlador,
route model binding (Laravel busca el producto y devuelve 404 si no existe),
asignación masiva protegida con `$fillable` + `validated()`, códigos HTTP correctos
(200, 201, 404, 405, 422) y tests automatizados.

## Conexión desde Android
- Emulador de Android Studio: `http://10.0.2.2:8000/api/`
- Celular físico (misma red Wi-Fi): `http://<IP-de-tu-PC>:8000/api/` (ver IP con `ipconfig`)
  y permitir el puerto 8000 en el Firewall de Windows si no conecta.
