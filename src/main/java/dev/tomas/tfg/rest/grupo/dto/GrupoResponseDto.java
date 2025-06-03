package dev.tomas.tfg.rest.grupo.dto;

import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import lombok.Builder;

import java.util.Set;

@Builder
public record GrupoResponseDto(
        String id,
        String nombre,
        String admin,
        Set<String> miembros,
        CalendarioResponseDto calendario
) {}