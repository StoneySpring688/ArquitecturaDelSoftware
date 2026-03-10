package SegundUM.Usuarios.rest.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.servicio.FactoriaServicios;
import SegundUM.Usuarios.servicio.ServicioException;
import SegundUM.Usuarios.servicio.usuarios.ServicioUsuarios;

/**
 * Controlador de autenticación.
 * Gestiona el registro y login de usuarios, emitiendo tokens JWT.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public static final String SECRET_KEY = "clave_secreta_segund_um_2026";
    private ServicioUsuarios servicioUsuarios;

    public AuthController() {
        this.servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
    }

    /** POST /auth/login — Login con email y clave, devuelve token JWT (pública) */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(
            @FormParam("email") String email,
            @FormParam("clave") String clave) {
    	
    	logger.info("Recibida petición de login (JWT) para el email: {}", email);
    	
        try {
            Usuario usuario = servicioUsuarios.login(email, clave);
            
            logger.debug("Login correcto en servicio para usuario ID: {}", usuario.getId());

            // Construir los claims del token
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", usuario.getId());
            claims.put("name", usuario.getNombre());
            claims.put("roles", "USUARIO");

            // Fecha de caducidad: 1 hora
            Date caducidad = Date.from(
                    Instant.now().plusSeconds(3600));

            // Generar el token JWT
            String token = Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .setExpiration(caducidad)
                    .compact();
            
            logger.info("Token JWT generado exitosamente para el usuario: {}", email);

            return Response.ok(token).build();

        } catch (ServicioException e) {
        	logger.warn("Intento de login fallido para el email '{}'. Motivo: {}", email, e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas").build();
            
        } catch (Exception e) {
            logger.error("Error inesperado durante el proceso de login JWT: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }
}
