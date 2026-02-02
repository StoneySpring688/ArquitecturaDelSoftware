package SegundUM.Productos.repositorio.usuarios;

import SegundUM.Productos.dominio.Usuario;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.RepositorioString;

public interface RepositorioUsuarios extends RepositorioString<Usuario> {
    
    Usuario getByEmail(String email) throws RepositorioException, EntidadNoEncontrada;
}