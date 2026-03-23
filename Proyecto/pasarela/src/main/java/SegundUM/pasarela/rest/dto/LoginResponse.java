package SegundUM.pasarela.rest.dto;

import java.io.Serializable;
import java.util.List;

public class LoginResponse implements Serializable {
    private String token;
    private String id;
    private String nombre;
    private List<String> roles;

    public LoginResponse() {}

    public LoginResponse(String token, String id, String nombre, List<String> roles) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
