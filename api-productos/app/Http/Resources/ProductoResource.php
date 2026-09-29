<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

/**
 * Define el formato JSON con el que la API devuelve un producto.
 *
 * @mixin \App\Models\Producto
 */
class ProductoResource extends JsonResource
{
    /**
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'nombre' => $this->nombre,
            'descripcion' => $this->descripcion,
            'precio' => (float) $this->precio,
            'stock' => (int) $this->stock,
            'creado_en' => $this->created_at?->toIso8601String(),
            'actualizado_en' => $this->updated_at?->toIso8601String(),
        ];
    }

    /**
     * Acentos legibles en el JSON ("Audífonos" en lugar de "Audífonos").
     */
    public function jsonOptions(): int
    {
        return JSON_UNESCAPED_UNICODE;
    }
}
