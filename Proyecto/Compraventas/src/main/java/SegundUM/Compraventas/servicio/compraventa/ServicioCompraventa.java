package SegundUM.Compraventas.servicio.compraventa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import SegundUM.Compraventas.dominio.Compraventa;

public interface ServicioCompraventa {
	
	/**
	 * Registrar la compraventa
	 **/
    Compraventa realizarCompra(String idProducto, String idComprador, String emailComprador, String claveComprador);
    
    /**
     * Recuperar las compras realizadas por un usuario
     **/
    Page<Compraventa> recuperarComprasDeUsuario(String idComprador, Pageable pageable);
    
    /**
	 * Recuperar las ventas realizadas por un usuario
	 **/
    Page<Compraventa> recuperarVentasDeUsuario(String idVendedor, Pageable pageable);
    
    /**
     * Recuperar las compraventas entre un comprador y un vendedor
     **/
    Page<Compraventa> recuperarCompraventasEntre(String idComprador, String idVendedor, Pageable pageable);
}
