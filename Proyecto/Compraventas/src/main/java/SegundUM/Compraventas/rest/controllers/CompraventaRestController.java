package SegundUM.Compraventas.rest.controllers;

import java.net.URI;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import SegundUM.Compraventas.dominio.Compraventa;
import SegundUM.Compraventas.rest.dto.CompraventaDTO;
import SegundUM.Compraventas.rest.dto.NuevaCompraventaDTO;
import SegundUM.Compraventas.servicio.ServicioException;
import SegundUM.Compraventas.servicio.compraventa.ServicioCompraventa;

@RestController
@RequestMapping(value = "/api/compraventas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Compraventas", description = "Operaciones de gestión de compraventas entre usuarios")
public class CompraventaRestController {

    private static final Logger logger = LoggerFactory.getLogger(CompraventaRestController.class);
    
    private final ServicioCompraventa servicioCompraventa;
    private final PagedResourcesAssembler<CompraventaDTO> pagedResourcesAssembler;

    @Autowired
    public CompraventaRestController(ServicioCompraventa servicioCompraventa, 
                                     PagedResourcesAssembler<CompraventaDTO> pagedResourcesAssembler) {
        this.servicioCompraventa = servicioCompraventa;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Realizar una compra", description = "Registra una nueva compraventa verificando stock y usuarios")
    public ResponseEntity<Void> realizarCompra(@Valid @RequestBody NuevaCompraventaDTO dto) {
        
        logger.info("Petición REST para realizar compra. Producto: {}, Comprador: {}", dto.getIdProducto(), dto.getIdComprador());
        
        Compraventa guardada;
        try {
            guardada = servicioCompraventa.realizarCompra(
                    dto.getIdProducto(), 
                    dto.getIdComprador(), 
                    dto.getEmailComprador(), 
                    dto.getClaveComprador()
            );

        // Generamos la URI hacia el recurso creado
        URI nuevaURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(guardada.getId())
                .toUri();
                
        return ResponseEntity.created(nuevaURI).build();

        } catch (ServicioException e) {
            logger.error("Error al realizar compra: ", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
       
        
    }

    @GetMapping("/comprador/{idComprador}")
    @Operation(summary = "Historial de compras", description = "Devuelve paginadas todas las compras de un usuario")
    public PagedModel<EntityModel<CompraventaDTO>> recuperarComprasDeUsuario(
            @PathVariable String idComprador,
            @ParameterObject Pageable paginacion) {

        Page<Compraventa> pagina = servicioCompraventa.recuperarComprasDeUsuario(idComprador, paginacion);
        Page<CompraventaDTO> paginaDTO = pagina.map(CompraventaDTO::fromEntity);
        
        // HATEOAS: Convierte la página a un modelo con enlaces "self", "next", "prev", etc.
        return pagedResourcesAssembler.toModel(paginaDTO);
    }

    @GetMapping("/vendedor/{idVendedor}")
    @Operation(summary = "Historial de ventas", description = "Devuelve paginadas todas las ventas de un usuario")
    public PagedModel<EntityModel<CompraventaDTO>> recuperarVentasDeUsuario(
            @PathVariable String idVendedor,
            @ParameterObject Pageable paginacion) {

        Page<Compraventa> pagina = servicioCompraventa.recuperarVentasDeUsuario(idVendedor, paginacion);
        Page<CompraventaDTO> paginaDTO = pagina.map(CompraventaDTO::fromEntity);
        
        return pagedResourcesAssembler.toModel(paginaDTO);
    }

    @GetMapping("/entre")
    @Operation(summary = "Historial entre dos usuarios", description = "Devuelve las operaciones exactas entre un comprador y un vendedor")
    public PagedModel<EntityModel<CompraventaDTO>> recuperarCompraventasEntre(
            @RequestParam String idComprador,
            @RequestParam String idVendedor,
            @ParameterObject Pageable paginacion) {

        Page<Compraventa> pagina = servicioCompraventa.recuperarCompraventasEntre(idComprador, idVendedor, paginacion);
        Page<CompraventaDTO> paginaDTO = pagina.map(CompraventaDTO::fromEntity);
        
        return pagedResourcesAssembler.toModel(paginaDTO);
    }
}