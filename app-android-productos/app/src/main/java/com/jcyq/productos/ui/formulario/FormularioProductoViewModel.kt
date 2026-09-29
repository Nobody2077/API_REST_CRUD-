package com.jcyq.productos.ui.formulario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.data.remote.dto.ProductoPeticion
import com.jcyq.productos.data.repository.ProductoRepository
import com.jcyq.productos.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado de la pantalla de formulario. */
data class FormularioUiState(
    val cargando: Boolean = false,
    val producto: Producto? = null,
    /** Error por campo: "nombre", "descripcion", "precio", "stock". */
    val errores: Map<String, String> = emptyMap(),
    val mensajeError: String? = null,
    val guardado: Boolean = false
)

/**
 * Lógica del formulario:
 * - Nuevo producto  -> POST /productos
 * - Editar producto -> GET /productos/{id} para cargarlo, y PUT /productos/{id} para guardarlo
 */
class FormularioProductoViewModel(
    private val repositorio: ProductoRepository,
    private val idProducto: Long?
) : ViewModel() {

    private val _estado = MutableStateFlow(FormularioUiState())
    val estado: StateFlow<FormularioUiState> = _estado.asStateFlow()

    val esEdicion: Boolean get() = idProducto != null

    init {
        idProducto?.let(::cargarProducto)
    }

    private fun cargarProducto(id: Long) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            when (val resultado = repositorio.obtener(id)) {
                is Resultado.Exito -> _estado.update { it.copy(cargando = false, producto = resultado.datos) }
                is Resultado.Error -> _estado.update { it.copy(cargando = false, mensajeError = resultado.mensaje) }
            }
        }
    }

    fun guardar(nombre: String, descripcion: String, precioTexto: String, stockTexto: String) {
        val precio = precioTexto.replace(',', '.').toDoubleOrNull()
        val stock = stockTexto.toIntOrNull()

        // Validación local rápida (la API vuelve a validar y responde 422 si algo falla)
        val errores = buildMap {
            if (nombre.isBlank()) put("nombre", "El nombre es obligatorio.")
            if (precio == null) put("precio", "Ingresa un precio válido.")
            else if (precio < 0) put("precio", "El precio no puede ser negativo.")
            if (stock == null) put("stock", "Ingresa un stock válido (número entero).")
            else if (stock < 0) put("stock", "El stock no puede ser negativo.")
        }
        if (errores.isNotEmpty() || precio == null || stock == null) {
            _estado.update { it.copy(errores = errores) }
            return
        }

        val peticion = ProductoPeticion(
            nombre = nombre.trim(),
            descripcion = descripcion.trim().ifBlank { null },
            precio = precio,
            stock = stock
        )

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errores = emptyMap(), mensajeError = null) }
            val resultado = if (idProducto == null) {
                repositorio.crear(peticion)
            } else {
                repositorio.actualizar(idProducto, peticion)
            }
            when (resultado) {
                is Resultado.Exito -> _estado.update { it.copy(cargando = false, guardado = true) }
                is Resultado.Error -> _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeError = resultado.mensaje,
                        errores = resultado.errores.mapValues { (_, mensajes) -> mensajes.first() }
                    )
                }
            }
        }
    }

    /** El usuario está corrigiendo el campo: se quita su mensaje de error. */
    fun limpiarError(campo: String) {
        if (campo in _estado.value.errores) {
            _estado.update { it.copy(errores = it.errores - campo) }
        }
    }

    /** La pantalla ya mostró el mensaje de error; se limpia para no repetirlo. */
    fun mensajeMostrado() {
        _estado.update { it.copy(mensajeError = null) }
    }

    companion object {
        fun factory(idProducto: Long?) = viewModelFactory {
            initializer { FormularioProductoViewModel(ProductoRepository(), idProducto) }
        }
    }
}
