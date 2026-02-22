package SegundUM.Productos.servicio.productos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.LugarRecogida;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.RepositorioException;
import SegundUM.Productos.repositorio.categorias.RepositorioCategorias;
import SegundUM.Productos.repositorio.productos.RepositorioProductos;
import SegundUM.Productos.servicio.ServicioException;

/**
 * Implementación del servicio de productos.
 */

@Service
@Transactional
public class ServicioProductosImpl implements ServicioProductos {

	private final Logger logger = LoggerFactory.getLogger(ServicioProductosImpl.class);
	
    private final RepositorioProductos repositorioProductos;
    private final RepositorioCategorias repositorioCategorias;

    @Autowired
    public ServicioProductosImpl(RepositorioCategorias repositorioCategorias, RepositorioProductos repositorioProductos) {
        this.repositorioProductos = repositorioProductos;
        this.repositorioCategorias = repositorioCategorias;
    }

    @Override
    public String altaProducto(String titulo, String descripcion, BigDecimal precio, EstadoProducto estado,
    		String categoriaId, boolean envioDisponible, String vendedorId) throws ServicioException {
    	
    	// VERIFICACIÓN: Obtener categoría y verificar que existe
    	Categoria categoria;
    	categoria = repositorioCategorias.findById(categoriaId)
    			.orElseThrow(() -> new ServicioException("Categoría con ID " + categoriaId + " no encontrada"));

    	String id = UUID.randomUUID().toString();

    	Producto p = new Producto(id, titulo, descripcion, precio, estado, categoria, envioDisponible, vendedorId);

    	repositorioProductos.save(p);
    	return id;
    }

    @Override
    public void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new ServicioException("El producto con ID " + productoId + " no existe en el sistema"));
    	LugarRecogida lugar = new LugarRecogida(descripcion, longitud, latitud);
    	p.setRecogida(lugar);
    	repositorioProductos.save(p);
    }

    @Override
    public void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new ServicioException("El producto con ID " + productoId + " no existe en el sistema"));
    	if (nuevoPrecio != null) p.setPrecio(nuevoPrecio);
    	if (nuevaDescripcion != null) p.setDescripcion(nuevaDescripcion);
    	repositorioProductos.save(p);
    }

    @Override
    public void anadirVisualizacion(String productoId) throws ServicioException {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new ServicioException("El producto con ID " + productoId + " no existe en el sistema"));
    	p.incrementarVisualizaciones();
    	repositorioProductos.save(p);
    }

    @Override
    public List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException {
    	
    	String vendedorId = null;
    	if (emailVendedor != null) {
    		logger.info("USANDO VALOR DE PRUEBAS PARA ID DEL VENDEDOR, SE DEBE REEMPLAZAR POR LA CONSULTA A LA API DE USUARIOS");
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
            return repositorioProductos.getByVendedorConCategoria(vendedorId);
        } catch (RepositorioException e) {
            // logger.error("Error al recuperar productos del vendedor: " + vendedorId, e);
            throw new ServicioException("Error al obtener los productos del vendedor", e);
        }
    }
	
	@Override
    public void modificarProducto(String idProducto, String nuevaDescripcion, BigDecimal nuevoPrecio, String idUsuarioSolicitante) throws ServicioException {
		
		// 1. Recuperamos el producto
		Producto p = repositorioProductos.findById(idProducto)
				.orElseThrow(() -> new ServicioException("El producto con ID " + idProducto + " no existe en el sistema"));

		// 2. VERIFICACIÓN DE SEGURIDAD - verificar que el usuario solicitante es el vendedor del producto
		if (!p.getVendedorId().equals(idUsuarioSolicitante)) {
			logger.warn("Intento de modificación no autorizada por usuario: " + idUsuarioSolicitante);
			throw new ServicioException("No tienes permiso para editar este producto.");
		}

		// 3. Actualizar solo los campos permitidos
		if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty()) {
			p.setDescripcion(nuevaDescripcion);
		}

		if (nuevoPrecio != null) {
			p.setPrecio(nuevoPrecio);
		}

		// 4. Persistir los cambios
		repositorioProductos.save(p);
    }

    @Override
    public Producto getProductoPorId(String productoId) throws ServicioException, EntidadNoEncontrada {
    	
    	return repositorioProductos.findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("El producto con ID " + productoId + " no existe."));
    }   

    @Override
    public void eliminarProducto(String productoId) throws ServicioException, EntidadNoEncontrada {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new EntidadNoEncontrada("El producto con ID " + productoId + " no existe."));
		repositorioProductos.delete(p);
    }
	
}