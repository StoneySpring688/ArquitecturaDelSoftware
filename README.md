# SegundUM

Este proyecto forma parte de la asignatura **Arquitectura del Software** en la **Universidad de Murcia (2025/2026)**. Consiste en la migración del proyecto realizado en [**Aplicaciones Distribuidas**](https://github.com/StoneySpring688/aadd2025) una arquitectura de microservicios, enfocándose en la autonomía de los servicios y la consistencia eventual de los datos.

## 🏗️ Arquitectura del Sistema

La aplicación se ha dividido siguiendo principios de diseño orientado al dominio (DDD) y patrones de microservicios:

* **Microservicio de Usuarios:** Gestiona el ciclo de vida completo de los usuarios, incluyendo credenciales, roles y datos personales extensos.
* **Microservicio de Productos:** Encargado de la gestión de artículos, categorías y búsquedas.
* **Autonomía de Datos:** Para evitar acoplamientos fuertes en tiempo de ejecución, el microservicio de Productos mantiene una **versión simplificada de la entidad Usuario** (réplica local) con datos básicos: ID, email, nombre y apellidos.
* **Jerarquía de Categorías:** Implementa la carga y gestión de categorías complejas mediante una estructura jerárquica.



## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología |
| --- | --- |
| **Lenguaje** | Java |
| **Persistencia** | JPA (Java Persistence API) |
| **Gestión de Entidades** | JPA con `EntityManager` |
| **Arquitectura** | Microservicios |
| **Logging** | SLF4J para trazabilidad de errores |

## 📦 Estructura del Proyecto (MS Productos)

**TODO**

---

## 🚀 Instalación y Uso

**TODO**

---

## 👥 Equipo de Trabajo

| Nombre | GitHub |
| --- | --- |
| **Alberto Zapata Mira** | StoneySpring688 |
| **María Capilla Zapata** |  meryphone |

