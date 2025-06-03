package dev.tomas.tfg.rest.calendario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CalendarioConflictException extends RuntimeException {
    public CalendarioConflictException(String message) {
        super(message);
    }
}
