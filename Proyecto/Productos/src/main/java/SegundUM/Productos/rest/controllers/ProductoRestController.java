package SegundUM.Productos.rest.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.servicio.FactoriaServicios;
import SegundUM.Productos.servicio.ServicioException;
import SegundUM.Productos.servicio.productos.ServicioProductos;

/**
 * Controlador REST para la gestión de productos.
 *
 * Expone operaciones CRUD, búsqueda con filtros, gestión de
 * visualizaciones, lugares de recogida e historial de ventas.
 *
 * Base path: /api/productos
 */
@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoRestController {

    @Context
    private UriInfo uriInfo;
    private ServicioProductos servicioProductos;

    public ProductoRestController() {
        this.servicioProductos = FactoriaServicios.getServicio(ServicioProductos.class);
    }

    /** GET /productos/{id} — Obtener un producto por ID */
    @GET
    @Path("/{id}")
    public Response getProducto(@PathParam("id") String id) {
        try {
            Producto producto = servicioProductos.getProductoPorId(id);
            return Response.ok(producto).build();
        } catch (EntidadNoEncontrada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** POST /productos — Dar de alta un producto */
    @POST
    public Response altaProducto(
            @QueryParam("titulo") String titulo,
            @QueryParam("descripcion") String descripcion,
            @QueryParam("precio") BigDecimal precio,
            @QueryParam("estado") EstadoProducto estado,
            @QueryParam("categoriaId") String categoriaId,
            @QueryParam("envioDisponible") boolean envioDisponible,
            @QueryParam("vendedorId") String vendedorId) {
        try {
            String id = servicioProductos.altaProducto(titulo, descripcion, precio, estado,
                    categoriaId, envioDisponible, vendedorId);
            URI nuevaURI = uriInfo.getAbsolutePathBuilder().path(id).build();
            return Response.created(nuevaURI).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** PUT /productos/{id} — Modificar precio y/o descripción (con verificación de propietario) */
    @PUT
    @Path("/{id}")
    public Response modificarProducto(
            @PathParam("id") String productoId,
            @QueryParam("descripcion") String nuevaDescripcion,
            @QueryParam("precio") BigDecimal nuevoPrecio,
            @QueryParam("usuarioId") String usuarioId) {
        try {
            servicioProductos.modificarProducto(productoId, nuevaDescripcion, nuevoPrecio, usuarioId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** PUT /productos/{id}/recogida — Asociar lugar de recogida a un producto */
    @PUT
    @Path("/{id}/recogida")
    public Response asociarLugarRecogida(
            @PathParam("id") String productoId,
            @QueryParam("descripcion") String descripcion,
            @QueryParam("longitud") Double longitud,
            @QueryParam("latitud") Double latitud) {
        try {
            servicioProductos.asignarLugarRecogida(productoId, descripcion, longitud, latitud);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** PUT /productos/{id}/visualizaciones — Registrar una nueva visualización */
    @PUT
    @Path("/{id}/visualizaciones")
    public Response registrarVisualizacion(@PathParam("id") String productoId) {
        try {
            servicioProductos.anadirVisualizacion(productoId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** GET /productos/buscar — Buscar productos con filtros opcionales */
    @GET
    @Path("/buscar")
    public Response buscarProductos(
            @QueryParam("categoriaId") String categoriaId,
            @QueryParam("texto") String texto,
            @QueryParam("estadoMinimo") EstadoProducto estadoMinimo,
            @QueryParam("precioMaximo") BigDecimal precioMaximo) {
        try {
            List<Producto> productos = servicioProductos.buscarProductos(categoriaId, texto,
                    estadoMinimo, precioMaximo);
            return Response.ok(productos).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** GET /productos/vendedor/{vendedorId} — Obtener productos de un vendedor */
    @GET
    @Path("/vendedor/{vendedorId}")
    public Response getProductosPorVendedor(@PathParam("vendedorId") String vendedorId) {
        try {
            List<Producto> productos = servicioProductos.getProductosPorVendedor(vendedorId);
            return Response.ok(productos).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** GET /productos/historial?mes=X&anio=Y — Resumen mensual de productos */
    @GET
    @Path("/historial")
    public Response historialMes(
            @QueryParam("mes") int mes,
            @QueryParam("anio") int anio) {
        try {
            List<ResumenProducto> resumen = servicioProductos.historialMes(mes, anio);
            return Response.ok(resumen).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** GET /productos/historial/{email}?mes=X&anio=Y — Resumen mensual de un vendedor */
    @GET
    @Path("/historial/{email}")
    public Response historialMesVendedor(
            @PathParam("email") String emailVendedor,
            @QueryParam("mes") int mes,
            @QueryParam("anio") int anio) {
        try {
            List<ResumenProducto> resumen = servicioProductos.historialMesVendedor(mes, anio, emailVendedor);
            return Response.ok(resumen).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /** DELETE /productos/{id} — Eliminar un producto */
    @DELETE
    @Path("/{id}")
    public Response eliminarProducto(@PathParam("id") String productoId) {
        try {
            servicioProductos.eliminarProducto(productoId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (EntidadNoEncontrada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (ServicioException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
