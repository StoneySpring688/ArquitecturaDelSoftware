package SegundUM.Productos.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.servicio.FactoriaServicios;
import SegundUM.Productos.servicio.ServicioException;
import SegundUM.Productos.servicio.categorias.ServicioCategorias;
import SegundUM.Productos.servicio.productos.ServicioProductos;

/**
 * Controlador principal del Microservicio de Productos.
 * <p>
 * Gestiona la lógica de entrada para productos y categorías.
 * Se han eliminado las dependencias del servicio de usuarios.
 * </p>
 */
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private static final String CARPETA_CATEGORIAS = "categoriasXML";
    
    // Solo servicios del dominio de Productos
    private ServicioProductos servicioProductos;
    private ServicioCategorias servicioCategorias;

    public Controller() {
        // Factoría cargando las implementaciones del microservicio actual
        this.servicioProductos = FactoriaServicios.getServicio(ServicioProductos.class); //
        this.servicioCategorias = FactoriaServicios.getServicio(ServicioCategorias.class);
    }

    

    // ========== CASOS DE USO DE PRODUCTOS ==========

    /**
     * CU3: Dar de alta un producto para la venta
     * @param titulo Título del producto
     * @param descripcion Descripción del producto
     * @param precio Precio del producto
     * @param estado Estado del producto
     * @param categoriaId ID de la categoría
     * @param envioDisponible Si está disponible el envío
     * @param vendedorId ID del usuario vendedor (Ahora es obligatorio pasarlo)
     * @return ID del producto creado, o null si hay algún error
     */
    public String darAltaProducto(String titulo, String descripcion, 
                                 BigDecimal precio, EstadoProducto estado, 
                                 String categoriaId, boolean envioDisponible, 
                                 String vendedorId) {
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                logger.warn("Intento de alta de producto con título vacío");
                return null;
            }
            if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Intento de alta de producto con precio inválido: " + precio);
                return null;
            }
            if (estado == null) {
                logger.warn("Intento de alta de producto sin estado");
                return null;
            }
            if (categoriaId == null || categoriaId.trim().isEmpty()) {
                logger.warn("Intento de alta de producto sin categoría");
                return null;
            }
            if (vendedorId == null || vendedorId.trim().isEmpty()) {
                logger.warn("Intento de alta de producto sin vendedor");
                return null;
            }

            return servicioProductos.altaProducto(titulo, descripcion, precio, estado, 
                                                 categoriaId, envioDisponible, vendedorId);
        } catch (ServicioException e) {
            logger.error("Error al dar de alta el producto: " + titulo, e);
            return null;
        }
    }

    /**
     * CU4: Modificar producto a la venta (precio y/o descripción)
     * <br>
     * <b>Actualizado:</b> Ahora requiere el ID del usuario solicitante para validación de seguridad.
     * * @param productoId ID del producto a modificar
     * @param nuevoPrecio Nuevo precio (null si no se modifica)
     * @param nuevaDescripcion Nueva descripción (null si no se modifica)
     * @param usuarioId ID del usuario que solicita el cambio (para verificar propiedad)
     * @return true si la modificación fue exitosa, false en caso contrario
     */
    public boolean modificarProducto(String productoId, String nuevaDescripcion, BigDecimal nuevoPrecio, 
                                    String usuarioId) {
        try {
            if (productoId == null || productoId.trim().isEmpty()) {
                logger.warn("Intento de modificación de producto con ID vacío");
                return false;
            }
            if (usuarioId == null || usuarioId.trim().isEmpty()) {
                logger.warn("Intento de modificación sin ID de usuario solicitante");
                return false;
            }
            
            if (nuevoPrecio == null && nuevaDescripcion == null) {
                logger.warn("Intento de modificación de producto sin especificar cambios");
                return false;
            }
            
            if (nuevoPrecio != null && precioInvalido(nuevoPrecio)) {
                logger.warn("Intento de modificación de producto con precio inválido: " + nuevoPrecio);
                return false;
            }

            // Llamada al método seguro del servicio
            servicioProductos.modificarProducto(productoId, nuevaDescripcion, nuevoPrecio, usuarioId); //
            return true;
        } catch (ServicioException e) {
            logger.error("Error al modificar el producto con ID: " + productoId + " - " + e.getMessage());
            return false;
        }
    }
    
    private boolean precioInvalido(BigDecimal precio) {
        return precio.compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * CU5: Asociar lugar de recogida a un producto
     */
    public boolean asociarLugarRecogida(String productoId, String descripcion, 
                                       Double longitud, Double latitud) {
        try {
            if (productoId == null || productoId.trim().isEmpty()) { return false; }
            if (descripcion == null || descripcion.trim().isEmpty()) { return false; }
            if (longitud == null || latitud == null) { return false; }
            if (longitud < -180 || longitud > 180 || latitud < -90 || latitud > 90) { return false; }

            servicioProductos.asignarLugarRecogida(productoId, descripcion, longitud, latitud);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al asociar lugar de recogida al producto ID: " + productoId, e);
            return false;
        }
    }

    /**
     * CU6: Obtener resumen mensual de productos en venta
     */
    public List<ResumenProducto> obtenerResumenMensual(int mes, int anio) {
        try {
            if (mes < 1 || mes > 12 || anio < 1900 || anio > 2100) {
                return Collections.emptyList();
            }
            return servicioProductos.historialMes(mes, anio);
        } catch (ServicioException e) {
            logger.error("Error al obtener resumen mensual", e);
            return Collections.emptyList();
        }
    }

    /**
     * CU7: Consultar productos a la venta con filtros
     */
    public List<Producto> buscarProductos(String categoriaId, String textoBusqueda, 
                                         EstadoProducto estadoMinimo, 
                                         BigDecimal precioMaximo) {
        try {
            if (precioMaximo != null && precioMaximo.compareTo(BigDecimal.ZERO) < 0) {
                return Collections.emptyList();
            }
            return servicioProductos.buscarProductos(categoriaId, textoBusqueda, 
                                                    estadoMinimo, precioMaximo);
        } catch (ServicioException e) {
            logger.error("Error al buscar productos", e);
            return Collections.emptyList();
        }
    }
    
    // ========== OTROS MÉTODOS DE CATEGORÍAS ==========

    /**
     * CU9: Modificar la descripción de una categoría existente
     */
    public boolean modificarDescripcionCategoria(String categoriaId, String nuevaDescripcion) {
        try {
            if (categoriaId == null || nuevaDescripcion == null) return false;
            servicioCategorias.modificarDescripcion(categoriaId, nuevaDescripcion);
            return true;
        } catch (ServicioException e) {
            logger.error("Error modificando categoría: " + categoriaId, e);
            return false;
        }
    }

    public List<Categoria> obtenerCategoriasRaiz() {
        try {
            return servicioCategorias.getCategoriasRaiz();
        } catch (ServicioException e) {
            logger.error("Error obteniendo raíces", e);
            return Collections.emptyList();
        }
    }

    public List<Categoria> obtenerDescendientesCategoria(String categoriaId) {
        try {
            if (categoriaId == null) return Collections.emptyList();
            return servicioCategorias.getDescendientes(categoriaId);
        } catch (ServicioException e) {
            logger.error("Error obteniendo descendientes", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Busca el ID de una categoría dado su nombre (o parte de él).
     * @return ID de la categoría o null si no se encuentra.
     */
    public String buscarIdCategoria(String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) return null;
            
            Categoria c = servicioCategorias.buscarCategoriaPorNombre(nombre);
            if (c != null) {
                logger.info("Categoría encontrada: {} (ID: {})", c.getNombre(), c.getId());
                return c.getId();
            } else {
                logger.warn("No se encontró ninguna categoría con el nombre: " + nombre);
                return null;
            }
        } catch (ServicioException e) {
            logger.error("Error al buscar ID de categoría", e);
            return null;
        }
    }
    
    // ========== GESTIÓN DE CATEGORÍAS (Carga Masiva e Individual) ==========

    /**
     * Carga automáticamente todas las categorías que existan en la carpeta 'categoriasXML'.
     * @return int número de ficheros cargados correctamente.
     */
    public int cargarTodasLasCategorias() {
        logger.info("Iniciando carga masiva de categorías desde directorio: " + CARPETA_CATEGORIAS);
        
        File directorio = new File(CARPETA_CATEGORIAS);
        
        if (!directorio.exists()) {
            logger.error("ERROR: No existe la carpeta: " + directorio.getAbsolutePath());
            return 0;
       }

       File[] archivosXML = directorio.listFiles(new FilenameFilter() {
           @Override
           public boolean accept(File dir, String name) {
               return name.toLowerCase().endsWith(".xml");
           }
       });

       int cargados = 0;
       int fallidos = 0;
       if (archivosXML != null) {
           
           for (File archivo : archivosXML) {
               boolean exito = cargarCategorias(archivo.getName()); 
               
               if (exito) {
                   cargados++;
               } else {
                   fallidos++;
                   logger.warn("FALLO al cargar: " + archivo.getName());
               }
           }
           logger.info("=== FIN CARGA: " + cargados + " cargados, " + fallidos + " fallidos ===");
       }
       return cargados;
    }

    /**
     * CU8: Cargar nuevas categorías desde archivo XML (administrador)
     * Método adaptado para ser usado tanto individualmente como por la carga masiva.
     * * @param rutaArchivoXML Ruta del archivo XML con la jerarquía de categorías
     * @return true si la carga fue exitosa, false en caso contrario
     */
    public boolean cargarCategorias(String rutaArchivoXML) {
        try {
            if (rutaArchivoXML == null || rutaArchivoXML.trim().isEmpty()) {
                logger.error("La ruta del archivo XML es nula o vacía");
                return false;
            }

            servicioCategorias.cargarJerarquia(rutaArchivoXML); //
            return true;
        } catch (ServicioException e) {
            logger.error("Error al cargar categorías desde XML: " + rutaArchivoXML + ". Causa: " + e.getMessage());
            return false;
        }
    }

    // ========== Visualizaciones ==========
    
    public boolean registrarVisualizacionProducto(String productoId) {
        try {
            if (productoId == null) return false;
            servicioProductos.anadirVisualizacion(productoId);
            return true;
        } catch (ServicioException e) {
            logger.error("Error registrando visualización", e);
            return false;
        }
    }
}