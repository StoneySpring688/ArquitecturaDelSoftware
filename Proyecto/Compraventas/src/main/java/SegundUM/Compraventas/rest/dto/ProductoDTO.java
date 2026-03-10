package SegundUM.Compraventas.rest.dto;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO extends RepresentationModel<ProductoDTO> {
	
	@NotBlank(message = "El ID del vendedor es obligatorio")
    private String vendedorId;
	
	@NotBlank(message = "El título del producto no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String titulo;
	
	@NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser positivo")
    private int precio;
	
	@NotNull(message = "El lugar de recogida es obligatorio")
    private LugarRecogidaDTO recogida;

	public ProductoDTO() {}
	
    public String getIdVendedor() { 
    	return vendedorId; 
    	}
    
    public String getTitulo() {
    	return titulo; 
    	}
    
    public int getPrecio() { 
    	return precio; 
    	}
    
    public LugarRecogidaDTO getRecogida() {
    	return recogida; 
    	}
    
    @Override
    public String toString() {
    			return "ProductoDTO{" +
				"idVendedor='" + vendedorId + '\'' +
				", titulo='" + titulo + '\'' +
				", precio=" + precio +
				", recogida=" + recogida +
				'}';
    			}
    
}
