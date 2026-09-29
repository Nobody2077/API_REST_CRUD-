<?php

namespace Tests\Feature;

use App\Models\Producto;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

/**
 * Tests de los 5 endpoints del CRUD.
 * Usan SQLite en memoria (phpunit.xml), no tocan la base MySQL real.
 */
class ProductoApiTest extends TestCase
{
    use RefreshDatabase;

    private array $datosValidos = [
        'nombre' => 'Audífonos JBL Tune 510BT',
        'descripcion' => 'Bluetooth 5.0',
        'precio' => 229.90,
        'stock' => 20,
    ];

    public function test_lista_los_productos(): void
    {
        Producto::factory()->count(3)->create();

        $this->getJson('/api/productos')
            ->assertOk()
            ->assertJsonCount(3, 'data')
            ->assertJsonStructure(['data' => [['id', 'nombre', 'descripcion', 'precio', 'stock']]]);
    }

    public function test_muestra_un_producto(): void
    {
        $producto = Producto::factory()->create();

        $this->getJson("/api/productos/{$producto->id}")
            ->assertOk()
            ->assertJsonPath('data.id', $producto->id)
            ->assertJsonPath('data.nombre', $producto->nombre);
    }

    public function test_devuelve_404_si_el_producto_no_existe(): void
    {
        $this->getJson('/api/productos/9999')
            ->assertNotFound()
            ->assertJson(['mensaje' => 'Producto no encontrado.']);
    }

    public function test_registra_un_producto(): void
    {
        $this->postJson('/api/productos', $this->datosValidos)
            ->assertCreated()
            ->assertJsonPath('data.nombre', 'Audífonos JBL Tune 510BT')
            ->assertJsonPath('data.precio', 229.9);

        $this->assertDatabaseHas('productos', ['nombre' => 'Audífonos JBL Tune 510BT', 'stock' => 20]);
    }

    public function test_no_registra_un_producto_con_datos_invalidos(): void
    {
        $this->postJson('/api/productos', ['nombre' => '', 'precio' => -5, 'stock' => 'abc'])
            ->assertUnprocessable()
            ->assertJsonPath('mensaje', 'Los datos enviados no son válidos.')
            ->assertJsonPath('errores.nombre.0', 'El nombre es obligatorio.')
            ->assertJsonPath('errores.precio.0', 'El precio no puede ser negativo.')
            ->assertJsonPath('errores.stock.0', 'El stock debe ser un número entero.');

        $this->assertDatabaseCount('productos', 0);
    }

    public function test_modifica_un_producto(): void
    {
        $producto = Producto::factory()->create();

        $this->putJson("/api/productos/{$producto->id}", $this->datosValidos)
            ->assertOk()
            ->assertJsonPath('data.nombre', 'Audífonos JBL Tune 510BT');

        $this->assertDatabaseHas('productos', ['id' => $producto->id, 'nombre' => 'Audífonos JBL Tune 510BT']);
    }

    public function test_elimina_un_producto(): void
    {
        $producto = Producto::factory()->create();

        $this->deleteJson("/api/productos/{$producto->id}")
            ->assertOk()
            ->assertJson(['mensaje' => 'Producto eliminado correctamente.']);

        $this->assertDatabaseMissing('productos', ['id' => $producto->id]);
    }
}
