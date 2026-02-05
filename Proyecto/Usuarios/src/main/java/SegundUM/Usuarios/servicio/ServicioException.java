package SegundUM.Usuarios.servicio;

public class ServicioException extends Exception {
    private static final long serialVersionUID = 2998172375041064008L;

	public ServicioException(String msg) {
        super(msg);
    }

    public ServicioException(String msg, Throwable causa) {
        super(msg, causa);
    }
}