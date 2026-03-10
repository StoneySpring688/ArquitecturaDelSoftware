# SegundUM

Este proyecto forma parte de la asignatura **Arquitectura del Software** en la **Universidad de Murcia (2025/2026)**. Consiste en la migración del proyecto realizado en [**Aplicaciones Distribuidas**](https://github.com/StoneySpring688/aadd2025) una arquitectura de microservicios, enfocándose en la autonomía de los servicios y la consistencia eventual de los datos.

## 🏗️ Arquitectura del Sistema

La aplicación se ha dividido siguiendo principios de diseño orientado al dominio (DDD) y patrones de microservicios:

* **Microservicio de Usuarios:** Gestiona el ciclo de vida completo de los usuarios, incluyendo credenciales, roles y datos personales extensos.
* **Microservicio de Productos:** Encargado de la gestión de artículos, categorías y búsquedas.
* **Autonomía de Datos:** Cada microservicio tiene una base de datos con la información específica de su servicio (usuarios o   productos + categorias).
* **Jerarquía de Categorías:** Implementa la carga y gestión de categorías complejas mediante una estructura jerárquica.

## Api Testing
Una vez ejecutado el microservicio de Correspondiente, se ofrece una documentación interactiva de la api en la url:
```
http://url-del-deploy/swagger-ui.html
```
por ejemplo: *http://localhost:8080/swagger-ui.html*.

## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología |
| --- | --- |
| **Lenguaje** | Java |
| **Persistencia** | JPA (Java Persistence API) |
| **Gestión de Entidades** | JPA con `EntityManager` |
| **Arquitectura** | Microservicios |
| **Logging** | SLF4J para trazabilidad de errores |

## 🚀 Instalación y Uso

**TODO**


## 👥 Equipo de Trabajo

| Nombre | GitHub |
| --- | --- |
| **Alberto Zapata Mira** | StoneySpring688 |
| **María Capilla Zapata** |  meryphone |

