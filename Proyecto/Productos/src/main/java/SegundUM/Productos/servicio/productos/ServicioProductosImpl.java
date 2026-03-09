package SegundUM.Productos.servicio.productos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SegundUM.Productos.dominio.Categoria;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.LugarRecogida;
import SegundUM.Productos.dominio.Producto;
import SegundUM.Productos.dominio.ResumenProducto;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.categorias.RepositorioCategoriasJPA;
import SegundUM.Productos.repositorio.productos.RepositorioProductosJPA;
import SegundUM.Productos.servicio.ServicioException;

/**
 * Implementación del servicio de productos.
 */

@Service
@Transactional
public class ServicioProductosImpl implements ServicioProductos {

	private final Logger logger = LoggerFactory.getLogger(ServicioProductosImpl.class);

    private final RepositorioProductosJPA repositorioProductos;
    private final RepositorioCategoriasJPA repositorioCategorias;

    @Autowired
    public ServicioProductosImpl(RepositorioCategoriasJPA repositorioCategorias, RepositorioProductosJPA repositorioProductos) {
        this.repositorioProductos = repositorioProductos;
        this.repositorioCategorias = repositorioCategorias;
    }

    /**
     * Alta de producto. Devuelve id generado.
     */
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

    /**
     * Asigna lugar de recogida al producto.
     */
    @Override
    public void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new ServicioException("El producto con ID " + productoId + " no existe en el sistema"));
    	LugarRecogida lugar = new LugarRecogida(descripcion, longitud, latitud);
    	p.setRecogida(lugar);
    	repositorioProductos.save(p);
    }

    /**
     * Modifica precio y/o descripción del producto. Parámetros nulos no se modifican.
     */
    @Override
    @Deprecated(since = "No lo se, borrarlo no deberia rromper nada, pero ya lo borrare", forRemoval = true)
    public void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new ServicioException("El producto con ID " + productoId + " no existe en el sistema"));
    	if (nuevoPrecio != null) p.setPrecio(nuevoPrecio);
    	if (nuevaDescripcion != null) p.setDescripcion(nuevaDescripcion);
    	repositorioProductos.save(p);
    }

    /**
     * Incrementa en 1 el contador de visualizaciones.
     */
    @Override
    public void anadirVisualizacion(String productoId) throws ServicioException {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new ServicioException("El producto con ID " + productoId + " no existe en el sistema"));
    	p.incrementarVisualizaciones();
    	repositorioProductos.save(p);
    }

    /**
     * Modifica precio y/o descripción de un producto.
     * Verifica que el usuario solicitante sea el propietario.
     */
    @Override
    public void modificarProducto(String idProducto, String nuevaDescripcion, BigDecimal nuevoPrecio, String idUsuarioSolicitante) throws ServicioException {
		
		Producto p = repositorioProductos.findById(idProducto)
				.orElseThrow(() -> new ServicioException("El producto con ID " + idProducto + " no existe en el sistema"));

		if (!p.getVendedorId().equals(idUsuarioSolicitante)) {
			logger.warn("Intento de modificación no autorizada por usuario: " + idUsuarioSolicitante);
			throw new ServicioException("No tienes permiso para editar este producto.");
		}

		if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty()) {
			p.setDescripcion(nuevaDescripcion);
		}

		if (nuevoPrecio != null) {
			p.setPrecio(nuevoPrecio);
		}

		repositorioProductos.save(p);
    }
    
    /**
     * Historial del mes de un vendedor: devuelve resumen ordenado por visualizaciones (desc).
     */
    @Deprecated
    @Override
    public List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException {

    	String vendedorId = null;
    	if (emailVendedor != null) {
    		logger.info("USANDO VALOR DE PRUEBAS PARA ID DEL VENDEDOR, SE DEBE REEMPLAZAR POR LA CONSULTA A LA API DE USUARIOS");
            // TODO: vendedorId = clienteHttpUsuarios.obtenerIdPorEmail(emailVendedor);

            vendedorId = "ID-TEMPORAL-PARA-PRUEBAS"; // TODO tempooral para pruebas sin la api
       }
    	return repositorioProductos.getHistorialMes(mes, anio, vendedorId).stream().map(ResumenProducto::fromEntity).toList();
    }
    
    @Override
    public Page<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor, Pageable pageable) throws ServicioException {

    	String vendedorId = null;
    	if (emailVendedor != null) {
    		logger.info("USANDO VALOR DE PRUEBAS PARA ID DEL VENDEDOR, SE DEBE REEMPLAZAR POR LA CONSULTA A LA API DE USUARIOS");
            // TODO: vendedorId = clienteHttpUsuarios.obtenerIdPorEmail(emailVendedor);

            vendedorId = "ID-TEMPORAL-PARA-PRUEBAS"; // TODO tempooral para pruebas sin la api
       }
    	return repositorioProductos.getHistorialMes(mes, anio, vendedorId, pageable).map(ResumenProducto::fromEntity);
    }
    
    /**
     * Historial del mes de: devuelve resumen ordenado por visualizaciones (desc).
     */
    @Deprecated
    @Override
	public List<ResumenProducto> historialMes(int mes, int anio) throws ServicioException {

		return repositorioProductos.getHistorialMes(mes, anio, null).stream().map(ResumenProducto::fromEntity).toList();
	}
    
    @Override
	public Page<ResumenProducto> historialMes(int mes, int anio, Pageable pageable) throws ServicioException {

		return repositorioProductos.getHistorialMes(mes, anio, null, pageable).map(ResumenProducto::fromEntity);
	}
    
    /**
     * Buscar productos con los criterios opcionales.
     */
    @Override
    @Deprecated
    public List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException {

    	logger.debug("Buscando productos con filtros - Categoría ID: " + categoriaId + ", Texto: " + texto + ", Estado mínimo: " + estadoMinimo + ", Precio máximo: " + precioMaximo);
        return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo);
    }
    
    @Override
    public Page<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo, Pageable pageable) throws ServicioException {
        logger.debug("Buscando productos (Paginado) - Cat: " + categoriaId + ", Txt: " + texto + ", Pág: " + pageable.getPageNumber());
        
        return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo, pageable);
    }

    /**
     * Recupera los productos publicados por un vendedor específico.
     */
    @Deprecated
	@Override
    public List<Producto> getProductosPorVendedor(String vendedorId) throws ServicioException {

        return repositorioProductos.getByVendedorConCategoria(vendedorId);
    }
	
	@Override
    public Page<Producto> getProductosPorVendedor(String vendedorId, Pageable pageable) throws ServicioException {

        return repositorioProductos.getByVendedorConCategoria(vendedorId, pageable);
    }
	
	/** 
     * Método para obtener un producto por su id
	 */
    @Override
    public Producto getProductoPorId(String productoId) throws ServicioException, EntidadNoEncontrada {
    	
    	return repositorioProductos.findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("El producto con ID " + productoId + " no existe."));
    }   

    /**
     * Método para eliminar un producto por su id
     */
    @Override
    public void eliminarProducto(String productoId) throws ServicioException, EntidadNoEncontrada {
    	
    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new EntidadNoEncontrada("El producto con ID " + productoId + " no existe."));
		repositorioProductos.delete(p);
    }
	
}