package SegundUM.Compraventas.servicio.compraventa;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import SegundUM.Compraventas.dominio.Compraventa;
import SegundUM.Compraventas.puertos.PuertoAutenticacion;
import SegundUM.Compraventas.puertos.PuertoProductos;
import SegundUM.Compraventas.puertos.PuertoUsuarios;
import SegundUM.Compraventas.repositorio.compraventa.RepositorioCompraventaMongo;
import SegundUM.Compraventas.rest.dto.ProductoDTO;
import SegundUM.Compraventas.rest.dto.UsuarioDTO;

@Service
public class ServicioCompraventaImpl implements ServicioCompraventa {
	
	private static final Logger logger = LoggerFactory.getLogger(ServicioCompraventaImpl.class);
	
	private final RepositorioCompraventaMongo repositorio;
    private final PuertoProductos puertoProductos;
    private final PuertoUsuarios puertoUsuarios;
    private final PuertoAutenticacion puertoAutenticacion;

    @Autowired
    public ServicioCompraventaImpl(RepositorioCompraventaMongo repositorio, 
                                   PuertoProductos puertoProductos, 
                                   PuertoUsuarios puertoUsuarios,
                                   PuertoAutenticacion puertoAutenticacion) {
        this.repositorio = repositorio;
        this.puertoProductos = puertoProductos;
        this.puertoUsuarios = puertoUsuarios;
        this.puertoAutenticacion = puertoAutenticacion;
    }

    @Override
    public Compraventa realizarCompra(String idProducto, String idComprador, String emailComprador, String claveComprador) {
    	
    	String tokenCrudo = puertoAutenticacion.login(emailComprador, claveComprador);
        String token = "Bearer " + tokenCrudo;
        
        ProductoDTO producto = puertoProductos.obtenerDatosProducto(idProducto);
        
        logger.info("Producto Obtenido = {}", producto.toString());

        UsuarioDTO comprador = puertoUsuarios.obtenerDatosUsuario(idComprador, token);
        
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

        return repositorio.save(nuevaCompraventa);
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
    
}
