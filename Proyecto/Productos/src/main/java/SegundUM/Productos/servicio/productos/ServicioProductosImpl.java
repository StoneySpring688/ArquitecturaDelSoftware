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
import SegundUM.Productos.puertos.PuertaEntradaEventos;
import SegundUM.Productos.puertos.PuertoSalidaEventos;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.repositorio.categorias.RepositorioCategoriasJPA;
import SegundUM.Productos.repositorio.productos.RepositorioProductosJPA;
import SegundUM.Productos.servicio.ServicioException;

/**
 * Implementacion del servicio de productos.
 */

@Service
@Transactional
public class ServicioProductosImpl implements ServicioProductos, PuertaEntradaEventos {

	private final Logger logger = LoggerFactory.getLogger(ServicioProductosImpl.class);

    private final RepositorioProductosJPA repositorioProductos;
    private final RepositorioCategoriasJPA repositorioCategorias;
    private final PuertoSalidaEventos puertoSalidaEventos;

    @Autowired
    public ServicioProductosImpl(RepositorioCategoriasJPA repositorioCategorias,
                                 RepositorioProductosJPA repositorioProductos,
                                 PuertoSalidaEventos puertoSalidaEventos) {
        this.repositorioProductos = repositorioProductos;
        this.repositorioCategorias = repositorioCategorias;
        this.puertoSalidaEventos = puertoSalidaEventos;
    }

    /**
     * Alta de producto. Devuelve id generado.
     */
    @Override
    public String altaProducto(String titulo, String descripcion, BigDecimal precio, EstadoProducto estado,
    		String categoriaId, boolean envioDisponible, String vendedorId) throws ServicioException {

    	// VERIFICACION: Obtener categoria y verificar que existe
    	Categoria categoria;
    	categoria = repositorioCategorias.findById(categoriaId)
    			.orElseThrow(() -> new ServicioException("Categoria con ID " + categoriaId + " no encontrada"));

    	String id = UUID.randomUUID().toString();

    	Producto p = new Producto(id, titulo, descripcion, precio, estado, categoria, envioDisponible, vendedorId);

    	repositorioProductos.save(p);

    	// Publicar evento producto-creado
    	puertoSalidaEventos.publicarProductoCreado(id, titulo, vendedorId);

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
     * Modifica precio y/o descripcion del producto. Parametros nulos no se modifican.
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
     * Modifica precio y/o descripcion de un producto.
     * Verifica que el usuario solicitante sea el propietario.
     */
    @Override
    public void modificarProducto(String idProducto, String nuevaDescripcion, BigDecimal nuevoPrecio, String idUsuarioSolicitante) throws ServicioException {

		Producto p = repositorioProductos.findById(idProducto)
				.orElseThrow(() -> new ServicioException("El producto con ID " + idProducto + " no existe en el sistema"));

		if (!p.getVendedorId().equals(idUsuarioSolicitante)) {
			logger.warn("Intento de modificacion no autorizada por usuario: " + idUsuarioSolicitante);
			throw new ServicioException("No tienes permiso para editar este producto.");
		}

		String tituloAnterior = p.getTitulo();

		if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty()) {
			p.setDescripcion(nuevaDescripcion);
		}

		if (nuevoPrecio != null) {
			p.setPrecio(nuevoPrecio);
		}

		repositorioProductos.save(p);

		// Si el titulo hubiera cambiado se publicaria evento (por ahora el titulo no se modifica en este metodo)
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
            vendedorId = "ID-TEMPORAL-PARA-PRUEBAS";
       }
    	return repositorioProductos.getHistorialMes(mes, anio, vendedorId).stream().map(ResumenProducto::fromEntity).toList();
    }

    @Override
    public Page<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor, Pageable pageable) throws ServicioException {

    	String vendedorId = null;
    	if (emailVendedor == null) {
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

    	logger.debug("Buscando productos con filtros - Categoria ID: " + categoriaId + ", Texto: " + texto + ", Estado minimo: " + estadoMinimo + ", Precio maximo: " + precioMaximo);
        return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo);
    }

    @Override
    public Page<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo, Pageable pageable) throws ServicioException {
        logger.debug("Buscando productos (Paginado) - Cat: " + categoriaId + ", Txt: " + texto + ", Pag: " + pageable.getPageNumber());

        return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo, pageable);
    }

    /**
     * Recupera los productos publicados por un vendedor especifico.
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
     * Metodo para obtener un producto por su id
	 */
    @Override
    public Producto getProductoPorId(String productoId) throws ServicioException, EntidadNoEncontrada {

    	return repositorioProductos.findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("El producto con ID " + productoId + " no existe."));
    }

    /**
     * Metodo para eliminar un producto por su id
     */
    @Override
    public void eliminarProducto(String productoId) throws ServicioException, EntidadNoEncontrada {

    	Producto p = repositorioProductos.findById(productoId)
    			.orElseThrow(() -> new EntidadNoEncontrada("El producto con ID " + productoId + " no existe."));

    	String vendedorId = p.getVendedorId();
		repositorioProductos.delete(p);

		// Publicar evento producto-eliminado
		puertoSalidaEventos.publicarProductoEliminado(productoId, vendedorId);
    }

    // --- Implementacion PuertaEntradaEventos ---

    /**
     * Maneja el evento de compraventa creada: marca el producto como vendido.
     */
    @Override
    public void manejarCompraventaCreada(String idProducto) throws ServicioException {
        logger.info("Evento recibido: marcando producto {} como vendido", idProducto);

        Producto p = repositorioProductos.findById(idProducto)
                .orElseThrow(() -> new ServicioException("El producto con ID " + idProducto + " no existe en el sistema"));

        p.setVendido(true);
        repositorioProductos.save(p);

        logger.info("Producto {} marcado como vendido correctamente", idProducto);
    }

    /**
     * Maneja el evento de usuario eliminado: elimina todos los productos del usuario.
     */
    @Override
    public void manejarUsuarioEliminado(String idUsuario) throws ServicioException {
        logger.info("Evento recibido: eliminando productos del usuario {}", idUsuario);

        List<Producto> productos = repositorioProductos.getByVendedorConCategoria(idUsuario);
        for (Producto p : productos) {
            repositorioProductos.delete(p);
            // Publicar evento producto-eliminado por cada producto eliminado
            puertoSalidaEventos.publicarProductoEliminado(p.getId(), idUsuario);
        }

        logger.info("Eliminados {} productos del usuario {}", productos.size(), idUsuario);
    }
}
