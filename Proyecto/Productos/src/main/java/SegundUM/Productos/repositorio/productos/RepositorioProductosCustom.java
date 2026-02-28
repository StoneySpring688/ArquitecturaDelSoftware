package SegundUM.Productos.repositorio.productos;

import java.math.BigDecimal;
import java.util.List;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;

/**
 * Interfaz custom para queries complejas de productos.
 * Implementada en RepositorioProductosImpl.
 */
public interface RepositorioProductosCustom {

    List<Producto> buscarProductos(
        String categoriaId,
        String textoBusqueda,
        EstadoProducto estadoMinimo,
        BigDecimal precioMaximo
    );

    List<ResumenProducto> getHistorialMes(int mes, int anio, String vendedorId);

    List<ResumenProducto> getHistorialMes(int mes, int anio);
}
