package dev.tomas.tfg.rest.eventos.repository;

import dev.tomas.tfg.rest.eventos.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByGrupoId(UUID grupoId);
}
