package SegundUM.Productos.repositorio;

/**
 * Excepción que representa que una entidad no ha sido encontrada en el repositorio.
 */
@SuppressWarnings("serial")
public class EntidadNoEncontrada extends Exception {
    
    public EntidadNoEncontrada(String mensaje) {
        super(mensaje);
    }
}