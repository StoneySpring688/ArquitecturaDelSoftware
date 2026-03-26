package SegundUM.Usuarios.servicio.usuarios;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.puertos.PuertoEntradaEventos;
import SegundUM.Usuarios.puertos.PuertoSalidaEventos;
import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;
import SegundUM.Usuarios.repositorio.FactoriaRepositorios;
import SegundUM.Usuarios.repositorio.RepositorioException;
import SegundUM.Usuarios.repositorio.usuarios.RepositorioUsuarios;
import SegundUM.Usuarios.servicio.ServicioException;

public class ServicioUsuariosImpl implements ServicioUsuarios, PuertoEntradaEventos {
	private final Logger logger = LoggerFactory.getLogger(ServicioUsuariosImpl.class);

	private final RepositorioUsuarios repositorioUsuarios;
	private PuertoSalidaEventos puertoSalidaEventos;

	public ServicioUsuariosImpl() {
		this.repositorioUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);
	}

	/**
	 * Permite inyectar el puerto de salida de eventos.
	 * Se usa desde App.java tras crear el adaptador RabbitMQ.
	 */
	public void setPuertoSalidaEventos(PuertoSalidaEventos puertoSalidaEventos) {
		this.puertoSalidaEventos = puertoSalidaEventos;
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
			if (repositorioUsuarios.existeEmail(email)) {
				logger.warn("Intento de alta con email ya registrado: " + email);
				throw new ServicioException("El email " + email + " ya esta registrado en el sistema");
			}

			String id = UUID.randomUUID().toString();

			Usuario u = new Usuario(id, email, nombre, apellidos, clave, fechaNacimiento, telefono);

			logger.debug("Dando de alta nuevo usuario: " + u.toString());
			String resultado = repositorioUsuarios.add(u);

			// Publicar evento usuario-creado 
			return resultado;
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

			boolean nombreCambiado = (nombre != null && !nombre.equals(u.getNombre()));

			if (nombre != null) u.setNombre(nombre);
			if (apellidos != null) u.setApellidos(apellidos);
			if (clave != null) u.setClave(clave);
			if (fechaNacimiento != null) u.setFechaNacimiento(fechaNacimiento);
			if (telefono != null) u.setTelefono(telefono);

			repositorioUsuarios.update(u);

			// Si el nombre cambio, publicar evento
			if (nombreCambiado && puertoSalidaEventos != null) {
				puertoSalidaEventos.publicarUsuarioModificado(usuarioId, nombre);
			}
		} catch (EntidadNoEncontrada e) {
			logger.error("Intento de modificacion de usuario inexistente con ID: " + usuarioId, e);
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
				throw new ServicioException("Contrasena incorrecta");
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

			// Publicar evento usuario-eliminado
			if (puertoSalidaEventos != null) {
				puertoSalidaEventos.publicarUsuarioEliminado(usuarioId);
			}
		} catch (RepositorioException e) {
			throw new ServicioException("Error al eliminar el usuario con ID: " + usuarioId, e);
		}
	}

	@Override
	public Usuario getUsuarioPorIdGitHub(String idGitHub) throws ServicioException, EntidadNoEncontrada {
		try {
			return repositorioUsuarios.getByIdGitHub(idGitHub);
		} catch (RepositorioException e) {
			throw new ServicioException("Error al obtener el usuario por idGitHub: " + idGitHub, e);
		}
	}

	@Override
	public String altaUsuarioGitHub(String idGitHub, String nombre, String email) throws ServicioException {
		try {
			String finalEmail = (email != null) ? email : idGitHub + "@github.com";
			
			if (repositorioUsuarios.existeEmail(finalEmail)) {
				try {
					Usuario existente = repositorioUsuarios.getByIdGitHub(idGitHub);
					return existente.getId();
				} catch (EntidadNoEncontrada | RepositorioException e) {
					throw new ServicioException("El email " + finalEmail + " ya esta registrado por otro usuario");
				}
			}

			String id = java.util.UUID.randomUUID().toString();
			Usuario u = new Usuario(id, finalEmail, nombre, null, null, null, null);
			u.setIdGitHub(idGitHub);

			logger.debug("Dando de alta nuevo usuario desde GitHub: " + u.toString());
			return repositorioUsuarios.add(u);
		} catch (RepositorioException e) {
			logger.error("Error al dar de alta el usuario GitHub: " + idGitHub, e);
			throw new ServicioException("Error al dar de alta el usuario GitHub", e);
		}
	}

	// --- Implementacion PuertoEntradaEventos ---

	@Override
	public void manejarCompraventaCreada(String idComprador, String idVendedor) throws ServicioException {
		try {
			Usuario comprador = repositorioUsuarios.getById(idComprador);
			comprador.setComprasRealizadas(comprador.getComprasRealizadas() + 1);
			repositorioUsuarios.update(comprador);
			logger.info("Compras realizadas del usuario {} incrementadas a {}", idComprador, comprador.getComprasRealizadas());

			Usuario vendedor = repositorioUsuarios.getById(idVendedor);
			vendedor.setVentasRealizadas(vendedor.getVentasRealizadas() + 1);
			repositorioUsuarios.update(vendedor);
			logger.info("Ventas realizadas del usuario {} incrementadas a {}", idVendedor, vendedor.getVentasRealizadas());

		} catch (EntidadNoEncontrada e) {
			logger.error("Error: usuario no encontrado al procesar evento compraventa-creada", e);
			throw new ServicioException("Usuario no encontrado al procesar evento", e);
		} catch (RepositorioException e) {
			logger.error("Error de repositorio al procesar evento compraventa-creada", e);
			throw new ServicioException("Error al procesar evento compraventa-creada", e);
		}
	}

	@Override
	public void manejarProductoCreado(String idProducto, String vendedorId) throws ServicioException {
		try {
			Usuario vendedor = repositorioUsuarios.getById(vendedorId);
			vendedor.getProductosId().add(idProducto);
			repositorioUsuarios.update(vendedor);
			logger.info("Producto {} anadido a la lista de productos del usuario {}", idProducto, vendedorId);

		} catch (EntidadNoEncontrada e) {
			logger.error("Error: usuario no encontrado al procesar evento producto-creado", e);
			throw new ServicioException("Usuario no encontrado al procesar evento", e);
		} catch (RepositorioException e) {
			logger.error("Error de repositorio al procesar evento producto-creado", e);
			throw new ServicioException("Error al procesar evento producto-creado", e);
		}
	}

	@Override
	public void manejarProductoEliminado(String idProducto, String vendedorId) throws ServicioException {
		try {
			Usuario vendedor = repositorioUsuarios.getById(vendedorId);
			vendedor.getProductosId().remove(idProducto);
			repositorioUsuarios.update(vendedor);
			logger.info("Producto {} eliminado de la lista de productos del usuario {}", idProducto, vendedorId);

		} catch (EntidadNoEncontrada e) {
			logger.error("Error: usuario no encontrado al procesar evento producto-eliminado", e);
			throw new ServicioException("Usuario no encontrado al procesar evento", e);
		} catch (RepositorioException e) {
			logger.error("Error de repositorio al procesar evento producto-eliminado", e);
			throw new ServicioException("Error al procesar evento producto-eliminado", e);
		}
	}
}
