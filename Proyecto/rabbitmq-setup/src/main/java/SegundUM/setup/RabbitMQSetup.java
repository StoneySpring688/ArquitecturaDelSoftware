package SegundUM.setup;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Script centralizado para crear la infraestructura RabbitMQ compartida
 * por todos los microservicios de la aplicacion.
 *
 * Crea:
 *   - Exchange "bus" (topic)
 *   - Cola "usuarios"     (durable)
 *   - Cola "productos"    (durable)
 *   - Cola "compraventas" (durable)
 *
 * Las suscripciones (bindings) se delegan a cada microservicio,
 * ya que cada uno decide a que eventos quiere suscribirse.
 *
 * Ejecutar:
 *   cd rabbitmq-setup
 *   mvn compile exec:java
 */
public class RabbitMQSetup {

    // Infraestructura compartida
    private static final String EXCHANGE_NAME = "bus";
    private static final String[] QUEUES = {"usuarios", "productos", "compraventas"};

    // Credenciales CloudAMQP
    private static final String HOST = "rat.rmq2.cloudamqp.com";
    private static final int PORT = 5671;
    private static final String USERNAME = "cfrvyzor";
    private static final String PASSWORD = "Y2mLAqiR1mOFZBBnupB6UDZ6o9E778iX";
    private static final String VHOST = "cfrvyzor";

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setVirtualHost(VHOST);
            factory.useSslProtocol();

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                // Crear exchange de tipo topic (durable, no auto-delete)
                channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
                System.out.println("[OK] Exchange creado: " + EXCHANGE_NAME + " (topic, durable)");

                // Crear las colas de cada microservicio (durables)
                for (String queue : QUEUES) {
                    channel.queueDeclare(queue, true, false, false, null);
                    System.out.println("[OK] Cola creada: " + queue + " (durable)");
                }

                System.out.println();
                System.out.println("Infraestructura RabbitMQ creada correctamente.");
                System.out.println("Los bindings seran configurados por cada microservicio al iniciar.");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo configurar RabbitMQ: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
