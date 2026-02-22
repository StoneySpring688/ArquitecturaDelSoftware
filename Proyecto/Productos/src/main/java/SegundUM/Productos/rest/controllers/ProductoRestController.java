package SegundUM.Productos.rest.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
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
@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {
	
    private final ServicioProductos servicioProductos;

    @Autowired
    public ProductoRestController(ServicioProductos servicioProductos) {
        this.servicioProductos = servicioProductos;
    }

    /** GET /productos/{id} — Obtener un producto por ID */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProducto(@PathVariable String id) throws ServicioException, EntidadNoEncontrada {
        Producto p = servicioProductos.getProductoPorId(id);
        return ResponseEntity.ok(p);
    }

    /** POST /productos — Dar de alta un producto */
    @PostMapping
    public ResponseEntity<Void> altaProducto(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam EstadoProducto estado,
            @RequestParam String categoriaId,
            @RequestParam boolean envioDisponible,
            @RequestParam String vendedorId) 
            		throws ServicioException {
        String id = servicioProductos.altaProducto(titulo, descripcion, precio, estado,
                categoriaId, envioDisponible, vendedorId);
        URI nuevaURI = ServletUriComponentsBuilder.fromCurrentRequest()
        		.path("/{id}")
        		.buildAndExpand(id)
        		.toUri();
        return ResponseEntity.created(nuevaURI).build();
    }

    /** PUT /productos/{id} — Modificar precio y/o descripción (con verificación de propietario) */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificarProducto(
            @PathVariable("id") String productoId,
            @RequestParam(name = "descripcion", required = false) String nuevaDescripcion,
            @RequestParam(name = "precio", required = false) BigDecimal nuevoPrecio,
            @RequestParam String usuarioId) throws ServicioException {
        servicioProductos.modificarProducto(productoId, nuevaDescripcion, nuevoPrecio, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /** PUT /productos/{id}/recogida — Asociar lugar de recogida a un producto */
    @PutMapping("/{id}/recogida")
    public ResponseEntity<Void> asociarLugarRecogida(
            @PathVariable("id") String productoId,
            @RequestParam String descripcion,
            @RequestParam Double longitud,
            @RequestParam Double latitud) 
            		throws ServicioException {
        servicioProductos.asignarLugarRecogida(productoId, descripcion, longitud, latitud);
        return ResponseEntity.noContent().build();
    }

    /** PUT /productos/{id}/visualizaciones — Registrar una nueva visualización */
    @PutMapping("/{id}/visualizaciones")
    public ResponseEntity<Void> registrarVisualizacion(@PathVariable("id") String productoId) throws ServicioException {
        servicioProductos.anadirVisualizacion(productoId);
        return ResponseEntity.noContent().build();
    }

    /** GET /productos/buscar — Buscar productos con filtros opcionales */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(
    		@RequestParam(required = false) String categoriaId,
    		@RequestParam(required = false) String texto,
    		@RequestParam(required = false) EstadoProducto estadoMinimo,
    		@RequestParam(required = false) BigDecimal precioMaximo) 
    				throws ServicioException {
        List<Producto> productos = servicioProductos.buscarProductos(categoriaId, texto,
                estadoMinimo, precioMaximo);
        return ResponseEntity.ok(productos);
    }

    /** GET /productos/vendedor/{vendedorId} — Obtener productos de un vendedor */
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Producto>> getProductosPorVendedor(@PathVariable String vendedorId) throws ServicioException {
        List<Producto> productos = servicioProductos.getProductosPorVendedor(vendedorId);
        return ResponseEntity.ok(productos);
    }

    /** GET /productos/historial?mes=X&anio=Y — Resumen mensual de productos */
    @GetMapping("/historial")
    public ResponseEntity<List<ResumenProducto>> historialMes(
    		@RequestParam int mes,
    		@RequestParam int anio) 
    				throws ServicioException {
        List<ResumenProducto> resumen = servicioProductos.historialMes(mes, anio);
        return ResponseEntity.ok(resumen);
    }

    /** GET /productos/historial/{email}?mes=X&anio=Y — Resumen mensual de un vendedor */
    @GetMapping("/historial/{email}")
    public ResponseEntity<List<ResumenProducto>> historialMesVendedor(
            @PathVariable("email") String emailVendedor,
            @RequestParam int mes,
            @RequestParam int anio) 
            		throws ServicioException {
        List<ResumenProducto> resumen = servicioProductos.historialMesVendedor(mes, anio, emailVendedor);
        return ResponseEntity.ok(resumen);
    }

    /** DELETE /productos/{id} — Eliminar un producto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("id") String productoId) throws ServicioException, EntidadNoEncontrada {
        servicioProductos.eliminarProducto(productoId);
        return ResponseEntity.noContent().build();
    }
}
