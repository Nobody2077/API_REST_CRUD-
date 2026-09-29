<?php

use App\Http\Controllers\ProductoController;
use Illuminate\Support\Facades\Route;

// GET    /api/productos        -> index   (listado de productos)
// GET    /api/productos/{id}   -> show    (un solo producto)
// POST   /api/productos        -> store   (registrar producto)
// PUT    /api/productos/{id}   -> update  (modificar producto)
// DELETE /api/productos/{id}   -> destroy (eliminar producto)
Route::apiResource('productos', ProductoController::class);
