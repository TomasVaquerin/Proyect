package dev.tomas.tfg.rest.eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsuarioNoApuntadoAlEventoException extends RuntimeException {

    public UsuarioNoApuntadoAlEventoException() {
        super("El usuario no está apuntado al evento");
    }
}

