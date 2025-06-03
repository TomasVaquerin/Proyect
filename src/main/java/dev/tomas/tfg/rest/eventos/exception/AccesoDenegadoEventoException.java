package dev.tomas.tfg.rest.eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccesoDenegadoEventoException extends EventException {

    public AccesoDenegadoEventoException() {
        super("Acceso denegado al evento");
    }
}
