package dev.tomas.tfg.rest.eventos.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Builder
public record EventResponseDto(
        String id,
        String grupoNombre,
        String creadorNombre,
        LocalDate fecha,
        List<LocalTime> horas,
        Integer duracionMinutos,
        String ubicacion,
        String imagenUrl,
        boolean encuestaDeCambioDeHora,
        String tipo,
        Set<String> usuariosApuntados,
        LocalDateTime fechaCreacion,
        boolean eliminado
) {}
