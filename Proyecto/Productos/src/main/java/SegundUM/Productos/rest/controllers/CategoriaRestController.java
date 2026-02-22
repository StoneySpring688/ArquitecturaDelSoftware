package SegundUM.Productos.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
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
public class CategoriaRestController {

    private final ServicioCategorias servicioCategorias;

    @Autowired
    public CategoriaRestController(ServicioCategorias servicioCategorias) {
        this.servicioCategorias = servicioCategorias;
    }

    /** GET /categorias/{id} — Obtener una categoría por ID */
    @GetMapping("/{id}")
    public Categoria getCategoria(@PathVariable String id) throws ServicioException, EntidadNoEncontrada {
        return servicioCategorias.getCategoriaById(id);
        
    }

    /** GET /categorias/ — Listar todas las categorías */
    @GetMapping
    public List<Categoria> getCategorias() throws ServicioException {
        return servicioCategorias.getCategorias();
    }
}
