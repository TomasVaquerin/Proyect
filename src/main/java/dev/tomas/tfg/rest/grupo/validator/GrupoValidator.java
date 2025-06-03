package dev.tomas.tfg.rest.grupo.validator;

import dev.tomas.tfg.rest.grupo.exceptions.AccesoDenegadoGrupoException;
import dev.tomas.tfg.rest.grupo.exceptions.CreadorNoPuedeAbandonarGrupoException;
import dev.tomas.tfg.rest.grupo.exceptions.GrupoNotFoundException;
import dev.tomas.tfg.rest.grupo.exceptions.UsuarioNoPerteneceAlGrupoException;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.grupo.repository.GrupoRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GrupoValidator {

    private final GrupoRepository grupoRepository;

    public GrupoValidator(GrupoRepository grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    public Grupo validateGrupoExists(UUID grupoId) {
        return grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException(grupoId));
    }

    public void validateIsCreator(Grupo grupo, UUID userId) {
        if (!grupo.getCreador().getId().equals(userId)) {
            throw new AccesoDenegadoGrupoException();
        }
    }

    public void validateNotCreator(Grupo grupo, UUID userId) {
        if (grupo.getCreador().getId().equals(userId)) {
            throw new CreadorNoPuedeAbandonarGrupoException();
        }
    }

    public void validateUserInGroup(Grupo grupo, UUID userId) {
        boolean exists = grupo.getUsuarios().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (!exists) {
            throw new UsuarioNoPerteneceAlGrupoException();
        }
    }
}
