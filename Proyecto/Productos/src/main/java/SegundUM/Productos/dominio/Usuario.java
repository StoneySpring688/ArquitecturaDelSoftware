package SegundUM.Productos.dominio;

import javax.persistence.*;

import SegundUM.Productos.repositorio.Identificable;

/**
 * Esto es una versión simplificada de los usuarios,
 * los datos necesarios se actualizarán con información proveniente del microservicio de usuarios.
 * Sirve para usar la bd como una cahé de los usuarios respecto a los productos, para evitar hacer llamadas excesivas a la api
 * del microservicio de usuarios.
 **/

@Entity
@Table(name = "usuarios_replica")
public class Usuario implements Identificable{
    
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellidos;

    // Constructor por defecto para JPA
    protected Usuario() {}

    public Usuario(String id, String email, String nombre, String apellidos) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    // Método para obtener nombre completo (útil para vistas si fuese necesario)
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellidos;
    }
}