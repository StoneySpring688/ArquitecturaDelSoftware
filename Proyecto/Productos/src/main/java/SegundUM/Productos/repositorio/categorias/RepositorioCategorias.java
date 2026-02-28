package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import SegundUM.Productos.dominio.Categoria;


/**
 * Repositorio específico para Categorías con operaciones AdHoc.
 */

@NoRepositoryBean
public interface RepositorioCategorias extends  JpaRepository<Categoria, String> {

    /**
     * Recupera todas las categorías raíz (sin padre).
     */
    List<Categoria> getCategoriasRaiz();

    /**
     * Recupera todos los descendientes de una categoría.
     */
    List<Categoria> getDescendientes(String categoriaId);

    /**
	 * Busca categorías por nombre (búsqueda insensible a mayúsculas).
	 */
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
