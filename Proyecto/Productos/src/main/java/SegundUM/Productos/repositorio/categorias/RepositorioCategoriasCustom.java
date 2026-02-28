package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import SegundUM.Productos.dominio.Categoria;

/**
 * Interfaz custom para queries complejas de categorías.
 * Implementada en RepositorioCategoriasImpl.
 */
public interface RepositorioCategoriasCustom {
    List<Categoria> getDescendientes(String categoriaId);
}
