-- ============================================
-- Inicialización de MySQL para Docker Compose
-- Crea las bases de datos y usuarios necesarios
-- para los microservicios Usuarios y Productos
-- ============================================

-- Base de datos para Usuarios
CREATE DATABASE IF NOT EXISTS segundumUsuarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Base de datos para Productos
CREATE DATABASE IF NOT EXISTS segundumProductos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usuario para el microservicio Usuarios
CREATE USER IF NOT EXISTS 'usuario_usuarios'@'%' IDENTIFIED BY 'usuarios2026';
GRANT ALL PRIVILEGES ON segundumUsuarios.* TO 'usuario_usuarios'@'%';

-- Usuario para el microservicio Productos
CREATE USER IF NOT EXISTS 'usuario_productos'@'%' IDENTIFIED BY 'productos2026';
GRANT ALL PRIVILEGES ON segundumProductos.* TO 'usuario_productos'@'%';

FLUSH PRIVILEGES;
