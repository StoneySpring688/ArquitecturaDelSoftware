package SegundUM.Usuarios.dominio;

import java.time.LocalDate;

public class ResumenUsuario {

    private String id;
    private String email;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String telefono;
    private boolean administrador;

    public ResumenUsuario(String id, String email, String nombre, String apellidos,
                          LocalDate fechaNacimiento, String telefono, boolean administrador) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.administrador = administrador;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getTelefono() { return telefono; }
    public boolean isAdministrador() { return administrador; }
}
