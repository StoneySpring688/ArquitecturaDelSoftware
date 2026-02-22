package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;

public class RepositorioCategoriasCustomImpl implements RepositorioCategoriasCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws EntidadNoEncontrada {
        Categoria categoria = em.find(Categoria.class, categoriaId);
        
        if (categoria == null) {
            throw new EntidadNoEncontrada("Categoría con id " + categoriaId + " no encontrada");
        }
        
        return categoria.obtenerDescendientes(); 
        
    }
}
