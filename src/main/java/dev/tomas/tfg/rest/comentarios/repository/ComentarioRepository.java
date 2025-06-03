package dev.tomas.tfg.rest.comentarios.repository;

import dev.tomas.tfg.rest.comentarios.model.Comentario;
import dev.tomas.tfg.rest.eventos.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {
    List<Comentario> findByEventoAndEliminadoFalse(Event evento);

}
