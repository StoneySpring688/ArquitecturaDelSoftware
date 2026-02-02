package SegundUM.Productos.repositorio.categorias;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.RepositorioXML;

public class RepositorioCategoriasXML extends RepositorioXML<Categoria> {

	@Override
	public Class<Categoria> getClase() {
		return Categoria.class;
	}
	
}
