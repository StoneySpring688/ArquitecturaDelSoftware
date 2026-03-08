package SegundUM.Productos.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;

@Repository
public interface RepositorioProductosJPA extends RepositorioProductos, JpaRepository<Producto, String> {

    // --- Consultas ya existentes ---

    List<Producto> findByVendedorId(String vendedorId);

    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria c WHERE p.vendedorId = :vendedorId")
    List<Producto> getByVendedorConCategoria(@Param("vendedorId") String vendedorId);

    @Query("SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion <= :fin")
    List<Producto> getProductosPorFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Deprecated
    @Override
    @Query("SELECT p FROM Producto p WHERE " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:textoBusqueda IS NULL OR (LOWER(p.titulo) LIKE LOWER(CONCAT('%', :textoBusqueda, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :textoBusqueda, '%')))) AND " +
           "(:estadoMinimo IS NULL OR p.estado <= :estadoMinimo) AND " +
           "(:precioMaximo IS NULL OR p.precio <= :precioMaximo)")
    List<Producto> buscarProductos(
        @Param("categoriaId") String categoriaId,
        @Param("textoBusqueda") String textoBusqueda,
        @Param("estadoMinimo") EstadoProducto estadoMinimo,
        @Param("precioMaximo") BigDecimal precioMaximo
    );
    
    @Override
    @Query("SELECT p FROM Producto p WHERE " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:textoBusqueda IS NULL OR (LOWER(p.titulo) LIKE LOWER(CONCAT('%', :textoBusqueda, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :textoBusqueda, '%')))) AND " +
           "(:estadoMinimo IS NULL OR p.estado <= :estadoMinimo) AND " +
           "(:precioMaximo IS NULL OR p.precio <= :precioMaximo)")
    Page<Producto> buscarProductos(
        @Param("categoriaId") String categoriaId,
        @Param("textoBusqueda") String textoBusqueda,
        @Param("estadoMinimo") EstadoProducto estadoMinimo,
        @Param("precioMaximo") BigDecimal precioMaximo,
        Pageable pageable
    );

    @Override
    @Query("SELECT new SegundUM.Productos.dominio.ResumenProducto(" +
           "p.id, p.titulo, p.precio, p.fechaPublicacion, p.categoria.nombre, p.visualizaciones) " +
           "FROM Producto p " +
           "WHERE MONTH(p.fechaPublicacion) = :mes " +
           "AND YEAR(p.fechaPublicacion) = :anio " +
           "AND p.vendedorId = :vendedorId " +
           "ORDER BY p.visualizaciones DESC")
    List<ResumenProducto> getHistorialMes(
        @Param("mes") int mes,
        @Param("anio") int anio,
        @Param("vendedorId") String vendedorId
    );

    @Override
    @Query("SELECT new SegundUM.Productos.dominio.ResumenProducto(" +
           "p.id, p.titulo, p.precio, p.fechaPublicacion, p.categoria.nombre, p.visualizaciones) " +
           "FROM Producto p " +
           "WHERE MONTH(p.fechaPublicacion) = :mes " +
           "AND YEAR(p.fechaPublicacion) = :anio " +
           "ORDER BY p.visualizaciones DESC")
    List<ResumenProducto> getHistorialMes(
        @Param("mes") int mes,
        @Param("anio") int anio
    );
}