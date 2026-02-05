CREATE USER 'usuario_productos'@'localhost' IDENTIFIED BY 'productos2026';
GRANT ALL PRIVILEGES ON segundumProductos.* TO 'usuario_productos'@'localhost';
FLUSH PRIVILEGES;