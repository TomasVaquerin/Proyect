package dev.tomas.tfg.rest.grupo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CreadorNoPuedeAbandonarGrupoException extends GrupoException {
    public CreadorNoPuedeAbandonarGrupoException() {
        super("El creador no puede abandonar su propio grupo.");
    }
}