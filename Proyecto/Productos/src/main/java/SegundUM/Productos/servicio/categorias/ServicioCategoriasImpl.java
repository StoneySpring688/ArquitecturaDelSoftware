package SegundUM.Productos.servicio.categorias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.categorias.RepositorioCategoriasJPA;
import SegundUM.Productos.repositorio.categorias.RepositorioCategoriasXML;
import SegundUM.Productos.servicio.ServicioException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementación del servicio de categorías.
 */
@Service
@Transactional
public class ServicioCategoriasImpl implements ServicioCategorias {
	private static final Logger logger = LoggerFactory.getLogger(ServicioCategoriasImpl.class);

    private final RepositorioCategoriasJPA repositorioCategorias;
    private final RepositorioCategoriasXML repositorioCategoriasXML;

    @Autowired
    public ServicioCategoriasImpl(RepositorioCategoriasJPA repositorioCategorias) {
        this.repositorioCategorias = repositorioCategorias;
        this.repositorioCategoriasXML = new RepositorioCategoriasXML();
    }

    @Override
    public void cargarJerarquia(String ruta) throws ServicioException {
    	// las verificaciones sobre el fichero xml las hace el repositorioXML
        try {
            Categoria raiz = repositorioCategoriasXML.getById(ruta);
            if (!repositorioCategorias.existsById(raiz.getId())) {
                repositorioCategorias.save(raiz);
                logger.info("Jerarquía de categorías cargada: " + raiz.toString());
            } else {
                logger.warn("La categoría " + raiz.getNombre() + " ya existe. No se cargará.");
            }
        } catch (Exception e) {
        	logger.error("Error al cargar la jerarquía desde el XML: " + ruta, "casusa : " + e.getCause(), e);
            throw new ServicioException("Error al cargar la jerarquía desde el XML: " + ruta, e);
        }
    }

    @Override
    public void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws ServicioException {
            Categoria c = repositorioCategorias.findById(categoriaId).orElseThrow(() -> new ServicioException("Categoría no encontrada: " + categoriaId));
            c.setDescripcion(nuevaDescripcion);
            repositorioCategorias.save(c);
            
    }

    @Override
    public List<Categoria> getCategoriasRaiz() throws ServicioException {
        logger.info("Recuperando categorías raíz");
        return repositorioCategorias.getCategoriasRaiz();

    }

    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws ServicioException {
    	logger.info("Recuperando descendientes de la categoría con ID " + categoriaId);
        List<Categoria> descendientes = repositorioCategorias.getDescendientes(categoriaId);
        if (descendientes.isEmpty()) {
            logger.warn("No se encontraron descendientes para la categoría con ID " + categoriaId);
        }
        return descendientes;
    }
    
    @Override
    public Categoria buscarCategoriaPorNombre(String nombre) throws ServicioException {
    	List<Categoria> resultados = repositorioCategorias.findByNombreContainingIgnoreCase(nombre);
        return resultados.isEmpty() ? null : resultados.get(0);
    }
    
    @Override
    public Categoria getCategoriaById(String id) throws ServicioException, EntidadNoEncontrada {
    	return repositorioCategorias.findById(id).orElseThrow(() -> new EntidadNoEncontrada("La categoría con ID " + id + " no existe.")); 
       
    }

    @Override
    public List<Categoria> getCategorias() throws ServicioException {
    	List<Categoria> categorias = StreamSupport.stream(repositorioCategorias.findAll().spliterator(), false).collect(Collectors.toList());
    	
    	if (categorias.isEmpty()) {
			logger.warn("No se encontraron categorías en el sistema");
			throw new ServicioException("No se encontraron categorías en el sistema");
		} else {
			logger.info("Recuperadas " + categorias.size() + " categorías del sistema");
		}
    	
		return categorias;
    }
    
}