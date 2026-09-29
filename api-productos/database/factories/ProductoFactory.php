<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * Genera productos falsos para los tests.
 *
 * @extends Factory<\App\Models\Producto>
 */
class ProductoFactory extends Factory
{
    /**
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'nombre' => ucfirst(fake()->words(3, true)),
            'descripcion' => fake()->sentence(),
            'precio' => fake()->randomFloat(2, 1, 5000),
            'stock' => fake()->numberBetween(0, 200),
        ];
    }
}
