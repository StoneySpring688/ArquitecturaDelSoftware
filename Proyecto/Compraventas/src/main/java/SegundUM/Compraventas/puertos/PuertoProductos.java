package SegundUM.Compraventas.puertos;

import SegundUM.Compraventas.rest.dto.ProductoDTO;

public interface PuertoProductos {
	ProductoDTO obtenerDatosProducto(String idProducto);
}
