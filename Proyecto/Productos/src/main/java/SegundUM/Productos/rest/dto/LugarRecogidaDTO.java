package SegundUM.Productos.rest.dto;

import SegundUM.Productos.dominio.LugarRecogida;

public class LugarRecogidaDTO {
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