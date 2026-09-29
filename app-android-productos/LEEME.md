# App Android – Cliente de la API de Productos (Kotlin)

Aplicación Android que consume la API REST Laravel (`../api-productos`) y permite
**listar, consultar, registrar, modificar y eliminar** productos.

## Requisitos
1. La API encendida: MySQL en XAMPP + `php artisan serve --host=0.0.0.0 --port=8000`
2. Android Studio (abrir esta carpeta `app-android-productos`)
3. Un emulador (p. ej. `Pixel_5`) o un celular

## URL de la API
Se configura en `gradle.properties`:
```properties
apiBaseUrl=http://10.0.2.2:8000/api/
```
- **Emulador**: `10.0.2.2` es la PC vista desde el emulador (no cambiar nada).
- **Celular físico** (misma Wi-Fi): poner la IP de la PC, p. ej. `http://192.168.1.50:8000/api/`
  (ver con `ipconfig`) y permitir el puerto 8000 en el Firewall de Windows.

## Compilar desde consola (opcional)
```bash
gradlew.bat assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

> El JDK de Android Studio en esta PC es Java 25 y Gradle 8.x no lo soporta; el proyecto
> ya está fijado a Temurin 17 en `gradle.properties` (consola) y `.gradle/config.properties` (Android Studio).

## Arquitectura (MVVM + Repository)
```
Pantalla (Activity)  →  ViewModel  →  Repository  →  Retrofit (ApiService)  →  API Laravel  →  MySQL
```

```
app/src/main/java/com/jcyq/productos/
├── data/
│   ├── model/
│   │   └── Producto.kt                 Modelo de dominio
│   ├── remote/
│   │   ├── dto/
│   │   │   ├── ProductoPeticion.kt     Cuerpo JSON de POST / PUT
│   │   │   └── Respuestas.kt           { "data": ... }, { "mensaje": ... }, errores 422
│   │   ├── ProductoApiService.kt       Endpoints (GET, POST, PUT, DELETE)
│   │   └── RetrofitClient.kt           Configuración de Retrofit/OkHttp
│   └── repository/
│       └── ProductoRepository.kt       Llama a la API y traduce respuestas/errores a Resultado
├── ui/
│   ├── lista/
│   │   ├── ListaProductosActivity.kt   Pantalla principal (listar + eliminar)
│   │   ├── ListaProductosViewModel.kt
│   │   └── ProductoAdapter.kt          RecyclerView (ListAdapter + DiffUtil)
│   └── formulario/
│       ├── FormularioProductoActivity.kt  Registrar / modificar
│       └── FormularioProductoViewModel.kt
└── util/
    ├── Resultado.kt                    Éxito / Error de cada operación
    └── Insets.kt                       Márgenes para barras del sistema (edge-to-edge)
```

## Qué endpoint usa cada acción
| Acción en la app | Petición HTTP |
|------------------|---------------|
| Abrir la app / deslizar hacia abajo | `GET /api/productos` |
| Tocar un producto o el lápiz | `GET /api/productos/{id}` (carga el formulario) |
| Botón «Nuevo» → «Registrar» | `POST /api/productos` |
| «Guardar cambios» | `PUT /api/productos/{id}` |
| Papelera → «Eliminar» | `DELETE /api/productos/{id}` |

## Buenas prácticas aplicadas
- Separación por capas (datos / UI / utilidades) y patrón **MVVM + Repository**.
- **ViewModel + StateFlow**: el estado sobrevive a la rotación de pantalla.
- **Corrutinas** (`suspend`) para las peticiones de red, sin bloquear la interfaz.
- **ViewBinding** en lugar de `findViewById`.
- Validación local + errores de la API (422) mostrados en cada campo.
- Mensajes claros si la API está apagada o sin conexión.
- Textos en `strings.xml`, colores para modo claro y oscuro.
- URL de la API configurable sin tocar el código.
