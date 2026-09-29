package com.jcyq.productos.data.model

/**
 * Producto tal como lo devuelve la API.
 */
data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int
)
