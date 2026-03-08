package SegundUM.Productos.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.*;

import org.springframework.hateoas.RepresentationModel;

import SegundUM.Productos.dominio.LugarRecogida;

public class LugarRecogidaDTO extends RepresentationModel<LugarRecogidaDTO> implements Serializable {
    private static final long serialVersionUID = 8886619346871580550L;
    
    @NotBlank(message = "La descripción del lugar es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
	public String descripcion;
    
    @NotNull(message = "La longitud es obligatoria")
    @Min(-180) @Max(180)
    public Double longitud;
    
    @NotNull(message = "La latitud es obligatoria")
    @Min(-90) @Max(90)
    public Double latitud;

    public LugarRecogidaDTO() {}

    public static LugarRecogidaDTO fromEntity(LugarRecogida entidad) {
        if (entidad == null) return null;
        LugarRecogidaDTO dto = new LugarRecogidaDTO();
        dto.descripcion = entidad.getDescripcion();
        dto.longitud = entidad.getLongitud();
        dto.latitud = entidad.getLatitud();
        return dto;
    }
}