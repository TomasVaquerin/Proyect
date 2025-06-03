package dev.tomas.tfg.rest.calendario.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ExcepcionCalendarioDto(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean disponible
) {}