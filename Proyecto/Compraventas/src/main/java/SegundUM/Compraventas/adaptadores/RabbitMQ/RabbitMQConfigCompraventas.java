package SegundUM.Compraventas.adaptadores.RabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de Compraventas.
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
public class RabbitMQConfigCompraventas {

    public static final String EXCHANGE_NAME = "bus";
    public static final String QUEUE_NAME = "compraventas";
    public static final String ROUTING_KEY_COMPRAVENTA_CREADA = "bus.compraventas.compraventa-creada";

    // Referencias al exchange y cola (creados por rabbitmq-setup)
    @Bean
    public TopicExchange busExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue compraventasQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    // --- Suscripciones (bindings) ---

    @Bean
    public Binding compraventasBindingUsuariosModificado(Queue compraventasQueue, TopicExchange busExchange) {
        return BindingBuilder.bind(compraventasQueue).to(busExchange).with("bus.usuarios.usuario-modificado");
    }

    @Bean
    public Binding compraventasBindingUsuariosEliminado(Queue compraventasQueue, TopicExchange busExchange) {
        return BindingBuilder.bind(compraventasQueue).to(busExchange).with("bus.usuarios.usuario-eliminado");
    }

    @Bean
    public Binding compraventasBindingProductosModificado(Queue compraventasQueue, TopicExchange busExchange) {
        return BindingBuilder.bind(compraventasQueue).to(busExchange).with("bus.productos.producto-modificado");
    }

    @Bean
    public Binding compraventasBindingProductosEliminado(Queue compraventasQueue, TopicExchange busExchange) {
        return BindingBuilder.bind(compraventasQueue).to(busExchange).with("bus.productos.producto-eliminado");
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
