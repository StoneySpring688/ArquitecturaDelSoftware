package SegundUM.Usuarios.adaptadores.RabbitMQ;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import SegundUM.Usuarios.puertos.PuertoSalidaEventos;

/**
 * Adaptador de salida que implementa el puerto de eventos
 * utilizando RabbitMQ como infraestructura de mensajeria (cliente directo, sin Spring).
 */
public class AdaptadorSalidaEventosRabbitMQ implements PuertoSalidaEventos {

    private static final Logger logger = LoggerFactory.getLogger(AdaptadorSalidaEventosRabbitMQ.class);
    private static final Gson gson = new Gson();

    private final ConnectionFactory connectionFactory;

    public AdaptadorSalidaEventosRabbitMQ(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private void publicarEvento(String routingKey, Map<String, String> evento) {
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            // El exchange ya fue creado por rabbitmq-setup
            String mensaje = gson.toJson(evento);
            channel.basicPublish(RabbitMQConfigUsuarios.EXCHANGE_NAME, routingKey, null, mensaje.getBytes("UTF-8"));

            logger.info("Evento publicado [{}]: {}", routingKey, mensaje);

        } catch (Exception e) {
            logger.error("Error al publicar evento {}: {}", routingKey, e.getMessage(), e);
        }
    }

    @Override
    public void publicarUsuarioCreado(String idUsuario, String email, String nombre) {
        Map<String, String> evento = new HashMap<>();
        evento.put("idUsuario", idUsuario);
        evento.put("email", email);
        evento.put("nombre", nombre);
        evento.put("tipo", "usuario-creado");
        evento.put("fechaHora", LocalDateTime.now().toString());

        publicarEvento("bus.usuarios.usuario-creado", evento);
    }

    @Override
    public void publicarUsuarioModificado(String idUsuario, String nombre) {
        Map<String, String> evento = new HashMap<>();
        evento.put("idUsuario", idUsuario);
        evento.put("nombre", nombre);
        evento.put("tipo", "usuario-modificado");
        evento.put("fechaHora", LocalDateTime.now().toString());

        publicarEvento("bus.usuarios.usuario-modificado", evento);
    }

    @Override
    public void publicarUsuarioEliminado(String idUsuario) {
        Map<String, String> evento = new HashMap<>();
        evento.put("idUsuario", idUsuario);
        evento.put("tipo", "usuario-eliminado");
        evento.put("fechaHora", LocalDateTime.now().toString());

        publicarEvento("bus.usuarios.usuario-eliminado", evento);
    }
}
