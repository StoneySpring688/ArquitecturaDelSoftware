package SegundUM.Usuarios.puertos;

import SegundUM.Usuarios.servicio.ServicioException;

/**
 * Puerto de entrada para manejar eventos recibidos desde el bus de mensajeria.
 *
 * Siguiendo la arquitectura hexagonal, este puerto define las operaciones
 * que el microservicio de Usuarios ofrece como reaccion a eventos externos.
 */
public interface PuertoEntradaEventos {

    /**
     * Maneja el evento de compraventa creada:
     * - Incrementa el contador de compras del comprador
     * - Incrementa el contador de ventas del vendedor
     */
    void manejarCompraventaCreada(String idComprador, String idVendedor) throws ServicioException;

    /**
     * Maneja el evento de producto creado:
     * - Anade el producto a la lista de productos del usuario vendedor
     */
    void manejarProductoCreado(String idProducto, String vendedorId) throws ServicioException;

    /**
     * Maneja el evento de producto eliminado:
     * - Elimina el producto de la lista de productos del usuario vendedor
     */
    void manejarProductoEliminado(String idProducto, String vendedorId) throws ServicioException;
}
