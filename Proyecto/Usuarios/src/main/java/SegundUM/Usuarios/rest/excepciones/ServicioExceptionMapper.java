package SegundUM.Usuarios.rest.excepciones;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import SegundUM.Usuarios.servicio.ServicioException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ServicioExceptionMapper implements ExceptionMapper<ServicioException> {

    private static final Logger logger = LoggerFactory.getLogger(ServicioExceptionMapper.class);

    @Override
    public Response toResponse(ServicioException exception) {
        logger.error("Error de servicio: {}", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .build();
    }

}
