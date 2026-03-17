package SegundUM.Compraventas.servicio.compraventa;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import SegundUM.Compraventas.dominio.Compraventa;
import SegundUM.Compraventas.puertos.PuertoAutenticacion;
import SegundUM.Compraventas.puertos.PuertoEntradaEventos;
import SegundUM.Compraventas.puertos.PuertoSalidaEventos;
import SegundUM.Compraventas.puertos.PuertoProductos;
import SegundUM.Compraventas.puertos.PuertoUsuarios;
import SegundUM.Compraventas.repositorio.compraventa.RepositorioCompraventaMongo;
import SegundUM.Compraventas.rest.dto.ProductoDTO;
import SegundUM.Compraventas.rest.dto.UsuarioDTO;
import SegundUM.Compraventas.servicio.ServicioException;

@Service
public class ServicioCompraventaImpl implements ServicioCompraventa, PuertoEntradaEventos {

	private static final Logger logger = LoggerFactory.getLogger(ServicioCompraventaImpl.class);

	private final RepositorioCompraventaMongo repositorio;
    private final PuertoProductos puertoProductos;
    private final PuertoUsuarios puertoUsuarios;
    private final PuertoAutenticacion puertoAutenticacion;
    private final PuertoSalidaEventos puertoEventos;

    @Autowired
    public ServicioCompraventaImpl(RepositorioCompraventaMongo repositorio,
                                   PuertoProductos puertoProductos,
                                   PuertoUsuarios puertoUsuarios,
                                   PuertoAutenticacion puertoAutenticacion,
                                   PuertoSalidaEventos puertoEventos) {
        this.repositorio = repositorio;
        this.puertoProductos = puertoProductos;
        this.puertoUsuarios = puertoUsuarios;
        this.puertoAutenticacion = puertoAutenticacion;
        this.puertoEventos = puertoEventos;
    }

    @Override
    public Compraventa realizarCompra(String idProducto, String idComprador, String emailComprador, String claveComprador) throws ServicioException {

    	String tokenCrudo = puertoAutenticacion.login(emailComprador, claveComprador);
        String token = "Bearer " + tokenCrudo;

        ProductoDTO producto = puertoProductos.obtenerDatosProducto(idProducto);

        logger.info("Producto Obtenido = {}", producto.toString());

        if (producto.isVendido()) {
            throw new ServicioException("El producto ya ha sido vendido");
        }

        UsuarioDTO comprador = puertoUsuarios.obtenerDatosUsuario(idComprador, token);

        System.out.println(comprador);

        logger.info("Comprador Obtenido = {}", comprador.toString());

        UsuarioDTO vendedor = puertoUsuarios.obtenerDatosUsuario(producto.getIdVendedor(), token);

        Compraventa nuevaCompraventa = new Compraventa(
                null,
                idProducto,
                producto.getTitulo(),
                producto.getRecogida().getDescripcion(),
                producto.getPrecio(),
                producto.getIdVendedor(),
                vendedor.getNombre(),
                idComprador,
                comprador.getNombre(),
                LocalDateTime.now()
        );

        Compraventa guardada = repositorio.save(nuevaCompraventa);

        // Publicar evento de compraventa creada en el bus de eventos
        puertoEventos.publicarCompraventaCreada(
                guardada.getId(),
                idProducto,
                idComprador,
                producto.getIdVendedor()
        );

        return guardada;
    }

    @Override
    public Page<Compraventa> recuperarComprasDeUsuario(String idComprador, Pageable pageable) {
        return repositorio.findByIdComprador(idComprador, pageable);
    }

    @Override
    public Page<Compraventa> recuperarVentasDeUsuario(String idVendedor, Pageable pageable) {
        return repositorio.findByIdVendedor(idVendedor, pageable);
    }

    @Override
    public Page<Compraventa> recuperarCompraventasEntre(String idComprador, String idVendedor, Pageable pageable) {
        return repositorio.findByIdCompradorAndIdVendedor(idComprador, idVendedor, pageable);
    }

    // --- Implementacion PuertoEntradaEventos ---

    @Override
    public void manejarUsuarioModificado(String idUsuario, String nuevoNombre) throws ServicioException {
        logger.info("Evento recibido: usuario-modificado para usuario {}", idUsuario);

        List<Compraventa> comoVendedor = repositorio.findByIdVendedor(idUsuario);
        for (Compraventa c : comoVendedor) {
            c.setNombreVendedor(nuevoNombre);
            repositorio.save(c);
        }

        List<Compraventa> comoComprador = repositorio.findByIdComprador(idUsuario);
        for (Compraventa c : comoComprador) {
            c.setNombreComprador(nuevoNombre);
            repositorio.save(c);
        }

        logger.info("Nombre actualizado en {} compraventas como vendedor y {} como comprador",
                comoVendedor.size(), comoComprador.size());
    }

    @Override
    public void manejarUsuarioEliminado(String idUsuario) throws ServicioException {
        logger.info("Evento recibido: usuario-eliminado para usuario {}", idUsuario);

        List<Compraventa> comoVendedor = repositorio.findByIdVendedor(idUsuario);
        for (Compraventa c : comoVendedor) {
            c.setIdVendedor(null);
            c.setNombreVendedor(null);
            repositorio.save(c);
        }

        List<Compraventa> comoComprador = repositorio.findByIdComprador(idUsuario);
        for (Compraventa c : comoComprador) {
            c.setIdComprador(null);
            c.setNombreComprador(null);
            repositorio.save(c);
        }

        logger.info("Datos de usuario eliminado en {} compraventas como vendedor y {} como comprador",
                comoVendedor.size(), comoComprador.size());
    }

    @Override
    public void manejarProductoModificado(String idProducto, String nuevoTitulo) throws ServicioException {
        logger.info("Evento recibido: producto-modificado para producto {}", idProducto);

        List<Compraventa> compraventas = repositorio.findByIdProducto(idProducto);
        for (Compraventa c : compraventas) {
            c.setTitulo(nuevoTitulo);
            repositorio.save(c);
        }

        logger.info("Titulo actualizado en {} compraventas", compraventas.size());
    }

    @Override
    public void manejarProductoEliminado(String idProducto) throws ServicioException {
        logger.info("Evento recibido: producto-eliminado para producto {}", idProducto);

        List<Compraventa> compraventas = repositorio.findByIdProducto(idProducto);

        if (compraventas.isEmpty()) {
            logger.info("Producto {} no tiene compraventas asociadas, evento ignorado", idProducto);
            return;
        }

        for (Compraventa c : compraventas) {
            c.setIdProducto(null);
            c.setTitulo(null);
            c.setRecogida(null);
            repositorio.save(c);
        }

        logger.info("Datos de producto eliminado en {} compraventas", compraventas.size());
    }
}
