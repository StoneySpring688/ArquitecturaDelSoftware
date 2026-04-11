package SegundUM.Productos.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import SegundUM.Productos.dominio.LugarRecogida;

public class LugarRecogidaAltaProductoDTO implements Serializable {
	private static final long serialVersionUID = 6385697183328793244L;

	@NotBlank(message = "La descripción del lugar es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
	public String descripcion;
    
    @NotNull(message = "La longitud es obligatoria")
    @Min(-180) @Max(180)
    public Double longitud;
    
    @NotNull(message = "La latitud es obligatoria")
    @Min(-90) @Max(90)
    public Double latitud;

    public LugarRecogidaAltaProductoDTO() {}

    public static LugarRecogidaDTO fromEntity(LugarRecogida entidad) {
        if (entidad == null) return null;
        LugarRecogidaDTO dto = new LugarRecogidaDTO();
        dto.descripcion = entidad.getDescripcion();
        dto.longitud = entidad.getLongitud();
        dto.latitud = entidad.getLatitud();
        return dto;
    }

    public LugarRecogida toEntity() {
        return new LugarRecogida(this.descripcion, this.longitud, this.latitud);
    }
    
    @Override
    public String toString() {
    			return "LugarRecogidaDTO{" +
				"descripcion='" + descripcion + '\'' +
				", longitud=" + longitud +
				", latitud=" + latitud +
				'}';
    }
}

