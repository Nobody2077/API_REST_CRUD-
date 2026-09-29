<?php

namespace Database\Seeders;

use App\Models\Producto;
use Illuminate\Database\Seeder;

class ProductoSeeder extends Seeder
{
    /**
     * Carga productos de ejemplo.
     */
    public function run(): void
    {
        $productos = [
            ['nombre' => 'Laptop Lenovo IdeaPad 3', 'descripcion' => 'Laptop 15.6", Ryzen 5, 8 GB RAM, 512 GB SSD', 'precio' => 3499.90, 'stock' => 10],
            ['nombre' => 'Mouse inalámbrico Logitech M170', 'descripcion' => 'Mouse óptico inalámbrico USB', 'precio' => 59.90, 'stock' => 50],
            ['nombre' => 'Teclado mecánico Redragon Kumara', 'descripcion' => 'Teclado mecánico retroiluminado, switch blue', 'precio' => 189.00, 'stock' => 25],
            ['nombre' => 'Monitor Samsung 24"', 'descripcion' => 'Monitor Full HD IPS 75 Hz', 'precio' => 699.00, 'stock' => 15],
            ['nombre' => 'Memoria USB Kingston 64 GB', 'descripcion' => 'USB 3.2 Gen 1', 'precio' => 35.50, 'stock' => 100],
        ];

        foreach ($productos as $producto) {
            Producto::create($producto);
        }
    }
}
