package com.jcyq.productos.data.remote.dto

/**
 * Cuerpo JSON que se envía en POST /productos y PUT /productos/{id}.
 */
data class ProductoPeticion(
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int
)
