package dev.tomas.tfg.rest.calendario.dto;

import dev.tomas.tfg.rest.calendario.model.DiaSemana;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record BloqueRecurrenteDto(
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin
) {}