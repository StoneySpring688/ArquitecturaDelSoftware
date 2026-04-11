package SegundUM.Productos.servicio.productos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.LugarRecogida;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.servicio.ServicioException;


/**
 * Operaciones de negocio sobre productos.
 */
public interface ServicioProductos {

    /**
     * Alta de producto. Devuelve id generado.
     */
    String altaProducto(String titulo, String descripcion, BigDecimal precio,
                        EstadoProducto estado, String categoriaId, boolean envioDisponible,
                        String vendedorId, LugarRecogida lugarRecogida) throws ServicioException;

    /**
     * Asigna lugar de recogida al producto.
     */
    void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException;

    /**
     * Modifica precio y/o descripción del producto. Parámetros nulos no se modifican.
     */
    void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException;

    /**
     * Incrementa en 1 el contador de visualizaciones.
     */
    void anadirVisualizacion(String productoId) throws ServicioException;
    
    /**
     * Modifica precio y/o descripción de un producto.
     * Verifica que el usuario solicitante sea el propietario.
     */
    void modificarProducto(String idProducto, String nuevaDescripcion, BigDecimal nuevoPrecio, String idUsuarioSolicitante) throws ServicioException;

    /**
     * Historial del mes de un vendedor: devuelve resumen ordenado por visualizaciones (desc).
     */
    // TODO quitar este apaño para devolver Producto (quitar dto antiguo)
    @Deprecated
    List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException;
    Page<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor, Pageable pageable) throws ServicioException;
    
    /**
     * Historial del mes de: devuelve resumen ordenado por visualizaciones (desc).
     */
    // TODO quitar este apaño para devolver Producto (quitar dto antiguo)
    @Deprecated
    List<ResumenProducto> historialMes(int mes, int anio) throws ServicioException;
    Page<ResumenProducto> historialMes(int mes, int anio, Pageable pageable) throws ServicioException;

    /**
     * Buscar productos con los criterios opcionales.
     */
    @Deprecated
    List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException;
    Page<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo, Pageable pageable) throws ServicioException;
    
    /**
     * Recupera los productos publicados por un vendedor específico.
     */
    List<Producto> getProductosPorVendedor(String vendedorId) throws ServicioException;
    Page<Producto> getProductosPorVendedor(String vendedorId, Pageable pageable) throws ServicioException;

    /** 
     * Método para obtener un producto por su id
     */
    Producto getProductoPorId(String productoId) throws ServicioException, EntidadNoEncontrada;

    /**
     * Método para eliminar un producto por su id
     */
    void eliminarProducto(String productoId) throws ServicioException, EntidadNoEncontrada;
    
}