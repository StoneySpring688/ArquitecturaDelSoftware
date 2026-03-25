# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**SegundUM** — Java microservices architecture project for a Software Architecture course (UMU 2025/2026). Migrated from a distributed apps project to a full microservices design.

## Commands

### Run the full system
```bash
cd Proyecto && ./arrancar.sh
```
This starts services in order: RabbitMQ setup → Productos (8080) → Usuarios (8081) → Compraventas (8082). The pasarela (8090) must be started separately.

### Run individual services
```bash
# Usuarios (JAX-RS/Grizzly)
cd Proyecto/Usuarios && mvn -q compile exec:java -Dexec.mainClass="SegundUM.Usuarios.rest.App"

# Productos, Compraventas, pasarela (Spring Boot)
cd Proyecto/[service] && mvn -q spring-boot:run
```

### Build / test
```bash
mvn clean package     # build JAR
mvn test              # run tests
```

## Architecture

Four microservices, each with its own database, communicating via HTTP (Retrofit) and events (RabbitMQ):

| Service | Port | Framework | DB |
|---|---|---|---|
| Usuarios | 8081 | JAX-RS + Grizzly | MySQL |
| Productos | 8080 | Spring Boot | MySQL |
| Compraventas | 8082 | Spring Boot | MongoDB |
| pasarela | 8090 | Spring Boot + Zuul | — |

### Internal structure (all services follow this)
```
dominio/       — domain entities
puertos/       — port interfaces
adaptadores/   — adapter implementations
servicio/      — business logic
repositorio/   — data access abstraction
rest/          — REST controllers
```

### Key patterns
- **Hexagonal architecture**: business logic depends only on port interfaces; adapters plug in at startup via `FactoriaServicios` / `FactoriaRepositorios`, which read implementation class names from `servicios.properties` / `repositorios.properties`.
- **Repository pattern**: generic `Repositorio<T, K>` with JPA, in-memory (for tests), and MongoDB implementations.
- **Event-driven**: RabbitMQ (CloudAMQP) via `PuertoSalidaEventos` / `PuertoEntradaEventos` ports and `AdaptadorSalidaEventosRabbitMQ` / `ConsumidorEventosRabbitMQ` adapters.
- **API Gateway (pasarela)**: Zuul routes all external traffic; GitHub OAuth2 login issues a JWT that all downstream services validate.
- **HATEOAS**: Productos and Compraventas REST APIs include hypermedia links.

### Inter-service HTTP communication
Compraventas and pasarela use **Retrofit 2** clients to call Usuarios and Productos synchronously.

### Swagger UI
Available on the Spring Boot services: `http://localhost:[port]/swagger-ui.html`

## Docs
- `docs/servicios.md` — service layer pattern details
- `docs/repositorios.md` — repository pattern with examples
- `docs/PreparaciónEntornoDesarrollo.md` — dev environment setup
