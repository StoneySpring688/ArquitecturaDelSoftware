package SegundUM.Usuarios.rest.excepciones;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import SegundUM.Usuarios.repositorio.EntidadNoEncontrada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class EntidadNoEncontradaExceptionMapper implements ExceptionMapper<EntidadNoEncontrada> {

    private static final Logger logger = LoggerFactory.getLogger(EntidadNoEncontradaExceptionMapper.class);

    public Response toResponse(EntidadNoEncontrada exception) {
        logger.warn("Entidad no encontrada: {}", exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
