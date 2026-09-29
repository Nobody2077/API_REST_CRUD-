package com.jcyq.productos.data.repository

import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.data.remote.ProductoApiService
import com.jcyq.productos.data.remote.RetrofitClient
import com.jcyq.productos.data.remote.dto.ErrorRespuesta
import com.jcyq.productos.data.remote.dto.ProductoPeticion
import com.jcyq.productos.util.Resultado
import com.jcyq.productos.util.map
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

/**
 * Única puerta de acceso a los datos de productos.
 * Las pantallas (ViewModels) no conocen Retrofit: reciben siempre un [Resultado].
 */
class ProductoRepository(
    private val api: ProductoApiService = RetrofitClient.api
) {

    suspend fun listar(): Resultado<List<Producto>> =
        llamar { api.listar() }.map { it.data }

    suspend fun obtener(id: Long): Resultado<Producto> =
        llamar { api.obtener(id) }.map { it.data }

    suspend fun crear(producto: ProductoPeticion): Resultado<Producto> =
        llamar { api.crear(producto) }.map { it.data }

    suspend fun actualizar(id: Long, producto: ProductoPeticion): Resultado<Producto> =
        llamar { api.actualizar(id, producto) }.map { it.data }

    suspend fun eliminar(id: Long): Resultado<String> =
        llamar { api.eliminar(id) }.map { it.mensaje }

    /**
     * Ejecuta la petición y traduce la respuesta (o la falla de red) a un [Resultado].
     */
    private suspend fun <T> llamar(peticion: suspend () -> Response<T>): Resultado<T> {
        return try {
            val respuesta = peticion()
            val cuerpo = respuesta.body()
            if (respuesta.isSuccessful && cuerpo != null) {
                Resultado.Exito(cuerpo)
            } else {
                leerError(respuesta)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Resultado.Error("No se pudo conectar con el servidor. Verifica que la API esté encendida.")
        } catch (e: Exception) {
            Resultado.Error("Error inesperado: ${e.message}")
        }
    }

    private fun leerError(respuesta: Response<*>): Resultado.Error {
        val error = try {
            respuesta.errorBody()?.charStream()?.use {
                RetrofitClient.gson.fromJson(it, ErrorRespuesta::class.java)
            }
        } catch (e: Exception) {
            null
        }
        return Resultado.Error(
            mensaje = error?.mensaje ?: "Error del servidor (código ${respuesta.code()}).",
            errores = error?.errores.orEmpty()
        )
    }
}
