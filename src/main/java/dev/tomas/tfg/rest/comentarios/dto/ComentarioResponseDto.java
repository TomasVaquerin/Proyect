package dev.tomas.tfg.rest.comentarios.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ComentarioResponseDto (
    UUID id,
    UUID eventoId,
    UUID autorId,
    String mensaje,
    LocalDateTime fechaCreacion
){}
