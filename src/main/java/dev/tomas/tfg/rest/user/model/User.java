package dev.tomas.tfg.rest.user.model;

import dev.tomas.tfg.rest.calendario.model.Calendario;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jdk.jshell.Snippet;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    private String nombre;
    private String apellidos;
    private String fotoPerfil;
    private LocalDate fechaNacimiento;
    @OneToOne(mappedBy = "user")
    private Calendario calendario;
}
