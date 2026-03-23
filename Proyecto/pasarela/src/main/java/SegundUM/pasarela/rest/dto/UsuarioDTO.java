package SegundUM.pasarela.rest.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
    private String id;
    private String email;
    private String nombre;
    private String apellidos;
    private boolean administrador;

    public UsuarioDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public boolean isAdministrador() { return administrador; }
    public void setAdministrador(boolean administrador) { this.administrador = administrador; }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
