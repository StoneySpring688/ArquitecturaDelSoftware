package SegundUM.pasarela.rest.dto;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private String email;
    private String clave;

    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}
