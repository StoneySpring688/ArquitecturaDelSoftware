package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import SegundUM.Productos.dominio.Categoria;


/**
 * Repositorio específico para Categorías con operaciones AdHoc.
 */

@NoRepositoryBean
public interface RepositorioCategorias extends PagingAndSortingRepository<Categoria, String> {

    /**
     * Recupera todas las categorías raíz (sin padre).
     */
	@Deprecated
    List<Categoria> getCategoriasRaiz();
	
	Page<Categoria> getCategoriasRaiz(Pageable pageable);

    /**
     * Recupera todos los descendientes de una categoría.
     */
    List<Categoria> getDescendientes(String categoriaId);

    /**
	 * Busca categorías por nombre (búsqueda insensible a mayúsculas).
	 */
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
