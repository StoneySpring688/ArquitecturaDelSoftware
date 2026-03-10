package SegundUM.Compraventas.servicio;

public class ServicioException extends Exception {
	
	private static final long serialVersionUID = 5427607768838102003L;

	public ServicioException(String msg) {
        super(msg);
    }

    public ServicioException(String msg, Throwable causa) {
        super(msg, causa);
    }
}