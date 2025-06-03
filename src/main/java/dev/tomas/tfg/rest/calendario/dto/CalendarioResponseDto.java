package dev.tomas.tfg.rest.calendario.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CalendarioResponseDto(
        UUID id,
        List<BloqueRecurrenteDto> bloquesRecurrentes,
        List<ExcepcionCalendarioDto> excepciones,
        UUID userId
) {}