package SegundUM.Compraventas.rest.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.hateoas.RepresentationModel;
import SegundUM.Compraventas.dominio.Compraventa;

/**
 * Este DTO es para los GET
 **/
public class CompraventaDTO extends RepresentationModel<CompraventaDTO> implements Serializable {
    
    private String id;
    private String idProducto;
    private String titulo;
    private String recogida;
    private int precio;
    private String idVendedor;
    private String nombreVendedor;
    private String idComprador;
    private String nombreComprador;
    private LocalDateTime fechaYHora;

    public CompraventaDTO() {}

    public static CompraventaDTO fromEntity(Compraventa c) {
        if (c == null) return null;
        
        CompraventaDTO dto = new CompraventaDTO();
        dto.id = c.getId();
        dto.idProducto = c.getIdProducto();
        dto.titulo = c.getTitulo();
        dto.recogida = c.getRecogida();
        dto.precio = c.getPrecio();
        dto.idVendedor = c.getIdVendedor();
        dto.nombreVendedor = c.getNombreVendedor();
        dto.idComprador = c.getIdComprador();
        dto.nombreComprador = c.getNombreComprador();
        dto.fechaYHora = c.getFechaYHora();
        return dto;
    }

    // Getters
    public String getId() { return id; }
    public String getIdProducto() { return idProducto; }
    public String getTitulo() { return titulo; }
    public String getRecogida() { return recogida; }
    public int getPrecio() { return precio; }
    public String getIdVendedor() { return idVendedor; }
    public String getNombreVendedor() { return nombreVendedor; }
    public String getIdComprador() { return idComprador; }
    public String getNombreComprador() { return nombreComprador; }
    public LocalDateTime getFechaYHora() { return fechaYHora; }
}