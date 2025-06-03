package dev.tomas.tfg.rest.calendario.repository;

import dev.tomas.tfg.rest.calendario.model.Calendario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CalendarioRepository extends JpaRepository<Calendario, UUID> {
    List<Calendario> findByUserId(UUID userId);
}
