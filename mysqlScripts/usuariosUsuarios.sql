CREATE USER 'usuario_usuarios'@'localhost' IDENTIFIED BY 'usuarios2026';
GRANT ALL PRIVILEGES ON segundumUsuarios.* TO 'usuario_usuarios'@'localhost';
FLUSH PRIVILEGES;