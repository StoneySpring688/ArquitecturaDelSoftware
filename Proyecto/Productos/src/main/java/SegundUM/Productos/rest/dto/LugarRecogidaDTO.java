package SegundUM.Productos.rest.dto;

import java.io.Serializable;

import SegundUM.Productos.dominio.LugarRecogida;

public class LugarRecogidaDTO implements Serializable {
    private static final long serialVersionUID = 8886619346871580550L;
	public String descripcion;
    public Double longitud;
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