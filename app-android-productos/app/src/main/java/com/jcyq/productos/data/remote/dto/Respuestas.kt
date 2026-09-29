package com.jcyq.productos.data.remote.dto

/**
 * Respuesta exitosa de la API: los datos vienen dentro de "data".
 * Ejemplo: { "data": { "id": 1, "nombre": "..." } }
 */
data class DataRespuesta<T>(
    val data: T
)

/**
 * Respuesta con solo un mensaje. Ejemplo: { "mensaje": "Producto eliminado correctamente." }
 */
data class MensajeRespuesta(
    val mensaje: String
)

/**
 * Respuesta de error de la API (404, 422, ...).
 * Ejemplo: { "mensaje": "Los datos enviados no son válidos.", "errores": { "precio": ["..."] } }
 */
data class ErrorRespuesta(
    val mensaje: String?,
    val errores: Map<String, List<String>>?
)
