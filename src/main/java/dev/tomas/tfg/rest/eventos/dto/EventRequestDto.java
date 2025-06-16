package dev.tomas.tfg.rest.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record EventRequestDto(
        @NotBlank(message = "El nombre del evento no puede estar vacío")
        String nombre,
        @NotNull(message = "La fecha del evento no puede estar vacía")
        LocalDate fecha,
        @NotNull(message = "La hora del evento no puede estar vacía")
        List<LocalTime> horas,


        Integer duracionMinutos,
        @NotBlank(message = "La ubicacion del evento no puede estar vacío")
        String ubicacion,
        String imagenUrl,
        boolean encuestaDeCambioDeHora,
        @NotBlank(message = "El tipo del evento no puede estar vacío")
        String tipo
) {}
