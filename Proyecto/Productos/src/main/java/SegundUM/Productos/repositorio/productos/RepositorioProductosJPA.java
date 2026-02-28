package SegundUM.Productos.repositorio.productos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import SegundUM.Productos.dominio.Producto;

/**
 * Implementación JPA del repositorio de productos.
 */

@Repository
public interface RepositorioProductosJPA extends RepositorioProductos, JpaRepository<Producto, String>, RepositorioProductosCustom {

    List<Producto> findByVendedorId(String vendedorId);

    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria c WHERE p.vendedorId = :vendedorId")
    List<Producto> getByVendedorConCategoria(@Param("vendedorId") String vendedorId);

    @Query("SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion <= :fin")
    List<Producto> getProductosPorFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
