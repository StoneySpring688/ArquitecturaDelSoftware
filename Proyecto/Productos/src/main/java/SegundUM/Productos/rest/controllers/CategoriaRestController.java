package SegundUM.Productos.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.rest.docs.CategoriasApi;
import SegundUM.Productos.rest.dto.CategoriaDTO;
import SegundUM.Productos.servicio.ServicioException;
import SegundUM.Productos.servicio.categorias.ServicioCategorias;

/**
 * Controlador REST para la gestión de categorías de productos.
 *
 * Expone operaciones de consulta sobre el árbol de categorías.
 *
 * Base path: /api/categorias
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaRestController implements CategoriasApi {

    private final ServicioCategorias servicioCategorias;
    
    @Autowired
    private PagedResourcesAssembler<CategoriaDTO> pagedResourcesAssembler;

    @Autowired
    public CategoriaRestController(ServicioCategorias servicioCategorias) {
        this.servicioCategorias = servicioCategorias;
    }

    /** GET /categorias/{id} — Obtener una categoría por ID */
    @GetMapping("/{id}")
    public EntityModel<CategoriaDTO> getCategoria(@PathVariable String id) throws ServicioException, EntidadNoEncontrada {
        return EntityModel.of(CategoriaDTO.fromEntity(servicioCategorias.getCategoriaById(id)))
        		.add(
        				WebMvcLinkBuilder.linkTo(
        						WebMvcLinkBuilder.methodOn(CategoriaRestController.class).getCategoria(id)
        						)
        				.withSelfRel()
        				);
        
    }

    /** GET /categorias/ — Listar todas las categorías
     *  <br>
     *  Muestra las categorias como la raíz y sus hijas, para evitar duplicidad (si no, no funciona con el swagger ui)*/
    /*@GetMapping
    public ResponseEntity<List<CategoriaDTO>> getCategorias() throws ServicioException {
    	List<CategoriaDTO> categoriasDTO = servicioCategorias.getCategorias().stream()
    			.filter(c -> c.getCategoriaPadre() == null)
    			.map(CategoriaDTO::fromEntity)
    			.toList();
        return ResponseEntity.ok(categoriasDTO);  
    }*/
    @Override
    public PagedModel<EntityModel<CategoriaDTO>> getCategoriasPaginado(Pageable paginacion) throws ServicioException {
    	Page<CategoriaDTO> categoriasDTO = servicioCategorias.getCategoriasPaginado(paginacion)
    			.map(CategoriaDTO::fromEntity);
        return pagedResourcesAssembler.toModel(categoriasDTO);
    }
}
