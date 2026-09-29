package com.jcyq.productos.util

/**
 * Resultado de una operación contra la API: éxito con datos, o error con mensaje.
 */
sealed interface Resultado<out T> {

    data class Exito<T>(val datos: T) : Resultado<T>

    /**
     * @param errores errores de validación por campo (respuesta 422), p. ej. "precio" -> ["El precio no puede ser negativo."]
     */
    data class Error(
        val mensaje: String,
        val errores: Map<String, List<String>> = emptyMap()
    ) : Resultado<Nothing>
}

inline fun <T, R> Resultado<T>.map(transformar: (T) -> R): Resultado<R> = when (this) {
    is Resultado.Exito -> Resultado.Exito(transformar(datos))
    is Resultado.Error -> this
}
