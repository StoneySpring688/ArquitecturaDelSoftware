package SegundUM.Productos.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.*;

import org.springframework.format.annotation.DateTimeFormat;

import SegundUM.Productos.dominio.EstadoProducto;
import SegundUM.Productos.dominio.Producto;

public class ProductoDTO implements Serializable {
    
    private static final long serialVersionUID = 7064953061294088595L;
	public String id;
	
	@NotBlank(message = "El título no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    public String titulo;
	
	@Size(max = 2000, message = "La descripción es demasiado larga")
    public String descripcion;
	
	@NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser positivo")
    public BigDecimal precio;
	
	@NotNull(message = "Debes especificar el estado del producto")
    public EstadoProducto estado;
	
	@PastOrPresent(message = "La fecha de publicación no puede ser futura")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalDateTime fechaPublicacion;
	
	@PositiveOrZero(message = "El número de visualizaciones no puede ser negativo")
    public Integer visualizaciones;
	
	@NotNull(message = "Debes indicar si el envío está disponible")
    public boolean envioDisponible;
    
	@NotBlank(message = "El ID del vendedor es obligatorio")
    public String vendedorId;
    
    // Solo dejo lo esencial de la categoría para quitar cosas de en medio (no hace falta más)
    @NotNull(message = "La categoría es obligatoria")
    public String categoriaId;
    public String categoriaNombre;
    
    @NotNull(message = "El lugar de recogida es obligatorio")
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