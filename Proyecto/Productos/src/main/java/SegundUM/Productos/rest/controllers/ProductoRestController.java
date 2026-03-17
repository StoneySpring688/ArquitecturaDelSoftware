package SegundUM.Productos.rest.controllers;

import java.math.BigDecimal;
import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.rest.dto.LugarRecogidaDTO;
import SegundUM.Productos.rest.dto.ProductoDTO;
import SegundUM.Productos.rest.dto.ProductoUpdateDTO;
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
	
	private static final Logger logger = LoggerFactory.getLogger(ProductoRestController.class);
	
    private final ServicioProductos servicioProductos;
    
    @Autowired
    private PagedResourcesAssembler<ProductoDTO> pagedResourcesAssembler;
    
    @Autowired
    private PagedResourcesAssembler<ResumenProducto> pagedResourcesAssemblerRP; // TODO quitar este apaño para devolver ProductoDTO (quitar dto antiguo)

    @Autowired
    public ProductoRestController(ServicioProductos servicioProductos) {
        this.servicioProductos = servicioProductos;
    }

    /** GET /productos/{id} — Obtener un producto por ID */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProducto(@PathVariable String id) throws ServicioException, EntidadNoEncontrada {
        Producto p = servicioProductos.getProductoPorId(id);
        logger.info("Producto obtenido: {}", ProductoDTO.fromEntity(p).toString());
        return ResponseEntity.ok(ProductoDTO.fromEntity(p));
    }

    /** POST /productos — Dar de alta un producto */
    @PostMapping
    public ResponseEntity<String> altaProducto(@Valid @RequestBody ProductoDTO dto) 
            		throws ServicioException {
        String id = servicioProductos.altaProducto(dto.titulo, dto.descripcion, dto.precio, dto.estado,
        		dto.categoriaId, dto.envioDisponible, dto.vendedorId);
        
        URI nuevaURI = ServletUriComponentsBuilder.fromCurrentRequest()
        		.path("/{id}")
        		.buildAndExpand(id)
        		.toUri();
        return ResponseEntity.created(nuevaURI).body(id);

    }

    /** PUT /productos/{id} — Modificar precio y/o descripción (con verificación de propietario) */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificarProducto(
    		@PathVariable("id") String productoId,
            @Valid @RequestBody ProductoUpdateDTO dto) throws ServicioException {
    	
        servicioProductos.modificarProducto(productoId, dto.descripcion, dto.precio, dto.vendedorId);
        return ResponseEntity.noContent().build();
    }

    /** PUT /productos/{id}/recogida — Asociar lugar de recogida a un producto */
    @PutMapping("/{id}/recogida")
    public ResponseEntity<Void> asociarLugarRecogida(
            @PathVariable("id") String productoId,
           @Valid @RequestBody LugarRecogidaDTO dto) 
            		throws ServicioException {
    	
        servicioProductos.asignarLugarRecogida(productoId, dto.descripcion, dto.longitud, dto.latitud);
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
    public PagedModel<EntityModel<ProductoDTO>> buscarProductos(
    		@RequestParam(required = false) String categoriaId,
    		@RequestParam(required = false) String texto,
    		@RequestParam(required = false) EstadoProducto estadoMinimo,
    		@RequestParam(required = false) BigDecimal precioMaximo,
    		@ParameterObject Pageable paginacion)
    				throws ServicioException {
        Page<Producto> productos = servicioProductos.buscarProductos(categoriaId, texto,
                estadoMinimo, precioMaximo, paginacion);
        
        Page<ProductoDTO> productosDtos = productos.map(ProductoDTO::fromEntity);
        
        return pagedResourcesAssembler.toModel(productosDtos);
    }

    /** GET /productos/vendedor/{vendedorId} — Obtener productos de un vendedor */
    @GetMapping("/vendedor/{vendedorId}")
    public PagedModel<EntityModel<ProductoDTO>> getProductosPorVendedor(
    		@PathVariable String vendedorId,
    		@ParameterObject Pageable paginacion) 
    				throws ServicioException {
        Page<Producto> productos = servicioProductos.getProductosPorVendedor(vendedorId, paginacion);
        
        Page<ProductoDTO> productosDtos = productos.map(ProductoDTO::fromEntity);
        
        return pagedResourcesAssembler.toModel(productosDtos);
    }

    /** GET /productos/historial?mes=X&anio=Y — Resumen mensual de productos */
    @GetMapping("/historial")
    public PagedModel<EntityModel<ResumenProducto>> historialMes(
    		@RequestParam int mes,
    		@RequestParam int anio,
    		@ParameterObject Pageable paginacion) 
    				throws ServicioException {
        Page<ResumenProducto> resumen = servicioProductos.historialMes(mes, anio, paginacion);
        return pagedResourcesAssemblerRP.toModel(resumen);
    }

    /** GET /productos/historial/{email}?mes=X&anio=Y — Resumen mensual de un vendedor */
    @GetMapping("/historial/{email}")
    public PagedModel<EntityModel<ResumenProducto>> historialMesVendedor(
            @PathVariable("email") String emailVendedor,
            @RequestParam int mes,
            @RequestParam int anio,
            @ParameterObject Pageable paginacion) 
            		throws ServicioException {
        Page<ResumenProducto> resumen = servicioProductos.historialMesVendedor(mes, anio, emailVendedor, paginacion);
        return pagedResourcesAssemblerRP.toModel(resumen);
    }

    /** DELETE /productos/{id} — Eliminar un producto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("id") String productoId) throws ServicioException, EntidadNoEncontrada {
        servicioProductos.eliminarProducto(productoId);
        return ResponseEntity.noContent().build();
    }
}
