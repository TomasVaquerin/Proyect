package dev.tomas.tfg.rest.eventos.model;

import dev.tomas.tfg.rest.calendario.model.Calendario;
import dev.tomas.tfg.rest.comentarios.model.Comentario;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Event {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creador_id")
    private User creador;

    private LocalDate fecha;

    private String nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evento_horas", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "hora")
    private List<LocalTime> horas;

    private Integer duracionMinutos;

    private String ubicacion;

    private String imagenUrl;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    private boolean encuestaDeCambioDeHora;

    private String tipo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "evento_usuarios_apuntados",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> usuariosApuntados;

    private LocalDateTime fechaCreacion;

    private boolean eliminado;

}