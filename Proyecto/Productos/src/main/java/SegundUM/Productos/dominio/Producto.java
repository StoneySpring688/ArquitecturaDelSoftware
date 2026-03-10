package SegundUM.Productos.dominio;

import javax.persistence.*;

import SegundUM.Productos.repositorio.Identificable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto implements Identificable {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(length = 2000)
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado;
    
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @Column(nullable = false)
    private Integer visualizaciones;
    
    @Column(name = "envio_disponible", nullable = false)
    private boolean envioDisponible;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "descripcion", column = @Column(name = "recogida_descripcion", length = 500)),
        @AttributeOverride(name = "longitud", column = @Column(name = "recogida_longitud")),
        @AttributeOverride(name = "latitud", column = @Column(name = "recogida_latitud"))
    })
    private LugarRecogida recogida;
    
    @Column(name = "vendedor_id", nullable = false)
    private String vendedorId;
    
    // Constructor por defecto para JPA
    protected Producto() {}
    
    public Producto(String id, String titulo, String descripcion, BigDecimal precio,
                    EstadoProducto estado, Categoria categoria, boolean envioDisponible,
                    String vendedorId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.categoria = categoria;
        this.envioDisponible = envioDisponible;
        this.vendedorId = vendedorId;
        this.fechaPublicacion = LocalDateTime.now();
        this.visualizaciones = 0;
    }
    
    // Método para incrementar visualizaciones
    public void incrementarVisualizaciones() {
        this.visualizaciones++;
    }
    
    // Getters y setters (implementa Identificable)
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public EstadoProducto getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }
    
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public Integer getVisualizaciones() {
        return visualizaciones;
    }
    
    public void setVisualizaciones(Integer visualizaciones) {
        this.visualizaciones = visualizaciones;
    }
    
    public boolean isEnvioDisponible() {
        return envioDisponible;
    }
    
    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }
    
    public LugarRecogida getRecogida() {
        return recogida;
    }
    
    public void setRecogida(LugarRecogida recogida) {
        this.recogida = recogida;
    }
    
    public String getVendedorId() {
        return vendedorId;
    }
    
    public void setVendedorId(String vendedorId) {
        this.vendedorId = vendedorId;
    }
    
    @Override
    public String toString() {
    			return "Producto{" +
				"id='" + id + '\'' +
				", titulo='" + titulo + '\'' +
				", descripcion='" + descripcion + '\'' +
				", precio=" + precio +
				", estado=" + estado +
				", fechaPublicacion=" + fechaPublicacion +
				", categoria=" + (categoria != null ? categoria.getNombre() : "null") +
				", visualizaciones=" + visualizaciones +
				", envioDisponible=" + envioDisponible +
				", recogida=" + (recogida != null ? recogida.getDescripcion() : "null") +
				", vendedorId='" + vendedorId + '\'' +
				'}';
    }
}