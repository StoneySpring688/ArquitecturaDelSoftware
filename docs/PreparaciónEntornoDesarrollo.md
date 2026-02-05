# Mysql
El primer requisito es instalar el motor de bases de datos mysql.
No se proporciona un comando concreto ya que varía según el sistema operativo.

[Instalación](https://dev.mysql.com/downloads/)

Se recomienda instalar **MysqlWorkbench** junto al motor mysql.

# Preparación de las bases de datos

> [!IMPORTANT]
> Esta sección debe realizarse desde el usuario root de mysql.

Para preparar las bases de datos se proporcionan una serie de scripts, los cuales deben ejecutarse en el siguiente orden:

1. [hacer base de datos de productos](../mysqlScripts/hacerDatabaseProductos.sql)
2. [hacer base de datos de usuarios](../mysqlScripts/hacerDatabaseUsuarios.sql)
3. [hacer usuario para microservicio de productos](../mysqlScripts/usuarioProductos.sql)
4. [hacer usuario para microservicio de usuarios](../mysqlScripts/usuariosUsuarios.sql)

También se proporcionan scripts para reiniciar las bases de datos :
- [reiniciar base de datos de productos](../mysqlScripts/resetDatabaseProductos.sql)
- [reiniciar base de datos de usuarios](../mysqlScripts/resetDatabaseUsuarios.sql)

Las credeenciales de los usuarios son :

**Microservicio Productos:**
- *usuario:* usuario_productos
- *clave:* productos2026

**Microservico Usuarios:**
- *usuario:* usuario_usuarios
- *clave:* usuarios2026
