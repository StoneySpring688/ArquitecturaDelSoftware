package SegundUM.Productos.rest.docs;

import io.swagger.v3.oas.annotations.Operation;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import SegundUM.Productos.rest.dto.CategoriaDTO;
import SegundUM.Productos.servicio.ServicioException;

/**
 * A estas alturas ya no sé ni por que intenté hacer una documentación así. <br>
 * Si alguien quiere modificar cosas aquí... Buena suerte.
 **/
public interface CategoriasApi {

    @Operation(summary = "Obtener categorías paginadas", 
               description = "Devuelve un listado paginado de las categorías raíz")
    @GetMapping("/getAll")
    PagedModel<EntityModel<CategoriaDTO>> getCategoriasPaginado(
            @ParameterObject Pageable paginacion
    ) throws ServicioException;

    @Operation(summary = "Cargar todas las categorías", 
               description = "Carga masiva de categorías desde ficheros XML (Solo ADMIN)")
    @PostMapping("/cargar")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    ResponseEntity<String> cargarTodas() throws ServicioException;
}
