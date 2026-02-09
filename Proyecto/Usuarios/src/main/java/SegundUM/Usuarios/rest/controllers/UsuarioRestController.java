package SegundUM.Usuarios.rest.controllers;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import SegundUM.Usuarios.dominio.ResumenUsuario;
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

    /** GET /usuarios — Obtener todos los usuarios */
    @GET
    @Path("/")
    public Response getAllusuarios() throws ServicioException {

        List<ResumenUsuario> usuarios = servicioUsuarios.getAllUsuarios().stream()
                .map(u -> new ResumenUsuario(u.getId(), u.getEmail(), u.getNombre(),
                        u.getApellidos(), u.getFechaNacimiento(), u.getTelefono(), u.isAdministrador()))
                .collect(Collectors.toList());

        if (usuarios.isEmpty()) {
            return Response.noContent().build();
        }

        return Response.ok(usuarios).build();
    }

    /** GET /usuarios/{id} — Obtener usuario por ID */
    @GET
    @Path("/{id}")
    public Response getUsuario(@PathParam("id") String id) throws ServicioException, EntidadNoEncontrada {
        Usuario usuario = servicioUsuarios.getUserById(id);
        ResumenUsuario resumen = new ResumenUsuario(usuario.getId(), usuario.getEmail(),
                usuario.getNombre(), usuario.getApellidos(), usuario.getFechaNacimiento(),
                usuario.getTelefono(), usuario.isAdministrador());
        return Response.ok(resumen).build();
    }

    /** POST /usuarios — Registrar un nuevo usuario */
    @POST
    public Response registrarUsuario(
            @QueryParam("email") String email,
            @QueryParam("nombre") String nombre,
            @QueryParam("apellidos") String apellidos,
            @QueryParam("clave") String clave,
            @QueryParam("fechaNacimiento") String fechaNacimiento,
            @QueryParam("telefono") String telefono) throws ServicioException {
        LocalDate fecha = LocalDate.parse(fechaNacimiento);
        String id = servicioUsuarios.altaUsuario(email, nombre, apellidos, clave, fecha, telefono);
        URI nuevaURI = uriInfo.getAbsolutePathBuilder().path(id).build();
        return Response.created(nuevaURI).entity(id).build();
    }

    /** POST /usuarios/login — Autenticar usuario */
    @POST
    @Path("/login")
    public Response login(
            @QueryParam("email") String email,
            @QueryParam("clave") String clave) throws ServicioException {
        Usuario usuario = servicioUsuarios.login(email, clave);
        ResumenUsuario resumen = new ResumenUsuario(usuario.getId(), usuario.getEmail(),
                usuario.getNombre(), usuario.getApellidos(), usuario.getFechaNacimiento(),
                usuario.getTelefono(), usuario.isAdministrador());
        return Response.ok(resumen).build();
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
            @QueryParam("telefono") String telefono) throws ServicioException {
        LocalDate fecha = fechaNacimiento != null ? LocalDate.parse(fechaNacimiento) : null;
        servicioUsuarios.modificarUsuario(usuarioId, nombre, apellidos, clave, fecha, telefono);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /** DELETE /usuarios/{id} — Eliminar usuario */
    @DELETE
    @Path("/{id}")
    public Response eliminarUsuario(@PathParam("id") String id) throws ServicioException, EntidadNoEncontrada {
        servicioUsuarios.deleteUserById(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
