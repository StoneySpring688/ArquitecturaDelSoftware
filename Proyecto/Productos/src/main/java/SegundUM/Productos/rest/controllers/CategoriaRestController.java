package SegundUM.Productos.rest.controllers;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.servicio.FactoriaServicios;
import SegundUM.Productos.servicio.ServicioException;
import SegundUM.Productos.servicio.categorias.ServicioCategorias;

/**
 * Controlador REST para la gestión de categorías de productos.
 *
 * Expone operaciones de consulta sobre el árbol de categorías.
 *
 * Base path: /api/categorias
 */
@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaRestController {

    private ServicioCategorias servicioCategorias;

    public CategoriaRestController() {
        this.servicioCategorias = FactoriaServicios.getServicio(ServicioCategorias.class);
    }

    /** GET /categorias/{id} — Obtener una categoría por ID */
    @GET
    @Path("/{id}")
    public Response getCategoria(@PathParam("id") String id) {
        try {
            Categoria categoria = servicioCategorias.getCategoriaById(id);
            return Response.ok(categoria).build();
        } catch (EntidadNoEncontrada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** GET /categorias/ — Listar todas las categorías */
    @GET
    @Path("/")
    public Response getCategorias() {
        try {
            return Response.ok(servicioCategorias.getCategorias()).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
