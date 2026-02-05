package SegundUM.Productos.servicio.categorias;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.FactoriaRepositorios;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.categorias.RepositorioCategorias;
import SegundUM.Productos.repositorio.categorias.RepositorioCategoriasXML;
import SegundUM.Productos.servicio.ServicioException;

import java.util.List;

/**
 * Implementación del servicio de categorías.
 */
public class ServicioCategoriasImpl implements ServicioCategorias {
	private static final Logger logger = LoggerFactory.getLogger(ServicioCategoriasImpl.class);

    private final RepositorioCategorias repositorioCategorias;
    private final RepositorioCategoriasXML repositorioCategoriasXML;

    public ServicioCategoriasImpl() {
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
        this.repositorioCategoriasXML = new RepositorioCategoriasXML();
    }

    @Override
    public void cargarJerarquia(String ruta) throws ServicioException {
    	// las verificaciones sobre el fichero xml las hace el repositorioXML
        try {
            Categoria raiz = repositorioCategoriasXML.getById(ruta);
            if (!repositorioCategorias.existe(raiz.getId())) {
                repositorioCategorias.add(raiz);
                logger.info("Jerarquía de categorías cargada: " + raiz.toString());
            } else {
                logger.info("La categoría " + raiz.getNombre() + " ya existe. No se cargará.");
            }

        } catch (RepositorioException e) {
        	logger.error("Error accediendo al repositorio de categorías", e);
            throw new ServicioException("Error accediendo al repositorio de categorías", e);
        } catch (Exception e) {
        	logger.error("Error al cargar la jerarquía desde el XML: " + ruta, "casusa : " + e.getCause(), e);
            throw new ServicioException("Error al cargar la jerarquía desde el XML: " + ruta, e);
        }
    }

    @Override
    public void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws ServicioException {
        try {
            Categoria c = repositorioCategorias.getById(categoriaId);
            c.setDescripcion(nuevaDescripcion);
            repositorioCategorias.update(c);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: la categoría no existe
        	logger.error("La categoría con ID " + categoriaId + " no existe en el sistema", e);
            throw new ServicioException("La categoría con ID " + categoriaId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al modificar la descripción de la categoría " + categoriaId, e);
            throw new ServicioException("Error al modificar la descripción de la categoría " + categoriaId, e);
        }
    }

    @Override
    public List<Categoria> getCategoriasRaiz() throws ServicioException {
        try {
        	logger.info("Recuperando categorías raíz");
            return repositorioCategorias.getCategoriasRaiz();
        } catch (RepositorioException e) {
        	logger.error("Error al recuperar categorías raíz", e);
            throw new ServicioException("Error al recuperar categorías raíz", e);
        }
    }

    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws ServicioException {
        try {
        	logger.info("Recuperando descendientes de la categoría con ID " + categoriaId);
            return repositorioCategorias.getDescendientes(categoriaId);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: la categoría no existe
        	logger.error("La categoría con ID " + categoriaId + " no existe en el sistema", e);
            throw new ServicioException("La categoría con ID " + categoriaId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al recuperar descendientes de " + categoriaId, e);
            throw new ServicioException("Error al recuperar descendientes de " + categoriaId, e);
        }
    }
    
    @Override
    public Categoria buscarCategoriaPorNombre(String nombre) throws ServicioException {
        try {
            List<Categoria> resultados = repositorioCategorias.buscarPorNombre(nombre);
            if (resultados.isEmpty()) {
                return null;
            }
            // Devolvemos la primera coincidencia
            return resultados.get(0);
        } catch (RepositorioException e) {
            logger.error("Error buscando categoría por nombre: " + nombre, e);
            throw new ServicioException("Error buscando categoría", e);
        }
    }
    
}