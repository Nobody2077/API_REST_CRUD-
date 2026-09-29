package com.jcyq.productos.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Evita que el contenido quede debajo de la barra de estado, la de navegación o el teclado
 * (desde Android 15 las apps se dibujan de borde a borde).
 */
fun View.aplicarPaddingBarrasSistema() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { vista, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
        insets
    }
}
