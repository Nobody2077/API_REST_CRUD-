package com.jcyq.productos.ui.lista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcyq.productos.R
import com.jcyq.productos.data.model.Producto
import com.jcyq.productos.databinding.ItemProductoBinding

/**
 * Muestra cada producto como una tarjeta con botones de editar y eliminar.
 */
class ProductoAdapter(
    private val onEditar: (Producto) -> Unit,
    private val onEliminar: (Producto) -> Unit
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ComparadorProductos) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.mostrar(getItem(position))
    }

    inner class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun mostrar(producto: Producto) {
            val contexto = binding.root.context
            binding.textoNombre.text = producto.nombre
            binding.textoDescripcion.text = producto.descripcion
            binding.textoDescripcion.isVisible = !producto.descripcion.isNullOrBlank()
            binding.textoPrecio.text = contexto.getString(R.string.formato_precio, producto.precio)
            binding.textoStock.text = contexto.getString(R.string.formato_stock, producto.stock)

            binding.root.setOnClickListener { onEditar(producto) }
            binding.botonEditar.setOnClickListener { onEditar(producto) }
            binding.botonEliminar.setOnClickListener { onEliminar(producto) }
        }
    }

    private object ComparadorProductos : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(anterior: Producto, nuevo: Producto) = anterior.id == nuevo.id
        override fun areContentsTheSame(anterior: Producto, nuevo: Producto) = anterior == nuevo
    }
}
