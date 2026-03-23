package SegundUM.Usuarios.servicio.usuarios;

import java.time.LocalDate;
import java.util.List;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;
import SegundUM.Usuarios.servicio.ServicioException;

/**
 * Operaciones de negocio sobre usuarios.
 */
public interface ServicioUsuarios {

    List<Usuario> getAllUsuarios() throws ServicioException;

    /**
     * Da de alta un usuario y devuelve su identificador.
     * Todos los usuarios se crean con administrador = false.
     */
    String altaUsuario(String email, String nombre, String apellidos, String clave,
                       LocalDate fechaNacimiento, String telefono) throws ServicioException;

    /**
     * Modifica los datos de un usuario existente.
     * Los parámetros pueden ser null si no se quieren modificar.
     */
    void modificarUsuario(String usuarioId, String nombre, String apellidos, String clave,
                         LocalDate fechaNacimiento, String telefono) throws ServicioException;
    
    /**
     * Verifica las credenciales del usuario.
     * @return El objeto Usuario si las credenciales son correctas.
     * @throws ServicioException Si el usuario no existe o la clave es incorrecta.
     */
    Usuario login(String email, String clave) throws ServicioException;

    /**
     * Obtiene un usuario por su identificador.
     */
    Usuario getUserById(String usuarioId) throws ServicioException, EntidadNoEncontrada;

    /**
     * Elimina un usuario por su identificador.
     */
    void deleteUserById(String usuarioId) throws ServicioException, EntidadNoEncontrada;

    /**
     * Obtiene un usuario por su identificador de GitHub.
     */
    Usuario getUsuarioPorIdGitHub(String idGitHub) throws ServicioException, EntidadNoEncontrada;
}
