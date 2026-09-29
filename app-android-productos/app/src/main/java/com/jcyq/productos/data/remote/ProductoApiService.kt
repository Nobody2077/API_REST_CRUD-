package com.jcyq.productos.data.remote

import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.data.remote.dto.DataRespuesta
import com.jcyq.productos.data.remote.dto.MensajeRespuesta
import com.jcyq.productos.data.remote.dto.ProductoPeticion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Endpoints de la API REST de productos (Laravel).
 */
interface ProductoApiService {

    @GET("productos")
    suspend fun listar(): Response<DataRespuesta<List<Producto>>>

    @GET("productos/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<DataRespuesta<Producto>>

    @POST("productos")
    suspend fun crear(@Body producto: ProductoPeticion): Response<DataRespuesta<Producto>>

    @PUT("productos/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body producto: ProductoPeticion
    ): Response<DataRespuesta<Producto>>

    @DELETE("productos/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<MensajeRespuesta>
}
