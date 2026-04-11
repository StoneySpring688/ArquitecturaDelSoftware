package SegundUM.Productos.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.*;
import SegundUM.Productos.dominio.EstadoProducto;

/**
 * DTO para la creación de un nuevo producto.
 * Contiene únicamente los campos necesarios para el alta.
 */
public class AltaProductoDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

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
    
    @NotNull(message = "Debes indicar si el envío está disponible")
    public boolean envioDisponible;
    
    @NotBlank(message = "El ID de la categoría es obligatorio")
    public String categoriaId;
    
    @NotBlank(message = "El ID del vendedor es obligatorio")
    public String vendedorId;
    
    @Valid
    @NotNull(message = "El lugar de recogida es obligatorio")
    public LugarRecogidaAltaProductoDTO recogida;

    public AltaProductoDTO() {}

    @Override
    public String toString() {
        return "AltaProductoDTO{" +
                "titulo='" + titulo + '\'' +
                ", precio=" + precio +
                ", estado=" + estado +
                ", envioDisponible=" + envioDisponible +
                ", categoriaId='" + categoriaId + '\'' +
                ", vendedorId='" + vendedorId + '\'' +
                ", recogida=" + recogida +
                '}';
    }
}
