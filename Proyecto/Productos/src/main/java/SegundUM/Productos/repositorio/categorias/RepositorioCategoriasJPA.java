package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SegundUM.Productos.dominio.Categoria;

/**
 * Implementación JPA del repositorio de categorías.
 */

@Repository
public interface RepositorioCategoriasJPA extends RepositorioCategorias, JpaRepository<Categoria, String>, RepositorioCategoriasCustom {
    
	@Override
    @Query("SELECT c FROM Categoria c WHERE c.categoriaPadre IS NULL")
    List<Categoria> getCategoriasRaiz();
    
    // existe ya viene implementado en el CrudRepository de Spring
    
    @Override
    // @Query("SELECT c FROM Categoria c WHERE LOWER(c.nombre) LIKE LOWER(:nombre)")
    public List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    
}