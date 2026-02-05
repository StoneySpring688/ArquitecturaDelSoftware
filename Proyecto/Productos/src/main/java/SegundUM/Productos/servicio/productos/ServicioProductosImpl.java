package SegundUM.Productos.servicio.productos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.LugarRecogida;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.FactoriaRepositorios;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.categorias.RepositorioCategorias;
import SegundUM.Productos.repositorio.productos.RepositorioProductos;
import SegundUM.Productos.servicio.ServicioException;

/**
 * Implementación del servicio de productos.
 */
public class ServicioProductosImpl implements ServicioProductos {

	private final Logger logger = LoggerFactory.getLogger(ServicioProductosImpl.class);
	
    private final RepositorioProductos repositorioProductos;
    private final RepositorioCategorias repositorioCategorias;

    public ServicioProductosImpl() {
        this.repositorioProductos = FactoriaRepositorios.getRepositorio(Producto.class);
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
    }

    @Override
    public String altaProducto(String titulo, String descripcion, BigDecimal precio, EstadoProducto estado,
                               String categoriaId, boolean envioDisponible, String vendedorId) throws ServicioException {
        try {
            // VERIFICACIÓN: Obtener categoría y verificar que existe
            Categoria categoria;
            try {
            	logger.info("Obteniendo categoría con ID: " + categoriaId);
                categoria = repositorioCategorias.getById(categoriaId);
            } catch (EntidadNoEncontrada e) {
            	logger.error("Categoría con ID " + categoriaId + " no encontrada", e);
                throw new ServicioException("La categoría con ID " + categoriaId + " no existe en el sistema", e);
            }

            String id = UUID.randomUUID().toString();

            Producto p = new Producto(id, titulo, descripcion, precio, estado, categoria, envioDisponible, vendedorId);

            return repositorioProductos.add(p);
        } catch (RepositorioException e) {
        	logger.error("Error al dar de alta el producto", e);
            throw new ServicioException("Error al dar de alta el producto", e);
        }
    }

    @Override
    public void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException {
        try {
            Producto p = repositorioProductos.getById(productoId);
            LugarRecogida lugar = new LugarRecogida(descripcion, longitud, latitud);
            p.setRecogida(lugar);
            repositorioProductos.update(p);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: El producto no existe
        	logger.error("Producto con ID " + productoId + " no encontrado", e);
            throw new ServicioException("El producto con ID " + productoId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al asignar lugar de recogida al producto " + productoId, e);
            throw new ServicioException("Error al asignar lugar de recogida al producto " + productoId, e);
        }
    }

    @Override
    public void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException {
        try {
            Producto p = repositorioProductos.getById(productoId);
            if (nuevoPrecio != null) p.setPrecio(nuevoPrecio);
            if (nuevaDescripcion != null) p.setDescripcion(nuevaDescripcion);
            repositorioProductos.update(p);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: el producto no existe
        	logger.error("Producto con ID " + productoId + " no encontrado", e);
            throw new ServicioException("El producto con ID " + productoId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al modificar el producto " + productoId, e);
            throw new ServicioException("Error al modificar producto " + productoId, e);
        }
    }

    @Override
    public void anadirVisualizacion(String productoId) throws ServicioException {
        try {
            Producto p = repositorioProductos.getById(productoId);
            p.incrementarVisualizaciones();
            repositorioProductos.update(p);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: el producto no existe
        	logger.error("Producto con ID " + productoId + " no encontrado", e);
            throw new ServicioException("El producto con ID " + productoId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al añadir visualización al producto " + productoId, e);
            throw new ServicioException("Error al añadir visualización al producto " + productoId, e);
        }
    }

    @Override
    public List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException {
    	String vendedorId = null;
    	if (emailVendedor != null) {
            // TODO: vendedorId = clienteHttpUsuarios.obtenerIdPorEmail(emailVendedor);
            
            vendedorId = "ID-TEMPORAL-PARA-PRUEBAS"; // TODO tempooral para pruebas sin la api
       }
    	try {
            return repositorioProductos.getHistorialMes(mes, anio, vendedorId);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al obtener historial del mes", e);
        }
    }

    @Override
    public List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException {
        try {
        	logger.info("Buscando productos con filtros - Categoría ID: " + categoriaId + ", Texto: " + texto + ", Estado mínimo: " + estadoMinimo + ", Precio máximo: " + precioMaximo);
            return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo);
        } catch (RepositorioException e) {
        	logger.error("Error buscando productos con los filtros proporcionados", e);
            throw new ServicioException("Error buscando productos", e);
        }
    }

	@Override
	public List<ResumenProducto> historialMes(int mes, int anio) throws ServicioException {
		try {
            return repositorioProductos.getHistorialMes(mes, anio);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al obtener historial del mes", e);
        }
	}
	
	@Override
    public List<Producto> getProductosPorVendedor(String vendedorId) throws ServicioException {
        try {
            return repositorioProductos.getByVendedor(vendedorId);
        } catch (RepositorioException e) {
            // logger.error("Error al recuperar productos del vendedor: " + vendedorId, e);
            throw new ServicioException("Error al obtener los productos del vendedor", e);
        }
    }
	
	@Override
    public void modificarProducto(String idProducto, String nuevaDescripcion, BigDecimal nuevoPrecio, String idUsuarioSolicitante) throws ServicioException {
        try {
            // 1. Recuperamos el producto
            Producto p = repositorioProductos.getById(idProducto);

            // 2. VERIFICACIÓN DE SEGURIDAD: ¿Es el dueño?
            if (!p.getVendedorId().equals(idUsuarioSolicitante)) {
                // logger.warn("Intento de modificación no autorizada por usuario: " + idUsuarioSolicitante);
                throw new ServicioException("No tienes permiso para editar este producto.");
            }

            // 3. Actualizamos solo los campos permitidos
            if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty()) {
                p.setDescripcion(nuevaDescripcion);
            }
            
            if (nuevoPrecio != null) {
                // Podrías añadir validación de precio > 0 aquí
                p.setPrecio(nuevoPrecio);
            }

            // 4. Persistimos los cambios
            repositorioProductos.update(p); // Asumimos que RepositorioJPA tiene update

        } catch (EntidadNoEncontrada e) {
            throw new ServicioException("El producto no existe.", e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al modificar el producto.", e);
        }
    }
	
}