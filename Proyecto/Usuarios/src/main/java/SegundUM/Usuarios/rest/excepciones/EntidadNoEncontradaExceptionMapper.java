package SegundUM.Usuarios.rest.excepciones;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;

@Provider
public class EntidadNoEncontradaExceptionMapper implements ExceptionMapper<EntidadNoEncontrada> {
    public Response toResponse(EntidadNoEncontrada exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
