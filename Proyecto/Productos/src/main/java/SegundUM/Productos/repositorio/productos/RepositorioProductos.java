package SegundUM.Productos.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;


/**
 * Repositorio específico para Productos con operaciones AdHoc.
 */

@NoRepositoryBean
public interface RepositorioProductos extends PagingAndSortingRepository<Producto, String> {

    /**
     * Obtiene los productos de un vendedor.
     */
	@Deprecated
    List<Producto> findByVendedorId(String vendedorId);
	Page<Producto> findByVendedorId(String vendedorId, Pageable pageable);

    /**
     * Busca productos por categoría, descripción, estado y precio máximo.
     * Todos los parámetros son opcionales (pueden ser null).
     */
    @Deprecated
    List<Producto> buscarProductos(
        String categoriaId,
        String textoBusqueda,
        EstadoProducto estadoMinimo,
        BigDecimal precioMaximo
    );
    Page<Producto> buscarProductos(
            String categoriaId,
            String textoBusqueda,
            EstadoProducto estadoMinimo,
            BigDecimal precioMaximo,
            Pageable pageable
        );

    /**
     * Obtiene el historial del mes de un vendedor, ordenado por visualizaciones.
     */
    @Deprecated
    List<Producto> getHistorialMes(int mes, int anio, String vendedorId);
    Page<Producto> getHistorialMes(int mes, int anio, String vendedorId, Pageable pageable);

    /**
     * Obtiene productos publicados en un rango de fechas.
     */
    @Deprecated
    List<Producto> getProductosPorFechas(LocalDateTime inicio, LocalDateTime fin);
    Page<Producto> getProductosPorFechas(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    /**
	 * Obtiene los productos de un vendedor con su categoría.
	 */
    @Deprecated
    List<Producto> getByVendedorConCategoria(String vendedorId);
    Page<Producto> getByVendedorConCategoria(String vendedorId, Pageable pageable);
}
