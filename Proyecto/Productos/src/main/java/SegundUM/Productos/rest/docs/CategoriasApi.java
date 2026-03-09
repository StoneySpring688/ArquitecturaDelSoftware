package SegundUM.Productos.rest.docs;

import io.swagger.v3.oas.annotations.Operation;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import SegundUM.Productos.rest.dto.CategoriaDTO;
import SegundUM.Productos.servicio.ServicioException;

public interface CategoriasApi {

    @Operation(summary = "Obtener categorías paginadas", 
               description = "Devuelve un listado paginado de las categorías raíz")
    @GetMapping
    PagedModel<EntityModel<CategoriaDTO>> getCategoriasPaginado(
            @ParameterObject Pageable paginacion
    ) throws ServicioException;
}
