package dev.tomas.tfg.rest.eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("No se encontró el evento con ID " + eventId);
    }
}

