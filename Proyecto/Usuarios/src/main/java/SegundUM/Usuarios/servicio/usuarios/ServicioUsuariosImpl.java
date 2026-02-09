package SegundUM.Usuarios.servicio.usuarios;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;
import SegundUM.Usuarios.repositorio.FactoriaRepositorios;
import SegundUM.Usuarios.repositorio.RepositorioException;
import SegundUM.Usuarios.repositorio.usuarios.RepositorioUsuarios;
import SegundUM.Usuarios.servicio.ServicioException;

public class ServicioUsuariosImpl implements ServicioUsuarios {
	private final Logger logger = LoggerFactory.getLogger(ServicioUsuariosImpl.class);

	private final RepositorioUsuarios repositorioUsuarios;

	public ServicioUsuariosImpl() {
		this.repositorioUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);
	}

	@Override
	public List<Usuario> getAllUsuarios() throws ServicioException {
		try {
			return repositorioUsuarios.getAll();
		} catch (RepositorioException e) {
			logger.error("Error al obtener la lista de usuarios", e);
			throw new ServicioException("Error al obtener la lista de usuarios", e);
		}
	}
	
	@Override
	public String altaUsuario(String email, String nombre, String apellidos, String clave,
			LocalDate fechaNacimiento, String telefono) throws ServicioException {
		try {
			// VERIFICACIÓN: Comprobar que el email no existe ya
			if (repositorioUsuarios.existeEmail(email)) {
				logger.warn("Intento de alta con email ya registrado: " + email);
				throw new ServicioException("El email " + email + " ya está registrado en el sistema");
			}

			// Generar id único
			String id = UUID.randomUUID().toString();

			Usuario u = new Usuario(id, email, nombre, apellidos, clave, fechaNacimiento, telefono);
			// administrador por defecto ya false en el constructor de dominio

			logger.debug("Dando de alta nuevo usuario: " + u.toString());
			return repositorioUsuarios.add(u);
		} catch (RepositorioException e) {
			logger.error("Error al dar de alta el usuario con email: " + email, e);
			throw new ServicioException("Error al dar de alta el usuario", e);
		}
	}

	@Override
	public void modificarUsuario(String usuarioId, String nombre, String apellidos, String clave,
			LocalDate fechaNacimiento, String telefono) throws ServicioException {
		try {
			Usuario u = repositorioUsuarios.getById(usuarioId);

			if (nombre != null) u.setNombre(nombre);
			if (apellidos != null) u.setApellidos(apellidos);
			if (clave != null) u.setClave(clave);
			if (fechaNacimiento != null) u.setFechaNacimiento(fechaNacimiento);
			if (telefono != null) u.setTelefono(telefono);

			repositorioUsuarios.update(u);
		} catch (EntidadNoEncontrada e) {
			// VERIFICACIÓN: Mensaje claro cuando el usuario no existe
			logger.error("Intento de modificación de usuario inexistente con ID: " + usuarioId, e);
			throw new ServicioException("El usuario con ID " + usuarioId + " no existe en el sistema", e);
		} catch (RepositorioException e) {
			logger.error("Error al modificar el usuario con ID: " + usuarioId, e);
			throw new ServicioException("Error al modificar usuario " + usuarioId, e);
		}
	}

	@Override
	public Usuario login(String email, String clave) throws ServicioException {
		try {
			Usuario u = repositorioUsuarios.getByEmail(email);

			if (u.getClave().equals(clave)) {
				return u;
			} else {
				throw new ServicioException("Contraseña incorrecta");
			}

		} catch (EntidadNoEncontrada e) {
			throw new ServicioException("El usuario no existe", e);
		} catch (RepositorioException e) {
			throw new ServicioException("Error en el repositorio durante el login", e);
		}
	}

	@Override
	public Usuario getUserById(String usuarioId) throws ServicioException, EntidadNoEncontrada {
		try {
			return repositorioUsuarios.getById(usuarioId);
		} catch (RepositorioException e) {
			throw new ServicioException("Error al obtener el usuario con ID: " + usuarioId, e);
		}
	}

	@Override
	public void deleteUserById(String usuarioId) throws ServicioException, EntidadNoEncontrada {
		try {
			Usuario u = repositorioUsuarios.getById(usuarioId);
			repositorioUsuarios.delete(u);
		} catch (RepositorioException e) {
			throw new ServicioException("Error al eliminar el usuario con ID: " + usuarioId, e);
		}
	}

}