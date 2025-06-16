package dev.tomas.tfg.rest.comentarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccesoDenegadoComentarioException extends ComentariosException {

    public AccesoDenegadoComentarioException() {
        super("Acceso denegado al comentario");
    }
}