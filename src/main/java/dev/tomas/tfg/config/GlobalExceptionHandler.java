package dev.tomas.tfg.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleCustomExceptions(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();

        HttpStatus status = getHttpStatusFromAnnotation(ex); // 💡 Obtiene automáticamente el código HTTP

        response.put("error", ex.getMessage());
        response.put("status", status.value());

        return new ResponseEntity<>(response, status);
    }

    private HttpStatus getHttpStatusFromAnnotation(RuntimeException ex) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        return (responseStatus != null) ? responseStatus.value() : HttpStatus.BAD_REQUEST;
    }
}
