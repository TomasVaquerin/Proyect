package dev.tomas.tfg.rest.grupo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GrupoNotFoundException extends GrupoException {
    public GrupoNotFoundException(UUID grupoId) {
        super("Grupo no encontrado con ID: " + grupoId);
    }
}
