<?php
// Ayudante de iniciar.bat. Lee los datos DB_* del archivo .env y:
//   php scripts/preparar-bd.php crear  -> crea la base si no existe (sale con 2 si MySQL está apagado)
//   php scripts/preparar-bd.php vacia  -> sale con 0 si la tabla productos está vacía

$env = [];
foreach (file(__DIR__ . '/../.env', FILE_IGNORE_NEW_LINES) as $linea) {
    if (preg_match('/^\s*(DB_\w+)\s*=\s*"?([^"#]*?)"?\s*$/', $linea, $m)) {
        $env[$m[1]] = $m[2];
    }
}
$host = $env['DB_HOST'] ?? '127.0.0.1';
$port = $env['DB_PORT'] ?? '3306';
$base = $env['DB_DATABASE'] ?? 'productos_db';
$user = $env['DB_USERNAME'] ?? 'root';
$pass = $env['DB_PASSWORD'] ?? '';

try {
    $pdo = new PDO("mysql:host=$host;port=$port", $user, $pass, [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]);
} catch (PDOException $e) {
    fwrite(STDERR, "No se pudo conectar a MySQL en $host:$port -> " . $e->getMessage() . PHP_EOL);
    exit(2);
}

switch ($argv[1] ?? '') {
    case 'crear':
        $pdo->exec("CREATE DATABASE IF NOT EXISTS `$base` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        echo "Base de datos '$base' lista." . PHP_EOL;
        exit(0);
    case 'vacia':
        $total = (int) $pdo->query("SELECT COUNT(*) FROM `$base`.`productos`")->fetchColumn();
        exit($total === 0 ? 0 : 1);
    default:
        fwrite(STDERR, "Uso: php scripts/preparar-bd.php crear|vacia" . PHP_EOL);
        exit(1);
}
