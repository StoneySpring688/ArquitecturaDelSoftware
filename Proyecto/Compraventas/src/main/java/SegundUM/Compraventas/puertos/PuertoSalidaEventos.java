package SegundUM.Compraventas.puertos;

/**
 * Puerto de salida para publicar eventos en el bus de mensajería.
 * 
 * Puerto  que define la interfaz
 * que el servicio utiliza para emitir eventos, sin acoplarse a la
 * implementación concreta (RabbitMQ).
 */
public interface PuertoSalidaEventos {

    /**
     * Publica un evento de compraventa creada en el bus de eventos.
     *
     * @param idCompraventa  ID de la compraventa registrada
     * @param idProducto     ID del producto comprado
     * @param idComprador    ID del usuario comprador
     * @param idVendedor     ID del usuario vendedor
     */
    void publicarCompraventaCreada(String idCompraventa, String idProducto, String idComprador, String idVendedor);
}
