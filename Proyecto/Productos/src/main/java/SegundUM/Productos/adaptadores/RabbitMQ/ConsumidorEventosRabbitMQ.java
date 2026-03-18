package SegundUM.Productos.adaptadores.RabbitMQ;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import SegundUM.Productos.puertos.PuertaEntradaEventos;
import SegundUM.Productos.servicio.ServicioException;

/**
 * Adaptador de entrada que consume eventos del bus RabbitMQ
 * y los despacha al puerto de entrada correspondiente.
 */
@Component
public class ConsumidorEventosRabbitMQ {

    private static final Logger logger = LoggerFactory.getLogger(ConsumidorEventosRabbitMQ.class);

    private final PuertaEntradaEventos puertaEntradaEventos;

    public ConsumidorEventosRabbitMQ(PuertaEntradaEventos puertaEntradaEventos) {
        this.puertaEntradaEventos = puertaEntradaEventos;
    }

    @RabbitListener(queues = RabbitMQConfigProductos.QUEUE_NAME)
    public void recibirEvento(Map<String, String> mensaje, @Header("amqp_receivedRoutingKey") String routingKey) {
        logger.info("Evento recibido en cola productos - routingKey: {}, mensaje: {}", routingKey, mensaje);

        try {
            switch (routingKey) {
                case "bus.compraventas.compraventa-creada":
                    puertaEntradaEventos.manejarCompraventaCreada(
                            mensaje.get("idProducto")
                    );
                    break;
                case "bus.usuarios.usuario-eliminado":
                    puertaEntradaEventos.manejarUsuarioEliminado(
                            mensaje.get("idUsuario")
                    );
                    break;
                default:
                    logger.warn("Evento no reconocido: {}", routingKey);
            }
        } catch (ServicioException e) {
            logger.error("Error al procesar evento {}: {}", routingKey, e.getMessage(), e);
        }
    }
}
