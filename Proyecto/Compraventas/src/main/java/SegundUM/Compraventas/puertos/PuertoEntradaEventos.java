package SegundUM.Compraventas.puertos;

import SegundUM.Compraventas.servicio.ServicioException;

/**
 * Puerto de entrada para manejar eventos recibidos desde el bus de mensajeria.
 *
 * Este puerto define las operaciones
 * que el microservicio de Compraventas ofrece como reaccion a eventos externos.
 */
public interface PuertoEntradaEventos {

    /**
     * Maneja el evento de usuario modificado:
     * actualiza el nombre del usuario en las compraventas donde aparece.
     */
    void manejarUsuarioModificado(String idUsuario, String nuevoNombre) throws ServicioException;

    /**
     * Maneja el evento de usuario eliminado:
     * establece a null los datos del vendedor o comprador en las compraventas.
     */
    void manejarUsuarioEliminado(String idUsuario) throws ServicioException;

    /**
     * Maneja el evento de producto modificado (titulo):
     * actualiza el titulo del producto en las compraventas.
     */
    void manejarProductoModificado(String idProducto, String nuevoTitulo) throws ServicioException;

    /**
     * Maneja el evento de producto eliminado:
     * establece a null los datos del producto en las compraventas.
     */
    void manejarProductoEliminado(String idProducto) throws ServicioException;
}
