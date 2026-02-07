package SegundUM.Usuarios.rest.controllers;

import java.net.URI;
import java.time.LocalDate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import SegundUM.Usuarios.dominio.Usuario;
import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;
import SegundUM.Usuarios.servicio.FactoriaServicios;
import SegundUM.Usuarios.servicio.ServicioException;
import SegundUM.Usuarios.servicio.usuarios.ServicioUsuarios;

/**
 * Controlador REST para la gestión de usuarios.
 *
 * Expone operaciones de registro, autenticación (login),
 * consulta, modificación y eliminación de usuarios.
 *
 * Base path: /api/usuarios
 */
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioRestController {

    @Context
    private UriInfo uriInfo;
    private ServicioUsuarios servicioUsuarios;

    public UsuarioRestController() {
        this.servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
    }

    /** GET /usuarios/{id} — Obtener usuario por ID */
    @GET
    @Path("/{id}")
    public Response getUsuario(@PathParam("id") String id) {
        try {
            Usuario usuario = servicioUsuarios.getUserById(id);
            return Response.ok(usuario).build();
        } catch (EntidadNoEncontrada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** POST /usuarios — Registrar un nuevo usuario */
    @POST
    public Response registrarUsuario(
            @QueryParam("email") String email,
            @QueryParam("nombre") String nombre,
            @QueryParam("apellidos") String apellidos,
            @QueryParam("clave") String clave,
            @QueryParam("fechaNacimiento") String fechaNacimiento,
            @QueryParam("telefono") String telefono) {
        try {
            LocalDate fecha = LocalDate.parse(fechaNacimiento);
            String id = servicioUsuarios.altaUsuario(email, nombre, apellidos, clave, fecha, telefono);
            URI nuevaURI = uriInfo.getAbsolutePathBuilder().path(id).build();
            return Response.created(nuevaURI).entity(id).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** POST /usuarios/login — Autenticar usuario */
    @POST
    @Path("/login")
    public Response login(
            @QueryParam("email") String email,
            @QueryParam("clave") String clave) {
        try {
            Usuario usuario = servicioUsuarios.login(email, clave);
            return Response.ok(usuario).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    /** PUT /usuarios/{id} — Modificar datos del usuario */
    @PUT
    @Path("/{id}")
    public Response modificarUsuario(
            @PathParam("id") String usuarioId,
            @QueryParam("nombre") String nombre,
            @QueryParam("apellidos") String apellidos,
            @QueryParam("clave") String clave,
            @QueryParam("fechaNacimiento") String fechaNacimiento,
            @QueryParam("telefono") String telefono) {
        try {
            LocalDate fecha = fechaNacimiento != null ? LocalDate.parse(fechaNacimiento) : null;
            servicioUsuarios.modificarUsuario(usuarioId, nombre, apellidos, clave, fecha, telefono);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** DELETE /usuarios/{id} — Eliminar usuario */
    @DELETE
    @Path("/{id}")
    public Response eliminarUsuario(@PathParam("id") String id) {
        try {
            servicioUsuarios.deleteUserById(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (EntidadNoEncontrada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
