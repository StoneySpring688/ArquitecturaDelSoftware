package SegundUM.Productos.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;


/**
 * Repositorio específico para Productos con operaciones AdHoc.
 */

@NoRepositoryBean
public interface RepositorioProductos extends CrudRepository<Producto, String> {

    /**
     * Obtiene los productos de un vendedor.
     */
    List<Producto> findByVendedorId(String vendedorId);

    /**
     * Busca productos por categoría, descripción, estado y precio máximo.
     * Todos los parámetros son opcionales (pueden ser null).
     */
    List<Producto> buscarProductos(
        String categoriaId,
        String textoBusqueda,
        EstadoProducto estadoMinimo,
        BigDecimal precioMaximo
    );

    /**
     * Obtiene el historial del mes de un vendedor, ordenado por visualizaciones.
     */
    List<ResumenProducto> getHistorialMes(int mes, int anio, String vendedorId);

    /**
     * Obtiene el historial del mes ordenado por visualizaciones.
     */
    List<ResumenProducto> getHistorialMes(int mes, int anio);

    /**
     * Obtiene productos publicados en un rango de fechas.
     */
    List<Producto> getProductosPorFechas(LocalDateTime inicio, LocalDateTime fin);

    List<Producto> getByVendedorConCategoria(String vendedorId);
}
