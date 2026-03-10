package SegundUM.Compraventas.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Este DTO es para los POST 
 **/
public class NuevaCompraventaDTO implements Serializable {
    
    private static final long serialVersionUID = 1565480739304972359L;

	@NotBlank(message = "El ID del producto es obligatorio")
    private String idProducto;
    
    @NotBlank(message = "El ID del comprador es obligatorio")
    private String idComprador;
    
    @NotBlank(message = "El email del comprador es obligatorio")
    @Email(message = "Debe ser un formato de email válido")
    private String emailComprador;
    
    @NotBlank(message = "La clave del comprador es obligatoria")
    private String claveComprador;

    // Getters y Setters
    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }
    
    public String getIdComprador() { return idComprador; }
    public void setIdComprador(String idComprador) { this.idComprador = idComprador; }
    
    public String getEmailComprador() { return emailComprador; }
    public void setEmailComprador(String emailComprador) { this.emailComprador = emailComprador; }
    
    public String getClaveComprador() { return claveComprador; }
    public void setClaveComprador(String claveComprador) { this.claveComprador = claveComprador; }
}