# --- Etapa 1: Compilación ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Etapa 2: Ejecución ---
# Usuarios usa JAX-RS + Grizzly (no Spring Boot), necesita Maven para exec:java
FROM maven:3.9-eclipse-temurin-17-alpine AS runtime
WORKDIR /app
COPY --from=build /app /app
COPY --from=build /root/.m2 /root/.m2

EXPOSE 8081

CMD ["mvn", "-q", "exec:java", "-Dexec.mainClass=SegundUM.Usuarios.rest.App"]
