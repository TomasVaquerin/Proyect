package dev.tomas.tfg.rest.calendario.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Embeddable
public class ExcepcionCalendario {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean disponible; // true = disponible, false = no disponible
}
