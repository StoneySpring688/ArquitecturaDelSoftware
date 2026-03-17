package SegundUM.Productos.puertos;

/**
 * Puerto de salida para publicar eventos en el bus de mensajería.
 * 
 * Puerto que define la interfaz que el servicio utiliza para emitir eventos,
 * sin acoplarse a la implementación concreta (RabbitMQ).
 */
public interface PuertoSalidaEventos {

    /**
     * Publica un evento de producto creado en el bus de eventos.
     *
     * @param idProducto   ID del producto creado
     * @param titulo       Título del producto
     * @param vendedorId   ID del usuario vendedor
     */
    void publicarProductoCreado(String idProducto, String titulo, String vendedorId);

    /**
     * Publica un evento de producto eliminado en el bus de eventos.
     *
     * @param idProducto   ID del producto eliminado
     * @param vendedorId   ID del usuario vendedor
     */
    void publicarProductoEliminado(String idProducto, String vendedorId);

    /**
     * Publica un evento de producto modificado (titulo) en el bus de eventos.
     *
     * @param idProducto   ID del producto modificado
     * @param nuevoTitulo  Nuevo titulo del producto
     */
    void publicarProductoModificado(String idProducto, String nuevoTitulo);
}
