package dev.tomas.tfg.rest.grupo.service;

import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoRequestDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoUpdateDto;
import dev.tomas.tfg.rest.grupo.model.Grupo;

import java.util.List;
import java.util.UUID;

public interface GrupoService {
    List<GrupoResponseDto> findAll();
    Grupo getById(UUID id);
    GrupoResponseDto crearGrupo(GrupoRequestDto dto, UUID creadorId);
    GrupoResponseDto updateGrupo(UUID grupoId, GrupoUpdateDto dto, UUID userId);
    void unirseAGrupo(UUID grupoId, UUID userId);
    void salirDeGrupo(UUID grupoId, UUID userId);
    void expulsarUsuario(UUID grupoId, UUID creadorId, UUID userIdAEliminar);
    CalendarioResponseDto getCalendarioDeGrupo(UUID grupoId);
}
