package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;


/**
 * Repositorio específico para Categorías con operaciones AdHoc.
 */

@NoRepositoryBean
public interface RepositorioCategorias extends CrudRepository<Categoria, String> {
    
    /**
     * Recupera todas las categorías raíz (sin padre).
     */
    List<Categoria> getCategoriasRaiz();
    
    /**
     * Recupera todos los descendientes de una categoría.
     */
    List<Categoria> getDescendientes(String categoriaId) throws EntidadNoEncontrada;
    
    /**
     * Verifica si existe una categoría con el ID dado.
     */
    // Este metodo ya existe en CrudRepository como existsById, se deja anotado por claridad
    
    /**
	 * Busca categorías por nombre (búsqueda insensible a mayúsculas).
	 */
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}