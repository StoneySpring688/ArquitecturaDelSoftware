package SegundUM.Productos.adaptadores.RabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de Productos.
 *
 * El exchange "bus" y las colas se crean de forma centralizada
 * mediante el script rabbitmq-setup.
 *
 * Esta clase solo define:
 *   - Referencias al exchange y cola (necesarias para los Binding)
 *   - Bindings (suscripciones a eventos de otros microservicios)
 *   - Converter y template para enviar mensajes
 */
@Configuration
public class RabbitMQConfigProductos {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfigProductos.class);

    public static final String EXCHANGE_NAME = "bus";
    public static final String QUEUE_NAME = "productos";

    // Referencias al exchange y cola (creados por rabbitmq-setup)
    @Bean
    public TopicExchange busExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue productosQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    // --- Suscripciones (bindings) ---

    @Bean
    public Binding productosBindingCompraventaCreada(Queue productosQueue, TopicExchange busExchange) {
        return BindingBuilder.bind(productosQueue).to(busExchange).with("bus.compraventas.compraventa-creada");
    }

    @Bean
    public Binding productosBindingUsuarioEliminado(Queue productosQueue, TopicExchange busExchange) {
        return BindingBuilder.bind(productosQueue).to(busExchange).with("bus.usuarios.usuario-eliminado");
    }

    // --- Mensajeria ---

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
