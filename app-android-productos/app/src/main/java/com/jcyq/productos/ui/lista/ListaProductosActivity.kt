package com.jcyq.productos.ui.lista

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jcyq.productos.R
import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.databinding.ActivityListaProductosBinding
import com.jcyq.productos.ui.formulario.FormularioProductoActivity
import com.jcyq.productos.util.aplicarPaddingBarrasSistema
import kotlinx.coroutines.launch

/**
 * Pantalla principal: listado de productos (GET /productos) y eliminación (DELETE).
 */
class ListaProductosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaProductosBinding
    private val viewModel: ListaProductosViewModel by viewModels { ListaProductosViewModel.Factory }

    private val adapter = ProductoAdapter(
        onEditar = { producto -> abrirFormulario(producto.id) },
        onEliminar = { producto -> confirmarEliminacion(producto) }
    )

    // Al volver del formulario con cambios guardados, se recarga la lista
    private val formularioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) viewModel.cargar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarPaddingBarrasSistema()
        setSupportActionBar(binding.toolbar)

        configurarLista()
        binding.botonAgregar.setOnClickListener { abrirFormulario(null) }
        observarViewModel()
    }

    private fun configurarLista() {
        binding.listaProductos.layoutManager = LinearLayoutManager(this)
        binding.listaProductos.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.cargar() }
        // El gesto de "deslizar para recargar" solo se activa si la lista está arriba del todo
        binding.swipeRefresh.setOnChildScrollUpCallback { _, _ ->
            binding.listaProductos.canScrollVertically(-1)
        }
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.estado.collect(::mostrarEstado) }
                launch {
                    viewModel.mensajes.collect { mensaje ->
                        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun mostrarEstado(estado: ListaUiState) {
        binding.swipeRefresh.isRefreshing = estado is ListaUiState.Cargando
        when (estado) {
            is ListaUiState.Cargando -> Unit
            is ListaUiState.Exito -> {
                adapter.submitList(estado.productos)
                binding.textoEstado.text = getString(R.string.lista_vacia)
                binding.textoEstado.isVisible = estado.productos.isEmpty()
            }
            is ListaUiState.Error -> {
                adapter.submitList(emptyList())
                binding.textoEstado.text = getString(R.string.error_con_reintento, estado.mensaje)
                binding.textoEstado.isVisible = true
            }
        }
    }

    private fun abrirFormulario(idProducto: Long?) {
        formularioLauncher.launch(FormularioProductoActivity.crearIntent(this, idProducto))
    }

    private fun confirmarEliminacion(producto: Producto) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.eliminar_titulo)
            .setMessage(getString(R.string.eliminar_mensaje, producto.nombre))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ -> viewModel.eliminar(producto) }
            .show()
    }
}
