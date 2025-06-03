package dev.tomas.tfg.rest.grupo.model;

import dev.tomas.tfg.rest.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Grupo {

    @Id
    private UUID id;

    private String nombre;

    private String descripciton;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private User creador;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "grupo_user",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> usuarios = new HashSet<>();
}

