package SegundUM.Usuarios.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.servicio.FactoriaServicios;
import SegundUM.Usuarios.servicio.ServicioException;
import SegundUM.Usuarios.servicio.usuarios.ServicioUsuarios;

/**
 * Controlador principal del Microservicio de Usuarios.
 * <p>
 * Gestiona la lógica de entrada para la gestión de usuarios:
 * registro, autenticación (login) y modificación de perfil.
 * </p>
 */
public class ControllerUsuarios {

    private static final Logger logger = LoggerFactory.getLogger(ControllerUsuarios.class);

    // Servicio del dominio de Usuarios
    private ServicioUsuarios servicioUsuarios;

    public ControllerUsuarios() {
        // Factoría cargando la implementación del microservicio de usuarios
        this.servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
    }

    // ========== CASOS DE USO DE USUARIOS ==========

    /**
     * CU1: Registrar usuario en la aplicación
     * * @param email Email del usuario (debe ser único)
     * @param nombre Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param clave Contraseña del usuario
     * @param fechaNacimiento Fecha de nacimiento
     * @param telefono Teléfono (opcional)
     * @return ID del usuario creado, o null si hay algún error
     */
    public String registrarUsuario(String email, String nombre, String apellidos, 
                                   String clave, LocalDate fechaNacimiento, 
                                   String telefono) {
        try {
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Intento de registro con email vacío");
                return null;
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                logger.warn("Intento de registro con nombre vacío");
                return null;
            }
            if (clave == null || clave.trim().isEmpty()) {
                logger.warn("Intento de registro con clave vacía");
                return null;
            }
            if (fechaNacimiento == null) {
                logger.warn("Intento de registro con fecha de nacimiento nula");
                return null;
            }

            return servicioUsuarios.altaUsuario(email, nombre, apellidos, clave, fechaNacimiento, telefono);
        } catch (ServicioException e) {
            logger.error("Error al registrar el usuario con email: " + email, e);
            return null;
        }
    }

    /**
     * CU2: Autenticación de usuario (Login)
     * * @param email Email del usuario
     * @param clave Contraseña
     * @return Objeto Usuario si las credenciales son correctas, null en caso contrario
     */
    public Usuario login(String email, String clave) {
        try {
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Intento de login con email vacío");
                return null;
            }
            if (clave == null || clave.trim().isEmpty()) {
                logger.warn("Intento de login con clave vacía");
                return null;
            }

            Usuario usuario = servicioUsuarios.login(email, clave);
            logger.info("Login exitoso para usuario: {}", usuario.getEmail());
            return usuario;

        } catch (ServicioException e) {
            // Usamos warn porque un fallo de login no es necesariamente un error del sistema
            logger.warn("Fallo de autenticación para {}: {}", email, e.getMessage());
            return null;
        }
    }

    /**
     * CU3: Modificar datos personales de usuario
     * <p>
     * Los parámetros nulos se ignoran (no se modifican).
     * </p>
     * * @param usuarioId ID del usuario a modificar
     * @param nombre Nuevo nombre (null si no se modifica)
     * @param apellidos Nuevos apellidos (null si no se modifica)
     * @param clave Nueva contraseña (null si no se modifica)
     * @param fechaNacimiento Nueva fecha de nacimiento (null si no se modifica)
     * @param telefono Nuevo teléfono (null si no se modifica)
     * @return true si la modificación fue exitosa, false en caso contrario
     */
    public boolean modificarDatosPersonales(String usuarioId, String nombre, 
                                           String apellidos, String clave, 
                                           LocalDate fechaNacimiento, 
                                           String telefono) {
        try {
            if (usuarioId == null || usuarioId.trim().isEmpty()) {
                logger.warn("Intento de modificación de usuario con ID vacío");
                return false;
            }

            // Comprobamos si todos los campos opcionales son nulos (para ahorrar la llamada)
            if (nombre == null && apellidos == null && clave == null && 
                fechaNacimiento == null && telefono == null) {
                logger.warn("Intento de modificación sin especificar cambios para ID: " + usuarioId);
                return false;
            }

            servicioUsuarios.modificarUsuario(usuarioId, nombre, apellidos, 
                                             clave, fechaNacimiento, telefono);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al modificar datos del usuario con ID: " + usuarioId, e);
            return false;
        }
    }
}