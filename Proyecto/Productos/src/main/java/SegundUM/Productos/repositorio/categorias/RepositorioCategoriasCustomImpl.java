package SegundUM.Productos.repositorio.categorias;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import SegundUM.Productos.dominio.Categoria;

/**
 * Implementación de los métodos custom de RepositorioCategoriasJPA.
 * Spring Data detecta esta clase por la convención de nombre (Impl).
 */
public class RepositorioCategoriasCustomImpl implements RepositorioCategoriasCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Categoria> getDescendientes(String categoriaId) {
        Categoria categoria = em.find(Categoria.class, categoriaId);

        if (categoria == null) {
            return Collections.emptyList();
        }

        return categoria.obtenerDescendientes();
    }
}
