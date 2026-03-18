package SegundUM.Usuarios.adaptadores.RabbitMQ;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import SegundUM.Usuarios.puertos.PuertoEntradaEventos;

/**
 * Adaptador de entrada que consume eventos del bus RabbitMQ
 * y los despacha al puerto de entrada correspondiente.
 *
 * Utiliza el cliente RabbitMQ directo (sin Spring).
 */
public class ConsumidorEventosRabbitMQ {

    private static final Logger logger = LoggerFactory.getLogger(ConsumidorEventosRabbitMQ.class);
    private static final Gson gson = new Gson();

    private final ConnectionFactory connectionFactory;
    private final PuertoEntradaEventos puertoEntradaEventos;

    public ConsumidorEventosRabbitMQ(ConnectionFactory connectionFactory, PuertoEntradaEventos puertoEntradaEventos) {
        this.connectionFactory = connectionFactory;
        this.puertoEntradaEventos = puertoEntradaEventos;
    }

    /**
     * Arranca el consumidor: configura bindings (suscripciones) y empieza a escuchar.
     * El exchange y las colas se crean de forma centralizada mediante rabbitmq-setup.
     */
    public void iniciar() {
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            // Configurar suscripciones (definidas en RabbitMQConfigUsuarios)
            RabbitMQConfigUsuarios.configurarBindings(channel);

            logger.info("Consumidor RabbitMQ de Usuarios iniciado. Cola: {}", RabbitMQConfigUsuarios.QUEUE_NAME);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String routingKey = delivery.getEnvelope().getRoutingKey();
                String cuerpo = new String(delivery.getBody(), StandardCharsets.UTF_8);
                logger.info("Evento recibido en cola usuarios - routingKey: {}, mensaje: {}", routingKey, cuerpo);

                try {
                    Map<String, String> mensaje = gson.fromJson(cuerpo, new TypeToken<Map<String, String>>(){}.getType());
                    recibirEvento(routingKey, mensaje);
                } catch (Exception e) {
                    logger.error("Error al procesar evento {}: {}", routingKey, e.getMessage(), e);
                }
            };

            channel.basicConsume(RabbitMQConfigUsuarios.QUEUE_NAME, true, deliverCallback, consumerTag -> {});

        } catch (Exception e) {
            logger.error("Error al iniciar consumidor RabbitMQ de Usuarios: {}", e.getMessage(), e);
        }
    }

    private void recibirEvento(String routingKey, Map<String, String> mensaje) {
        try {
            switch (routingKey) {
                case "bus.compraventas.compraventa-creada":
                    puertoEntradaEventos.manejarCompraventaCreada(
                            mensaje.get("idComprador"),
                            mensaje.get("idVendedor")
                    );
                    break;
                case "bus.productos.producto-creado":
                    puertoEntradaEventos.manejarProductoCreado(
                            mensaje.get("idProducto"),
                            mensaje.get("vendedorId")
                    );
                    break;
                case "bus.productos.producto-eliminado":
                    puertoEntradaEventos.manejarProductoEliminado(
                            mensaje.get("idProducto"),
                            mensaje.get("vendedorId")
                    );
                    break;
                default:
                    logger.warn("Evento no reconocido: {}", routingKey);
            }
        } catch (Exception e) {
            logger.error("Error al despachar evento {}: {}", routingKey, e.getMessage(), e);
        }
    }
}
