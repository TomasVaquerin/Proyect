package dev.tomas.tfg.rest.calendario.dto;

import java.util.List;
import java.util.UUID;

public record CalendarioRequestDto(
        List<BloqueRecurrenteDto> bloquesRecurrentes,
        List<ExcepcionCalendarioDto> excepciones,
        UUID userId
) {}