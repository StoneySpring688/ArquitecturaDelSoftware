package SegundUM.Usuarios.dominio;

import javax.persistence.*;

import SegundUM.Usuarios.repositorio.Identificable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements Identificable {
    
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellidos;
    
    @Column(nullable = false)
    private String clave;
    
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    private String telefono;
    
    @Column(nullable = false)
    private boolean administrador;
    
    /**
     * TODO
     * Este atributo hay que revisarlo, a ser psible en el laboratorio de prácticas,
     * puede que lo mejor sea una nueva consulta sql en el microservicio de productos
     * si se mantiene este atributo habrá que mantenerlo cada vez que el usuario publique un producto
     **/
    @ElementCollection
    @CollectionTable(name = "productos_id")
    private List<String> productos = new ArrayList<>();
    
    // Constructor por defecto para JPA
    protected Usuario() {}
    
    public Usuario(String id, String email, String nombre, String apellidos, 
                   String clave, LocalDate fechaNacimiento, String telefono) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.clave = clave;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.administrador = false;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellidos() {
        return apellidos;
    }
    
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    
    public String getClave() {
        return clave;
    }
    
    public void setClave(String clave) {
        this.clave = clave;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public boolean isAdministrador() {
        return administrador;
    }
    
    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }
    
    public List<String> getProductosId() {
        return productos;
    }
}