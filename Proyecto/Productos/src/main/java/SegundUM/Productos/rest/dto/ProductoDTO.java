package SegundUM.Productos.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;

public class ProductoDTO implements Serializable {
    
    private static final long serialVersionUID = -7064953061294088595L;
	public String id;
    public String titulo;
    public String descripcion;
    public BigDecimal precio;
    public EstadoProducto estado;
    public LocalDateTime fechaPublicacion;
    public Integer visualizaciones;
    public boolean envioDisponible;
    public String vendedorId;
    
    // Solo dejo lo esencial de la categoría para quitar cosas de en medio (no hace falta más)
    public String categoriaId;
    public String categoriaNombre;
    
    public LugarRecogidaDTO recogida;

    public ProductoDTO() {}

    public static ProductoDTO fromEntity(Producto p) {
        if (p == null) return null;

        ProductoDTO dto = new ProductoDTO();
        dto.id = p.getId();
        dto.titulo = p.getTitulo();
        dto.descripcion = p.getDescripcion();
        dto.precio = p.getPrecio();
        dto.estado = p.getEstado();
        dto.fechaPublicacion = p.getFechaPublicacion();
        dto.visualizaciones = p.getVisualizaciones();
        dto.envioDisponible = p.isEnvioDisponible();
        dto.vendedorId = p.getVendedorId();

        if (p.getCategoria() != null) {
            dto.categoriaId = p.getCategoria().getId();
            dto.categoriaNombre = p.getCategoria().getNombre();
        }

        dto.recogida = LugarRecogidaDTO.fromEntity(p.getRecogida());

        return dto;
    }
}