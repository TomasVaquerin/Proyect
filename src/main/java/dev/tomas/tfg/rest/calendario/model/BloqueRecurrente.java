package dev.tomas.tfg.rest.calendario.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Embeddable
public class BloqueRecurrente {
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}