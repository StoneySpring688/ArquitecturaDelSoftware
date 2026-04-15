# --- Etapa 1: Compilación ---
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Etapa 2: Ejecución ---
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Copiamos la carpeta de categorías XML
COPY categoriasXML ./categoriasXML

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
