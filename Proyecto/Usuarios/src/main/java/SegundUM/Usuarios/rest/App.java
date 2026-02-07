package SegundUM.Usuarios.rest;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada del microservicio REST de Usuarios.
 *
 * Arranca un servidor HTTP Grizzly con Jersey (JAX-RS) en el puerto 8081,
 * exponiendo el controlador REST de usuarios.
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final String BASE_URI = "http://localhost:8081/api/";

    public static void main(String[] args) {
        try {
            // Escanea el paquete raíz rest (incluye controllers y utils/ObjectMapperProvider)
            ResourceConfig config = new ResourceConfig()
                    .packages("SegundUM.Usuarios.rest")
                    .register(JacksonFeature.class);

            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                    URI.create(BASE_URI), config);

            logger.info("=== Microservicio USUARIOS iniciado ===");
            logger.info("Base URI: {}", BASE_URI);
            logger.info("Endpoints de Usuarios:");
            logger.info("  GET    {}usuarios/{{id}}    - Obtener usuario por ID", BASE_URI);
            logger.info("  POST   {}usuarios           - Registrar nuevo usuario", BASE_URI);
            logger.info("  POST   {}usuarios/login     - Autenticar usuario", BASE_URI);
            logger.info("  PUT    {}usuarios/{{id}}    - Modificar datos del usuario", BASE_URI);
            logger.info("  DELETE {}usuarios/{{id}}    - Eliminar usuario", BASE_URI);
            logger.info("========================================");
            logger.info("Pulsa ENTER para detener el servidor...");

            System.in.read();
            server.shutdownNow();
            logger.info("=== Microservicio USUARIOS detenido ===");
        } catch (Exception e) {
            logger.error("Error al arrancar el microservicio de Usuarios", e);
        }
    }
}
