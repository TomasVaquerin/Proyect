package dev.tomas.tfg.rest.grupo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccesoDenegadoGrupoException extends GrupoException {
    public AccesoDenegadoGrupoException() {
        super("Solo el creador puede expulsar usuarios.");
    }
}