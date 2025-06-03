package dev.tomas.tfg.rest.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends UserException {
    public UserNotFoundException(UUID id) {
        super("Usuario no encontrado con id: " + id);
    }
    public UserNotFoundException(String email) {
        super("Usuario no encontrado con email: " + email);
    }
}
