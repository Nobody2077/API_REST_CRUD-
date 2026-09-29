<?php

use Illuminate\Database\Eloquent\ModelNotFoundException;
use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;
use Symfony\Component\HttpKernel\Exception\MethodNotAllowedHttpException;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        //
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        // Errores de la API siempre en JSON, en español y con acentos legibles.
        $json = fn (array $cuerpo, int $estado) => response()->json($cuerpo, $estado, [], JSON_UNESCAPED_UNICODE);

        // 422: datos inválidos
        $exceptions->render(function (ValidationException $e, Request $request) use ($json) {
            if ($request->is('api/*')) {
                return $json([
                    'mensaje' => 'Los datos enviados no son válidos.',
                    'errores' => $e->errors(),
                ], 422);
            }
        });

        // 404: el producto (o la ruta) no existe
        $exceptions->render(function (NotFoundHttpException $e, Request $request) use ($json) {
            if ($request->is('api/*')) {
                $mensaje = $e->getPrevious() instanceof ModelNotFoundException
                    ? 'Producto no encontrado.'
                    : 'Recurso no encontrado.';

                return $json(['mensaje' => $mensaje], 404);
            }
        });

        // 405: método HTTP no permitido en esa ruta
        $exceptions->render(function (MethodNotAllowedHttpException $e, Request $request) use ($json) {
            if ($request->is('api/*')) {
                return $json(['mensaje' => 'Método no permitido para esta ruta.'], 405);
            }
        });

        // Cualquier otro error de /api/* también se devuelve como JSON
        $exceptions->shouldRenderJsonWhen(function (Request $request, Throwable $e) {
            return $request->is('api/*') || $request->expectsJson();
        });
    })->create();
