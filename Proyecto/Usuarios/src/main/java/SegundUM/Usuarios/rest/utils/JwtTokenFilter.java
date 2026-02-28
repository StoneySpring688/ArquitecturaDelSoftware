package SegundUM.Usuarios.rest.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import SegundUM.Usuarios.rest.controllers.AuthController;

/**
 * Filtro JAX-RS para control de autenticación y autorización mediante JWT.
 *
 * - Comprueba si la ruta es pública (@PermitAll) y la deja pasar.
 * - Para rutas protegidas, valida el token JWT de la cabecera Authorization.
 * - Si la ruta tiene @RolesAllowed, verifica que el usuario tenga el rol adecuado.
 * - Pone la información del token (claims) como propiedad de la petición.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtTokenFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Comprobamos si la ruta tiene la anotación @PermitAll
        if (resourceInfo.getResourceMethod()
                .isAnnotationPresent(PermitAll.class)) {
            return; // no se controla la autorización
        }

        // Implementación del control de autorización
        String authorization = requestContext.getHeaderString("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        } else {
            String token = authorization.substring("Bearer ".length()).trim();
            try {
                // Validar el token
                Claims claims = Jwts.parser()
                        .setSigningKey(AuthController.SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                // Poner los claims como propiedad de la petición para que estén disponibles en el controlador
                requestContext.setProperty("claims", claims);

                // Autorización basada en roles
                Set<String> roles = new HashSet<>(
                        Arrays.asList(claims.get("roles", String.class).split(",")));

                // Consulta si la operación está protegida por rol
                if (resourceInfo.getResourceMethod()
                        .isAnnotationPresent(RolesAllowed.class)) {

                    String[] allowedRoles = resourceInfo.getResourceMethod()
                            .getAnnotation(RolesAllowed.class).value();

                    if (roles.stream()
                            .noneMatch(userRole -> Arrays.asList(allowedRoles)
                                    .contains(userRole))) {
                        requestContext.abortWith(
                                Response.status(Response.Status.FORBIDDEN).build());
                    }
                }

            } catch (Exception e) { // Error de validación (token inválido o caducado)
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}
