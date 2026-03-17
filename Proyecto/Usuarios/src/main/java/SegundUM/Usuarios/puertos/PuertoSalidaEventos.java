package SegundUM.Usuarios.puertos;

/**
 * Puerto de salida para publicar eventos en el bus de mensajeria.
 *
 * Puerto que define la interfaz que el servicio utiliza para emitir eventos,
 * sin acoplarse a la implementacion concreta (RabbitMQ).
 */
public interface PuertoSalidaEventos {

    /**
     * Publica un evento de usuario creado en el bus de eventos.
     */
    void publicarUsuarioCreado(String idUsuario, String email, String nombre);

    /**
     * Publica un evento de usuario modificado (nombre) en el bus de eventos.
     */
    void publicarUsuarioModificado(String idUsuario, String nombre);

    /**
     * Publica un evento de usuario eliminado en el bus de eventos.
     */
    void publicarUsuarioEliminado(String idUsuario);
}
