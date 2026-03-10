package SegundUM.Compraventas.rest.dto;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioDTO extends RepresentationModel<UsuarioDTO> {
	
	@NotBlank(message = "El ID de usuario es obligatorio")
	private String id;
	
	@NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String nombre;
	
	public UsuarioDTO() {}
    
	public String getId() {
		return id;
	}
	public String getNombre() {
		return nombre;
	}
	
	@Override
		public String toString() {
			return "UsuarioDTO{" +
				"id='" + id + '\'' +
				", nombre='" + nombre + '\'' +
				'}';
	}
    
}
