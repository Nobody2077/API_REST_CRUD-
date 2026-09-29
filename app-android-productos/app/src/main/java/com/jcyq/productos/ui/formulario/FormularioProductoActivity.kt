package com.jcyq.productos.ui.formulario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.jcyq.productos.R
import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.databinding.ActivityFormularioProductoBinding
import com.jcyq.productos.util.aplicarPaddingBarrasSistema
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Formulario para registrar (POST) o modificar (PUT) un producto.
 */
class FormularioProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioProductoBinding

    private val viewModel: FormularioProductoViewModel by viewModels {
        val id = intent.getLongExtra(EXTRA_ID_PRODUCTO, SIN_ID).takeIf { it != SIN_ID }
        FormularioProductoViewModel.factory(id)
    }

    // Evita sobrescribir lo que el usuario escribió al rotar la pantalla
    private var camposLlenados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormularioProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarPaddingBarrasSistema()

        camposLlenados = savedInstanceState?.getBoolean(ESTADO_CAMPOS_LLENADOS) ?: false

        binding.toolbar.setTitle(if (viewModel.esEdicion) R.string.titulo_editar else R.string.titulo_nuevo)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.botonGuardar.setText(if (viewModel.esEdicion) R.string.guardar_cambios else R.string.registrar)
        binding.botonGuardar.setOnClickListener { guardar() }

        binding.inputNombre.doAfterTextChanged { viewModel.limpiarError("nombre") }
        binding.inputDescripcion.doAfterTextChanged { viewModel.limpiarError("descripcion") }
        binding.inputPrecio.doAfterTextChanged { viewModel.limpiarError("precio") }
        binding.inputStock.doAfterTextChanged { viewModel.limpiarError("stock") }

        observarViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ESTADO_CAMPOS_LLENADOS, camposLlenados)
    }

    private fun guardar() {
        viewModel.guardar(
            nombre = binding.inputNombre.text.toString(),
            descripcion = binding.inputDescripcion.text.toString(),
            precioTexto = binding.inputPrecio.text.toString(),
            stockTexto = binding.inputStock.text.toString()
        )
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect(::mostrarEstado)
            }
        }
    }

    private fun mostrarEstado(estado: FormularioUiState) {
        binding.progreso.isVisible = estado.cargando
        binding.botonGuardar.isEnabled = !estado.cargando

        if (!camposLlenados && estado.producto != null) {
            llenarCampos(estado.producto)
            camposLlenados = true
        }

        binding.campoNombre.error = estado.errores["nombre"]
        binding.campoDescripcion.error = estado.errores["descripcion"]
        binding.campoPrecio.error = estado.errores["precio"]
        binding.campoStock.error = estado.errores["stock"]

        estado.mensajeError?.let { mensaje ->
            Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
            viewModel.mensajeMostrado()
        }

        if (estado.guardado) {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun llenarCampos(producto: Producto) {
        binding.inputNombre.setText(producto.nombre)
        binding.inputDescripcion.setText(producto.descripcion.orEmpty())
        binding.inputPrecio.setText(String.format(Locale.US, "%.2f", producto.precio))
        binding.inputStock.setText(producto.stock.toString())
    }

    companion object {
        private const val EXTRA_ID_PRODUCTO = "id_producto"
        private const val ESTADO_CAMPOS_LLENADOS = "campos_llenados"
        private const val SIN_ID = -1L

        /** @param idProducto null para registrar uno nuevo; un id para editarlo. */
        fun crearIntent(contexto: Context, idProducto: Long?): Intent =
            Intent(contexto, FormularioProductoActivity::class.java).apply {
                idProducto?.let { putExtra(EXTRA_ID_PRODUCTO, it) }
            }
    }
}
