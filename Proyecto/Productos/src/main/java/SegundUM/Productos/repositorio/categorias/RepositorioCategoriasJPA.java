package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SegundUM.Productos.dominio.Categoria;

/**
 * Implementación JPA del repositorio de categorías.
 */

@Repository
public interface RepositorioCategoriasJPA extends RepositorioCategorias, JpaRepository<Categoria, String>, RepositorioCategoriasCustom {

	/**
     * Recupera todas las categorías raíz (sin padre).
     */
	@Deprecated
    @Override
    @Query("SELECT c FROM Categoria c WHERE c.categoriaPadre IS NULL")
    List<Categoria> getCategoriasRaiz();
    
    @Override
    @Query("SELECT c FROM Categoria c WHERE c.categoriaPadre IS NULL")
    Page<Categoria> getCategoriasRaiz(Pageable pageable);

    /**
	 * Busca categorías por nombre (búsqueda insensible a mayúsculas).
	 */
    @Override
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
