<?php

namespace App\Models;

use Database\Factories\ProductoFactory;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Producto extends Model
{
    /** @use HasFactory<ProductoFactory> */
    use HasFactory;

    protected $table = 'productos';

    /**
     * Campos que se pueden asignar en masa (POST / PUT).
     *
     * @var list<string>
     */
    protected $fillable = [
        'nombre',
        'descripcion',
        'precio',
        'stock',
    ];

    /**
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'precio' => 'decimal:2',
            'stock' => 'integer',
        ];
    }
}
