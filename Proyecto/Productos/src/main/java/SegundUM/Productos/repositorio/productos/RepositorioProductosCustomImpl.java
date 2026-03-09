package SegundUM.Productos.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;

/**
 * Implementación de los métodos custom de RepositorioProductosJPA.
 * Spring Data detecta esta clase por la convención de nombre (Impl).
 */
@Deprecated(since = "refactor: simplificación repositorio productos - prodcutosRestImprovements", forRemoval = true)
public class RepositorioProductosCustomImpl implements RepositorioProductosCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Producto> buscarProductos(
        String categoriaId, String textoBusqueda,
        EstadoProducto estadoMinimo, BigDecimal precioMaximo) {

        StringBuilder jpql = new StringBuilder("SELECT p FROM Producto p WHERE 1=1");
        List<String> categoriasIds = new ArrayList<>();

        if (categoriaId != null) {
            Categoria categoria = em.find(Categoria.class, categoriaId);
            if (categoria != null) {
                categoriasIds.add(categoriaId);
                categoria.obtenerDescendientes().forEach(c -> categoriasIds.add(c.getId()));
            }
        }

        if (!categoriasIds.isEmpty()) {
            jpql.append(" AND p.categoria.id IN :categoriasIds");
        }

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            jpql.append(" AND LOWER(p.descripcion) LIKE LOWER(:texto)");
        }

        if (estadoMinimo != null) {
            jpql.append(" AND p.estado IN :estados");
        }

        if (precioMaximo != null) {
            jpql.append(" AND p.precio <= :precioMaximo");
        }

        TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);

        if (!categoriasIds.isEmpty()) {
            query.setParameter("categoriasIds", categoriasIds);
        }
        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            query.setParameter("texto", "%" + textoBusqueda.trim() + "%");
        }
        if (estadoMinimo != null) {
            List<EstadoProducto> estadosValidos = new ArrayList<>();
            for (EstadoProducto estado : EstadoProducto.values()) {
                if (estado.esMejorOIgualQue(estadoMinimo)) {
                    estadosValidos.add(estado);
                }
            }
            query.setParameter("estados", estadosValidos);
        }
        if (precioMaximo != null) {
            query.setParameter("precioMaximo", precioMaximo);
        }

        return query.getResultList();
    }

    @Override
    public List<ResumenProducto> getHistorialMes(int mes, int anio, String vendedorId) {
        LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime fin = inicio.plusMonths(1);

        StringBuilder jpql = new StringBuilder(
            "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion < :fin"
        );

        if (vendedorId != null && !vendedorId.trim().isEmpty()) {
            jpql.append(" AND p.vendedorId = :vendedorId");
        }

        jpql.append(" ORDER BY p.visualizaciones DESC");

        TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);
        query.setParameter("inicio", inicio);
        query.setParameter("fin", fin);

        if (vendedorId != null && !vendedorId.trim().isEmpty()) {
            query.setParameter("vendedorId", vendedorId);
        }

        return query.getResultList().stream()
            .map(p -> new ResumenProducto(
                p.getId(), p.getTitulo(), p.getPrecio(),
                p.getFechaPublicacion(), p.getCategoria().getNombre(),
                p.getVisualizaciones()
            )).collect(Collectors.toList());
    }

    @Override
    public List<ResumenProducto> getHistorialMes(int mes, int anio) {
        return getHistorialMes(mes, anio, null);
    }
}
