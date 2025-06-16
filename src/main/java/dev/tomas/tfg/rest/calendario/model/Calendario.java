package dev.tomas.tfg.rest.calendario.model;

import dev.tomas.tfg.rest.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Calendario {
    @Id
    private UUID id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<BloqueRecurrente> bloquesRecurrentes;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<ExcepcionCalendario> excepciones;

    @OneToOne
    private User user;
}