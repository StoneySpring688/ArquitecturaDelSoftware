package SegundUM.Usuarios.adaptadores.RabbitMQ;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuracion de RabbitMQ para el microservicio de Usuarios.
 *
 * Centraliza los parametros de conexion y las suscripciones (bindings).
 *
 * El exchange "bus" y las colas se crean de forma centralizada
 * mediante el script rabbitmq-setup.
 *
 * Esta clase define:
 *   - ConnectionFactory con las credenciales de CloudAMQP
 *   - Suscripciones (bindings) a eventos de otros microservicios
 *
 * Equivalente a las clases @Configuration de Spring en los otros microservicios,
 * pero implementada en Java puro al no usar Spring en este microservicio.
 */
public class RabbitMQConfigUsuarios {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfigUsuarios.class);

    public static final String EXCHANGE_NAME = "bus";
    public static final String QUEUE_NAME = "usuarios";

    // --- Suscripciones (patrones de routing key) ---
    private static final String[] BINDINGS = {
            "bus.compraventas.compraventa-creada",
            "bus.productos.producto-creado",
            "bus.productos.producto-eliminado"
    };

    // Credenciales RabbitMQ (sobreescribibles con variables de entorno)
    private static final String HOST = env("RABBITMQ_HOST", "rat.rmq2.cloudamqp.com");
    private static final int PORT = Integer.parseInt(env("RABBITMQ_PORT", "5671"));
    private static final String USERNAME = env("RABBITMQ_USERNAME", "cfrvyzor");
    private static final String PASSWORD = env("RABBITMQ_PASSWORD", "Y2mLAqiR1mOFZBBnupB6UDZ6o9E778iX");
    private static final String VIRTUAL_HOST = env("RABBITMQ_VHOST", "cfrvyzor");
    private static final boolean USE_SSL = Boolean.parseBoolean(env("RABBITMQ_SSL", "true"));

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    /**
     * Crea y devuelve una ConnectionFactory configurada con las credenciales de RabbitMQ.
     * En Docker se usa sin SSL (RabbitMQ local); en produccion con SSL (CloudAMQP).
     */
    public static ConnectionFactory crearConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUAL_HOST);
        logger.info("Creando ConnectionFactory RabbitMQ para Usuarios (host: {})", HOST);
        if (USE_SSL) {
            try {
                factory.useSslProtocol();
            } catch (Exception e) {
                throw new RuntimeException("Error al configurar SSL para RabbitMQ", e);
            }
        }
        return factory;
    }

    /**
     * Configura los bindings (suscripciones) de la cola de usuarios
     * al exchange "bus" para recibir eventos de compraventas y productos.
     */
    public static void configurarBindings(Channel channel) throws IOException {
        for (String pattern : BINDINGS) {
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, pattern);
        }
        logger.info("Bindings configurados para la cola de Usuarios: {} suscripciones", BINDINGS.length);
    }
}
