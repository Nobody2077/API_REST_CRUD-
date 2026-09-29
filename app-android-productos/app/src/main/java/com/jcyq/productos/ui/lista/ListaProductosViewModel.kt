package com.jcyq.productos.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.data.repository.ProductoRepository
import com.jcyq.productos.util.Resultado
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** Estados posibles de la pantalla de listado. */
sealed interface ListaUiState {
    data object Cargando : ListaUiState
    data class Exito(val productos: List<Producto>) : ListaUiState
    data class Error(val mensaje: String) : ListaUiState
}

/**
 * Lógica de la pantalla de listado: consultar productos (GET) y eliminarlos (DELETE).
 */
class ListaProductosViewModel(
    private val repositorio: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<ListaUiState>(ListaUiState.Cargando)
    val estado: StateFlow<ListaUiState> = _estado.asStateFlow()

    // Mensajes de un solo uso (Snackbar)
    private val _mensajes = Channel<String>(Channel.BUFFERED)
    val mensajes: Flow<String> = _mensajes.receiveAsFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = ListaUiState.Cargando
            _estado.value = when (val resultado = repositorio.listar()) {
                is Resultado.Exito -> ListaUiState.Exito(resultado.datos)
                is Resultado.Error -> ListaUiState.Error(resultado.mensaje)
            }
        }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            when (val resultado = repositorio.eliminar(producto.id)) {
                is Resultado.Exito -> {
                    _mensajes.send(resultado.datos)
                    cargar()
                }
                is Resultado.Error -> _mensajes.send(resultado.mensaje)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ListaProductosViewModel(ProductoRepository()) }
        }
    }
}
