package SegundUM.Usuarios.rest.excepciones;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import SegundUM.Usuarios.servicio.ServicioException;

@Provider
public class ServicioExceptionMapper implements ExceptionMapper<ServicioException> {
        
    @Override
    public Response toResponse(ServicioException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .build();
    }
    
}
