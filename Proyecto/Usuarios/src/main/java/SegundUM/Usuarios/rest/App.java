package SegundUM.Usuarios.rest;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.ConnectionFactory;

import SegundUM.Usuarios.adaptadores.RabbitMQ.AdaptadorSalidaEventosRabbitMQ;
import SegundUM.Usuarios.adaptadores.RabbitMQ.ConsumidorEventosRabbitMQ;
import SegundUM.Usuarios.adaptadores.RabbitMQ.RabbitMQConfigUsuarios;
import SegundUM.Usuarios.puertos.PuertoEntradaEventos;
import SegundUM.Usuarios.servicio.FactoriaServicios;
import SegundUM.Usuarios.servicio.usuarios.ServicioUsuarios;
import SegundUM.Usuarios.servicio.usuarios.ServicioUsuariosImpl;

/**
 * Punto de entrada del microservicio REST de Usuarios.
 *
 * Arranca un servidor HTTP Grizzly con Jersey (JAX-RS) en el puerto 8081,
 * exponiendo el controlador REST de usuarios.
 * Tambien arranca el consumidor de eventos RabbitMQ.
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final String BASE_URI = "http://0.0.0.0:8081/api/";

    public static void main(String[] args) {
        try {
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

            // --- Configurar bus de eventos RabbitMQ ---

            // ConnectionFactory compartida (creada por RabbitMQConfigUsuarios)
            ConnectionFactory connectionFactory = RabbitMQConfigUsuarios.crearConnectionFactory();

            // Obtener la instancia del servicio (singleton via FactoriaServicios)
            ServicioUsuariosImpl servicio = (ServicioUsuariosImpl) FactoriaServicios.getServicio(ServicioUsuarios.class);

            // Crear e inyectar el adaptador de salida (publisher)
            AdaptadorSalidaEventosRabbitMQ adaptadorSalida = new AdaptadorSalidaEventosRabbitMQ(connectionFactory);
            servicio.setPuertoSalidaEventos(adaptadorSalida);

            // Crear y arrancar el consumidor de eventos (entrada)
            ConsumidorEventosRabbitMQ consumidor = new ConsumidorEventosRabbitMQ(
                    connectionFactory, (PuertoEntradaEventos) servicio);
            consumidor.iniciar();

            logger.info("Bus de eventos RabbitMQ configurado correctamente");

            // Registrar shutdown hook para cerrar recursos
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Cerrando microservicio Usuarios...");
                server.shutdownNow();
            }));

        } catch (Exception e) {
            logger.error("Error al arrancar el microservicio de Usuarios", e);
        }
    }
}
