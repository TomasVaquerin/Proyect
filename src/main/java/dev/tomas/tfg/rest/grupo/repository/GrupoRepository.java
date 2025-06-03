package dev.tomas.tfg.rest.grupo.repository;

import dev.tomas.tfg.rest.grupo.model.Grupo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GrupoRepository extends JpaRepository<Grupo, UUID> {
    @EntityGraph(attributePaths = "usuarios")
    List<Grupo> findAll();
}
