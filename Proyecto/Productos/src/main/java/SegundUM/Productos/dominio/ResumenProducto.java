package SegundUM.Productos.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResumenProducto {
    
    private String id;
    private String titulo;
    private BigDecimal precio;
    private LocalDateTime fechaPublicacion;
    private String nombreCategoria;
    private Integer visualizaciones;
    
    public ResumenProducto() {}
    
    public ResumenProducto(String id, String titulo, BigDecimal precio, 
                           LocalDateTime fechaPublicacion, String nombreCategoria, 
                           Integer visualizaciones) {
        this.id = id;
        this.titulo = titulo;
        this.precio = precio;
        this.fechaPublicacion = fechaPublicacion;
        this.nombreCategoria = nombreCategoria;
        this.visualizaciones = visualizaciones;
    }
    
    public static ResumenProducto fromEntity(Producto p) {
        if (p == null) return null;
        
        ResumenProducto resumen = new ResumenProducto();
        resumen.id = p.getId();
        resumen.titulo = p.getTitulo();
        resumen.precio = p.getPrecio();
        resumen.fechaPublicacion = p.getFechaPublicacion();
        resumen.nombreCategoria = p.getCategoria() != null ? p.getCategoria().getNombre() : null;
        resumen.visualizaciones = p.getVisualizaciones();
        
        return resumen;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }
    
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    
    public Integer getVisualizaciones() {
        return visualizaciones;
    }
    
    @Override
    public String toString() {
        return String.format("ID: %s | Título: %s | Precio: %.2f€ | Publicado: %s | Categoría: %s | Visualizaciones: %d",
                id, titulo, precio, fechaPublicacion, nombreCategoria, visualizaciones);
    }
}