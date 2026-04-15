# SegundUM

Este proyecto forma parte de la asignatura **Arquitectura del Software** en la **Universidad de Murcia (2025/2026)**. Consiste en la elaboración de un servicio de compraventa tomando como base el proyecto realizado en [**Aplicaciones Distribuidas**](https://github.com/StoneySpring688/aadd2025) y utilizando una arquitectura basada en microservicios.

## 🚀 Despliegue

### Versión en la Nube (OCI)
El servicio se encuentra desplegado y operativo en una instancia de Oracle Cloud Infrastructure y es accesible mediante su ip pública:
*   **API Gateway:** `http://79.72.53.253:8090/**`
*   **Documentación Interactiva:** [http://79.72.53.253:8090/swagger-ui.html](http://79.72.53.253:8090/swagger-ui.html)

## 🐋 Arrancar con Docker

```bash
cd Proyecto && docker compose up --build
```

Esto levanta todos los servicios (MySQL, MongoDB, RabbitMQ, Usuarios, Productos, Compraventas y Pasarela) en el orden correcto.

La API estará disponible en `http://localhost:8090`.

## 🔐 Credenciales de Prueba (Admin)
Para facilitar la evaluación de los endpoints protegidos, se ha habilitado el siguiente usuario administrador en el servicio desplegado:

*   **Email:** `admin@admin.com`
*   **Clave:** `admin123`
*   **ID de Usuario:** `admin-maravilloso`

---

## 🛠 API Endpoints

A través de la Pasarela (puerto 8090), se exponen los siguientes endpoints principales:

### 🔑 Autenticación (Pasarela)
| Método | Path | Auth Level | Parámetros (Query) | Body (JSON) | Return | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | PUBLIC | - | `LoginRequest` | `LoginResponse` | Inicia sesión y devuelve el token JWT. |
| `GET` | `/oauth2/authorization/github` | PUBLIC | - | - | `302 Redirect` | Inicia el flujo de autenticación social con GitHub. |
| `POST` | `/auth/logout` | USUARIO | - | - | `200 OK` | Cierra la sesión (elimina la cookie JWT). |

### 👤 Usuarios (Microservicio Usuarios)
| Método | Path | Auth Level | Parámetros (Query) | Body (JSON) | Return | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/usuarios/` | PUBLIC | `email, nombre, apellidos, clave, fechaNacimiento, telefono` | - | `String (ID)` | Registro público de un nuevo usuario. |
| `POST` | `/usuarios/github` | PUBLIC | `idGitHub, nombre, email` | - | `Usuario` | Registro automático desde GitHub OAuth2. |
| `GET` | `/usuarios/{id}` | USUARIO | - | - | `ResumenUsuario` | Obtener perfil detallado del usuario. |
| `PUT` | `/usuarios/{id}` | USUARIO | `nombre, apellidos, clave, fechaNacimiento, telefono` | - | `204 No Content` | Actualizar datos del perfil (solo propietario). |
| `DELETE` | `/usuarios/{id}` | USUARIO | - | - | `204 No Content` | Eliminar cuenta de usuario (solo propietario). |
| `GET` | `/usuarios/getAll` | USUARIO | - | - | `List<ResumenUsuario>` | Listado completo de usuarios. |
| `GET` | `/usuarios/verificar` | PUBLIC | `email, clave` | - | `Usuario` | Endpoint interno para verificación de credenciales. |
| `GET` | `/usuarios/verificar-github` | PUBLIC | `idGitHub` | - | `Usuario` | Endpoint interno para verificación de ID de GitHub. |

### 📦 Productos y Categorías (Microservicio Productos)
| Método | Path | Auth Level | Parámetros (Query) | Body (JSON) | Return | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/productos` | USUARIO | - | `AltaProductoDTO` | `String (ID)` | Publicar un nuevo producto para la venta. |
| `GET` | `/productos/{id}` | PUBLIC | - | - | `ProductoDTO` | Consultar detalles técnicos de un producto. |
| `PUT` | `/productos/{id}` | USUARIO | - | `ProductoUpdateDTO` | `204 No Content` | Modificar precio o descripción (solo vendedor). |
| `PUT` | `/productos/{id}/recogida` | USUARIO | - | `LugarRecogidaDTO` | `204 No Content` | Establecer coordenadas del punto de recogida. |
| `PUT` | `/productos/{id}/visualizaciones` | PUBLIC | - | - | `204 No Content` | Incrementar el contador de visitas. |
| `DELETE` | `/productos/{id}` | USUARIO | - | - | `204 No Content` | Retirar un producto de la venta. |
| `GET` | `/productos/buscar` | PUBLIC | `categoriaId, texto, estadoMinimo, precioMaximo` | - | `PagedModel` | Buscador con filtros y paginación. |
| `GET` | `/productos/vendedor/{id}` | PUBLIC | - | - | `PagedModel` | Listar productos de un vendedor específico. |
| `GET` | `/productos/historial` | PUBLIC | `mes, anio` | - | `PagedModel` | Estadísticas mensuales de productos. |
| `GET` | `/categorias/getAll` | PUBLIC | - | - | `PagedModel` | Listar jerarquía de categorías disponibles. |
| `POST` | `/categorias/cargar` | ADMINISTRADOR | - | - | `String` | Carga masiva de categorías (Solo Admin). |

### 🤝 Compraventas (Microservicio Compraventas)
| Método | Path | Auth Level | Parámetros (Query) | Body (JSON) | Return | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/compraventas` | USUARIO | - | `NuevaCompraventaDTO` | `201 Created` | Registrar una nueva transacción comercial. |
| `GET` | `/compraventas/comprador/{id}` | USUARIO | - | - | `PagedModel` | Historial de compras de un usuario. |
| `GET` | `/compraventas/vendedor/{id}` | USUARIO | - | - | `PagedModel` | Historial de ventas de un usuario. |
| `GET` | `/compraventas/entre` | ADMINISTRADOR | `idComprador, idVendedor` | - | `PagedModel` | Historial entre dos usuarios (Solo Admin). |

---
### 📂 Anexo: Estructuras JSON (DTOs) ejemplos

#### `LoginRequest`
```json
{
  "email": "usuario@ejemplo.com",
  "clave": "secreto123"
}
```

#### `AltaProductoDTO`
```json
{
  "titulo": "iPhone 15 Pro",
  "descripcion": "Nuevo, precintado",
  "precio": 999.99,
  "estado": "NUEVO",
  "envioDisponible": true,
  "categoriaId": "Electronica.xml",
  "vendedorId": "id-del-usuario",
  "recogida": {
    "descripcion": "Plaza Mayor, Madrid",
    "longitud": -3.703790,
    "latitud": 40.416775
  }
}
```

#### `ProductoUpdateDTO`
```json
{
  "descripcion": "Nueva descripción del producto",
  "precio": 850.00,
  "vendedorId": "id-del-usuario"
}
```

#### `LugarRecogidaDTO`
```json
{
  "descripcion": "Calle Gran Vía, 1, Madrid",
  "longitud": -3.699,
  "latitud": 40.419
}
```

#### `NuevaCompraventaDTO`
```json
{
  "idProducto": "id-producto-uuid",
  "idComprador": "id-comprador-uuid",
  "emailComprador": "comprador@ejemplo.com",
  "claveComprador": "clave-comprador"
}
```

---
## 📖 Documentación Técnica

El Swagger UI de la pasarela agrega la documentación de los microservicios mediante el uso de `springdoc-openapi`.

| Servicio | Swagger UI Local | Swagger UI OCI |
| --- | --- | --- |
| **Pasarela (Agregado)** | `http://localhost:8090/swagger-ui.html` | [Enlace OCI](http://79.72.53.253:8090/swagger-ui.html) |
| **Productos** | `http://localhost:8080/swagger-ui.html` | - |
| **Compraventas** | `http://localhost:8082/swagger-ui.html` | - |

>[!NOTE]
> **Nota sobre Usuarios:** 
> El microservicio de Usuarios utiliza JAX-RS/Grizzly (no Spring Boot). Aunque no genera un Swagger UI dinámico, todos sus endpoints públicos están integrados en la tabla de arriba y son accesibles vía `/usuarios/**` en la Pasarela.

## 👥 Equipo de Trabajo

| Nombre | GitHub |
| --- | --- |
| **Alberto Zapata Mira** | [StoneySpring688](https://github.com/StoneySpring688) |
| **María Capilla Zapata** | [meryphone](https://github.com/meryphone) |
