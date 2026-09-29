<?php

namespace App\Http\Controllers;

use App\Http\Requests\ProductoRequest;
use App\Http\Resources\ProductoResource;
use App\Models\Producto;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Resources\Json\AnonymousResourceCollection;
use Symfony\Component\HttpFoundation\Response;

/**
 * CRUD de productos.
 *
 * - La validación vive en ProductoRequest.
 * - El formato de salida vive en ProductoResource.
 * - Si el {producto} no existe, Laravel responde 404 automáticamente (route model binding)
 *   y bootstrap/app.php convierte ese error en JSON.
 */
class ProductoController extends Controller
{
    /**
     * GET /api/productos
     * Listado de productos.
     */
    public function index(): AnonymousResourceCollection
    {
        return ProductoResource::collection(Producto::orderBy('id')->get());
    }

    /**
     * GET /api/productos/{producto}
     * Muestra un único producto.
     */
    public function show(Producto $producto): ProductoResource
    {
        return new ProductoResource($producto);
    }

    /**
     * POST /api/productos
     * Registra un producto nuevo.
     */
    public function store(ProductoRequest $request): JsonResponse
    {
        $producto = Producto::create($request->validated());

        return (new ProductoResource($producto))
            ->response()
            ->setStatusCode(Response::HTTP_CREATED);
    }

    /**
     * PUT /api/productos/{producto}
     * Modifica los datos de un producto.
     */
    public function update(ProductoRequest $request, Producto $producto): ProductoResource
    {
        $producto->update($request->validated());

        return new ProductoResource($producto);
    }

    /**
     * DELETE /api/productos/{producto}
     * Elimina un producto.
     */
    public function destroy(Producto $producto): JsonResponse
    {
        $producto->delete();

        return response()->json(
            ['mensaje' => 'Producto eliminado correctamente.'],
            Response::HTTP_OK,
            [],
            JSON_UNESCAPED_UNICODE
        );
    }
}
