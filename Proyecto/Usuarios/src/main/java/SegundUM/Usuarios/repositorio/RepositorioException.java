package SegundUM.Usuarios.repositorio;

/**
 * Excepción que representa un fallo en el sistema de persistencia.
 */
@SuppressWarnings("serial")
public class RepositorioException extends Exception {
    
    public RepositorioException(String msg, Throwable causa) {
        super(msg, causa);
    }
    
    public RepositorioException(String msg) {
        super(msg);
    }
}