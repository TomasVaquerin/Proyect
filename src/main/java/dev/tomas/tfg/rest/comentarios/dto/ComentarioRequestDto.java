package dev.tomas.tfg.rest.comentarios.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ComentarioRequestDto(
    UUID eventoId,
    UUID autorId,
    String mensaje
){}
