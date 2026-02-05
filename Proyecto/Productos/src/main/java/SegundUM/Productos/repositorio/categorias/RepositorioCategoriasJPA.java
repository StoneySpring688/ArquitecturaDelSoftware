package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.RepositorioJPA;
import SegundUM.Productos.utils.EntityManagerHelper;


/**
 * Implementación JPA del repositorio de categorías.
 */
public class RepositorioCategoriasJPA extends RepositorioJPA<Categoria> implements RepositorioCategorias {
    
    @Override
    public Class<Categoria> getClase() {
        return Categoria.class;
    }
    
    @Override
    public List<Categoria> getCategoriasRaiz() throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Categoria> query = em.createQuery(
                "SELECT c FROM Categoria c WHERE c.categoriaPadre IS NULL", 
                Categoria.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al recuperar las categorías raíz", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws RepositorioException, EntidadNoEncontrada {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            Categoria categoria = em.find(Categoria.class, categoriaId);
            if (categoria == null) {
                throw new EntidadNoEncontrada("Categoría con id " + categoriaId + " no encontrada");
            }
            
            // Obtener descendientes recursivamente usando el método del dominio
            return categoria.obtenerDescendientes();
        } catch (EntidadNoEncontrada e) {
            throw e;
        } catch (Exception e) {
            throw new RepositorioException("Error al recuperar los descendientes de la categoría " + categoriaId, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public boolean existe(String id) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            Categoria categoria = em.find(Categoria.class, id);
            return categoria != null;
        } catch (Exception e) {
            throw new RepositorioException("Error al verificar existencia de categoría " + id, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<Categoria> buscarPorNombre(String nombre) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // Usamos LOWER y LIKE para que la búsqueda sea flexible (insensible a mayúsculas)
            TypedQuery<Categoria> query = em.createQuery(
                "SELECT c FROM Categoria c WHERE LOWER(c.nombre) LIKE LOWER(:nombre)", 
                Categoria.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar categoría por nombre: " + nombre, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
}