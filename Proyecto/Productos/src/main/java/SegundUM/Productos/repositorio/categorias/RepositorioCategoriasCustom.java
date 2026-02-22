package SegundUM.Productos.repositorio.categorias;

import java.util.List;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;

public interface RepositorioCategoriasCustom {
    List<Categoria> getDescendientes(String categoriaId) throws EntidadNoEncontrada;
}
