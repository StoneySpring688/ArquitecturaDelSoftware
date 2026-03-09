package SegundUM.Productos.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

import org.springframework.hateoas.RepresentationModel;

public class ProductoUpdateDTO extends RepresentationModel<ProductoUpdateDTO> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    public String descripcion;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    public BigDecimal precio;

    @NotBlank(message = "El ID del vendedor es obligatorio para verificar permisos")
    public String vendedorId;

    public ProductoUpdateDTO() {}
}