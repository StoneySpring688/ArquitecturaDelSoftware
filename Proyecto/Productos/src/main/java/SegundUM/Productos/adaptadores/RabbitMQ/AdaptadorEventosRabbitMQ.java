package SegundUM.Productos.adaptadores.RabbitMQ;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import SegundUM.Productos.puertos.PuertoSalidaEventos;

/**
 * Adaptador de salida que implementa el puerto de eventos
 * utilizando RabbitMQ como infraestructura de mensajeria.
 */
@Component
public class AdaptadorEventosRabbitMQ implements PuertoSalidaEventos {

    private static final Logger logger = LoggerFactory.getLogger(AdaptadorEventosRabbitMQ.class);

    private final RabbitTemplate rabbitTemplate;

    public AdaptadorEventosRabbitMQ(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarProductoCreado(String idProducto, String titulo, String vendedorId) {
        Map<String, String> evento = new HashMap<>();
        evento.put("idProducto", idProducto);
        evento.put("titulo", titulo);
        evento.put("vendedorId", vendedorId);
        evento.put("tipo", "producto-creado");
        evento.put("fechaHora", LocalDateTime.now().toString());

        logger.info("Publicando evento producto-creado: {}", evento);
        rabbitTemplate.convertAndSend(RabbitMQConfigProductos.EXCHANGE_NAME, "bus.productos.producto-creado", evento);
        logger.info("Evento producto-creado publicado correctamente");
    }

    @Override
    public void publicarProductoEliminado(String idProducto, String vendedorId) {
        Map<String, String> evento = new HashMap<>();
        evento.put("idProducto", idProducto);
        evento.put("vendedorId", vendedorId);
        evento.put("tipo", "producto-eliminado");
        evento.put("fechaHora", LocalDateTime.now().toString());

        logger.info("Publicando evento producto-eliminado: {}", evento);
        rabbitTemplate.convertAndSend(RabbitMQConfigProductos.EXCHANGE_NAME, "bus.productos.producto-eliminado", evento);
        logger.info("Evento producto-eliminado publicado correctamente");
    }

    @Override
    public void publicarProductoModificado(String idProducto, String nuevoTitulo) {
        Map<String, String> evento = new HashMap<>();
        evento.put("idProducto", idProducto);
        evento.put("titulo", nuevoTitulo);
        evento.put("tipo", "producto-modificado");
        evento.put("fechaHora", LocalDateTime.now().toString());

        logger.info("Publicando evento producto-modificado: {}", evento);
        rabbitTemplate.convertAndSend(RabbitMQConfigProductos.EXCHANGE_NAME, "bus.productos.producto-modificado", evento);
        logger.info("Evento producto-modificado publicado correctamente");
    }
}
