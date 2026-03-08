package SegundUM.Productos.servicio.categorias;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.servicio.ServicioException;

public interface ServicioCategorias {

    /**
     * Carga una jerarquía de categorías desde un fichero XML.
     * - ruta puede ser:
     *    - un path en classpath (ej: "categoriasXML/Arte_y_ocio.xml")
     *    - o un path absoluto/relativo del sistema de ficheros
     *
     * No debe cargar una categoría principal si ya existe en el sistema.
     */
    void cargarJerarquia(String ruta) throws ServicioException;

    /**
     * Modifica la descripción de una categoría.
     */
    void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws ServicioException;

    /**
     * Devuelve las categorías raíz (categoriaPadre == null).
     */
    List<Categoria> getCategoriasRaiz() throws ServicioException;

    /**
     * Devuelve todos los descendientes (directos e indirectos) de la categoría indicada.
     */
    List<Categoria> getDescendientes(String categoriaId) throws ServicioException;
    
    /**
     * Busca una categoría por su nombre (o parte de él).
     * Devuelve la primera coincidencia o null si no existe.
     */
    Categoria buscarCategoriaPorNombre(String nombre) throws ServicioException;

    Categoria getCategoriaById(String id) throws ServicioException, EntidadNoEncontrada;
    
    @Deprecated
    List<Categoria> getCategorias() throws ServicioException ;
    
    Page<Categoria> getCategoriasPaginado(Pageable pageable);

}