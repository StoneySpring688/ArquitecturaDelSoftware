package SegundUM.Productos.dominio;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LugarRecogida {
    
    @Column(length = 500)
    private String descripcion;
    
    private Double longitud;
    
    private Double latitud;
    
    // Constructor por defecto para JPA
    protected LugarRecogida() {}
    
    public LugarRecogida(String descripcion, Double longitud, Double latitud) {
        this.descripcion = descripcion;
        this.longitud = longitud;
        this.latitud = latitud;
    }
    
    // Getters y setters
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Double getLongitud() {
        return longitud;
    }
    
    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
    
    public Double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
    
    @Override
    public String toString() {
    			return "LugarRecogida{" +
				"descripcion='" + descripcion + '\'' +
				", longitud=" + longitud +
				", latitud=" + latitud +
				'}';
    }
}