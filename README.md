# SegundUM

Este proyecto forma parte de la asignatura **Arquitectura del Software** en la **Universidad de Murcia (2025/2026)**. Consiste en la migración del proyecto realizado en [**Aplicaciones Distribuidas**](https://github.com/StoneySpring688/aadd2025) a una arquitectura de microservicios.

## Arrancar con Docker

```bash
cd Proyecto && docker compose up --build
```

Esto levanta todos los servicios (MySQL, MongoDB, RabbitMQ, Usuarios, Productos, Compraventas y Pasarela) en el orden correcto.

La API estará disponible en `http://localhost:8090`.

## Documentación

El Swagger UI de la pasarela agrega la documentación de todos los microservicios:

| Servicio | Swagger UI | 
| --- | --- | 
| Pasarela (agregado) | http://localhost:8090/swagger-ui.html | 
| Productos | http://localhost:8080/swagger-ui.html | 
| Compraventas | http://localhost:8082/swagger-ui.html | 

> **Nota:** El servicio Usuarios (JAX-RS/Grizzly) no expone documentación Swagger.

## Equipo de Trabajo

| Nombre | GitHub |
| --- | --- |
| **Alberto Zapata Mira** | StoneySpring688 |
| **María Capilla Zapata** | meryphone |

