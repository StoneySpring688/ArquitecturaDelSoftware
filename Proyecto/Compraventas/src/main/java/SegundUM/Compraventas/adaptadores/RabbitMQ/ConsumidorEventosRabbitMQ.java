package SegundUM.Compraventas.adaptadores.RabbitMQ;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import SegundUM.Compraventas.puertos.PuertoEntradaEventos;
import SegundUM.Compraventas.servicio.ServicioException;

/**
 * Adaptador de entrada que consume eventos del bus RabbitMQ
 * y los despacha al puerto de entrada correspondiente.
 */
@Component
public class ConsumidorEventosRabbitMQ {

    private static final Logger logger = LoggerFactory.getLogger(ConsumidorEventosRabbitMQ.class);

    private final PuertoEntradaEventos puertoEntradaEventos;

    public ConsumidorEventosRabbitMQ(PuertoEntradaEventos puertoEntradaEventos) {
        this.puertoEntradaEventos = puertoEntradaEventos;
    }

    @RabbitListener(queues = RabbitMQConfigCompraventas.QUEUE_NAME)
    public void recibirEvento(Map<String, String> mensaje, @Header("amqp_receivedRoutingKey") String routingKey) {
        logger.info("Evento recibido en cola compraventas - routingKey: {}, mensaje: {}", routingKey, mensaje);

        try {
            switch (routingKey) {
                case "bus.usuarios.usuario-modificado":
                    puertoEntradaEventos.manejarUsuarioModificado(
                            mensaje.get("idUsuario"),
                            mensaje.get("nombre")
                    );
                    break;
                case "bus.usuarios.usuario-eliminado":
                    puertoEntradaEventos.manejarUsuarioEliminado(
                            mensaje.get("idUsuario")
                    );
                    break;
                case "bus.productos.producto-modificado":
                    puertoEntradaEventos.manejarProductoModificado(
                            mensaje.get("idProducto"),
                            mensaje.get("titulo")
                    );
                    break;
                case "bus.productos.producto-eliminado":
                    puertoEntradaEventos.manejarProductoEliminado(
                            mensaje.get("idProducto")
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
