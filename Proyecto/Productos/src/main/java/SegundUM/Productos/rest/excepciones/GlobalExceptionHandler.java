package SegundUM.Productos.rest.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import SegundUM.Productos.repositorio.EntidadNoEncontrada;
import SegundUM.Productos.servicio.ServicioException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadNoEncontrada.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFound(EntidadNoEncontrada ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ServicioException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleServiceError(ServicioException ex) {
        return ex.getMessage();
    }
    
}