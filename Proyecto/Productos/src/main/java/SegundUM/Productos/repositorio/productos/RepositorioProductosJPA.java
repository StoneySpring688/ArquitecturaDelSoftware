package SegundUM.Productos.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.RepositorioJPA;
import SegundUM.Productos.utils.EntityManagerHelper;


/**
 * Implementación JPA del repositorio de productos.
 */
public class RepositorioProductosJPA extends RepositorioJPA<Producto> implements RepositorioProductos {
    
    @Override
    public Class<Producto> getClase() {
        return Producto.class;
    }
    
    @Override
    public List<Producto> getProductosPorVendedor(String vendedorId) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
            	"SELECT p FROM Producto p WHERE p.vendedorId = :vendedorId", 
                Producto.class
            );
            query.setParameter("vendedorId", vendedorId);
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al recuperar productos del vendedor " + vendedorId, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<Producto> buscarProductos(
        String categoriaId,
        String textoBusqueda,
        EstadoProducto estadoMinimo,
        BigDecimal precioMaximo
    ) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
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
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar productos", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<ResumenProducto> getHistorialMes(int mes, int anio, String vendedorId) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // inicio y fin del mes
            LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
            LocalDateTime fin = inicio.plusMonths(1);
            
            // consulta
            StringBuilder jpql = new StringBuilder(
                "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion < :fin"
            );
            // TODO hay que lamar a la api de Microservicio de usuarios ara obtener el id del vendedor a partir del email.
            // Si email de vendedor, añadir filtro
            if (vendedorId != null && !vendedorId.trim().isEmpty()) {
                jpql.append(" AND p.vendedorId = :vendedorId");
            }
            
            jpql.append(" ORDER BY p.visualizaciones DESC");
            
            TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);
            
            // parámetro de email si está presente
            if (vendedorId != null && !vendedorId.trim().isEmpty()) {
                query.setParameter("vendedorId", vendedorId);
            }
            
            List<Producto> productos = query.getResultList();
            
            // ResumenProducto
            return productos.stream()
                .map(p -> new ResumenProducto(
                    p.getId(),
                    p.getTitulo(),
                    p.getPrecio(),
                    p.getFechaPublicacion(),
                    p.getCategoria().getNombre(),
                    p.getVisualizaciones()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RepositorioException(
                "Error al obtener historial del mes " + mes + "/" + anio + 
                (vendedorId != null ? " para el vendedor " + vendedorId : ""), 
                e
            );
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<Producto> getProductosPorFechas(LocalDateTime inicio, LocalDateTime fin) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion <= :fin", 
                Producto.class
            );
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al recuperar productos por fechas", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

	@Override
	public List<ResumenProducto> getHistorialMes(int mes, int anio) throws RepositorioException {
		EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // inicio y fin del mes
            LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
            LocalDateTime fin = inicio.plusMonths(1);
            
         // consulta
            StringBuilder jpql = new StringBuilder(
                "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion < :fin"
            );
            
            jpql.append(" ORDER BY p.visualizaciones DESC");
            
            TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);
            
            List<Producto> productos = query.getResultList();
            
            // ResumenProducto
            return productos.stream()
                .map(p -> new ResumenProducto(
                    p.getId(),
                    p.getTitulo(),
                    p.getPrecio(),
                    p.getFechaPublicacion(),
                    p.getCategoria().getNombre(),
                    p.getVisualizaciones()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RepositorioException("Error al obtener historial del mes " + mes + "/" + anio , e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
	}
	
	@Override
	public List<Producto> getByVendedor(String vendedorId) throws RepositorioException {
	    EntityManager em = EntityManagerHelper.getEntityManager();
	    try {
	        // Usamos JOIN FETCH c para traer la categoría y evitar LazyException en la vista
	        TypedQuery<Producto> query = em.createQuery(
	            "SELECT p FROM Producto p JOIN FETCH p.categoria c WHERE p.vendedorId = :vendedorId", 
	            Producto.class
	        );
	        query.setParameter("vendedorId", vendedorId);
	        
	        return query.getResultList();
	    } catch (Exception e) {
	        throw new RepositorioException("Error al recuperar productos del vendedor " + vendedorId, e);
	    } finally {
	        EntityManagerHelper.closeEntityManager();
	    }
	}
}