package dev.tomas.tfg.rest.comentarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ComentarioNotFoundException extends ComentariosException {

    public ComentarioNotFoundException(UUID comentarioId) {
        super("Comentario no encontrado con ID: " + comentarioId);
    }
}
