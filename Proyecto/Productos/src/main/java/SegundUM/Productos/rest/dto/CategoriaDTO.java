package SegundUM.Productos.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import SegundUM.Productos.dominio.Categoria;

public class CategoriaDTO {
    public String id;
    public String nombre;
    public String descripcion;
    public List<CategoriaDTO> subcategorias = new ArrayList<>();

    public CategoriaDTO() {}

    public static CategoriaDTO fromEntity(Categoria entidad) {
        if (entidad == null) return null;
        
        CategoriaDTO dto = new CategoriaDTO();
        dto.id = entidad.getId();
        dto.nombre = entidad.getNombre();
        dto.descripcion = entidad.getDescripcion();
        
        if (entidad.getSubcategorias() != null) {
            dto.subcategorias = entidad.getSubcategorias().stream()
                .map(CategoriaDTO::fromEntity)
                .collect(Collectors.toList());
        }
        return dto;
    }
}