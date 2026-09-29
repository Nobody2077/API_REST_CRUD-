<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

/**
 * Valida los datos de un producto al registrarlo (POST) o modificarlo (PUT).
 * PUT reemplaza el recurso completo, por eso ambas operaciones usan las mismas reglas.
 */
class ProductoRequest extends FormRequest
{
    /**
     * La API es pública (sin autenticación), cualquiera puede hacer la petición.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Reglas de validación.
     *
     * @return array<string, \Illuminate\Contracts\Validation\ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        return [
            'nombre' => ['required', 'string', 'max:100'],
            'descripcion' => ['nullable', 'string', 'max:1000'],
            'precio' => ['required', 'numeric', 'min:0', 'max:99999999.99'],
            'stock' => ['required', 'integer', 'min:0'],
        ];
    }

    /**
     * Mensajes de error en español.
     *
     * @return array<string, string>
     */
    public function messages(): array
    {
        return [
            'nombre.required' => 'El nombre es obligatorio.',
            'nombre.string' => 'El nombre debe ser un texto.',
            'nombre.max' => 'El nombre no puede tener más de 100 caracteres.',
            'descripcion.string' => 'La descripción debe ser un texto.',
            'descripcion.max' => 'La descripción no puede tener más de 1000 caracteres.',
            'precio.required' => 'El precio es obligatorio.',
            'precio.numeric' => 'El precio debe ser un número.',
            'precio.min' => 'El precio no puede ser negativo.',
            'precio.max' => 'El precio es demasiado alto.',
            'stock.required' => 'El stock es obligatorio.',
            'stock.integer' => 'El stock debe ser un número entero.',
            'stock.min' => 'El stock no puede ser negativo.',
        ];
    }
}
