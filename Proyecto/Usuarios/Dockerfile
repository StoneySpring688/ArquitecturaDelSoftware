# --- Etapa 1: Compilación ---
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Etapa 2: Ejecución ---
# Usuarios usa JAX-RS + Grizzly (no Spring Boot), necesita Maven para exec:java
FROM amazoncorretto:17 AS runtime
WORKDIR /app
COPY --from=build /app/target/Usuarios-*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
