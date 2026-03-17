package SegundUM.Compraventas.adaptadores.RabbitMQ;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import SegundUM.Compraventas.puertos.PuertoSalidaEventos;

/**
 * Adaptador de salida que implementa el puerto de eventos
 * utilizando RabbitMQ como infraestructura de mensajería.
 *
 * Publica eventos en el exchange "bus" con routing keys de tipo topic.
 */
@Component
public class AdaptadorEventosRabbitMQ implements PuertoSalidaEventos {

    private static final Logger logger = LoggerFactory.getLogger(AdaptadorEventosRabbitMQ.class);

    private final RabbitTemplate rabbitTemplate;

    public AdaptadorEventosRabbitMQ(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarCompraventaCreada(String idCompraventa, String idProducto, String idComprador, String idVendedor) {

        Map<String, String> evento = new HashMap<>();
        evento.put("idCompraventa", idCompraventa);
        evento.put("idProducto", idProducto);
        evento.put("idComprador", idComprador);
        evento.put("idVendedor", idVendedor);
        evento.put("tipo", "compraventa-creada");
        evento.put("fechaHora", LocalDateTime.now().toString());

        logger.info("Publicando evento compraventa-creada: {}", evento);

        rabbitTemplate.convertAndSend(
                RabbitMQConfigCompraventas.EXCHANGE_NAME,
                RabbitMQConfigCompraventas.ROUTING_KEY_COMPRAVENTA_CREADA,
                evento
        );

        logger.info("Evento compraventa-creada publicado correctamente");
    }
}
