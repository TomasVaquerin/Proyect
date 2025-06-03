package dev.tomas.tfg.rest.grupo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsuarioNoPerteneceAlGrupoException extends GrupoException {
    public UsuarioNoPerteneceAlGrupoException() {
        super("El usuario no pertenece a este grupo.");
    }
}