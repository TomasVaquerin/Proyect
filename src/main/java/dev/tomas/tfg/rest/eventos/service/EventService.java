package dev.tomas.tfg.rest.eventos.service;

import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.dto.EventResponseDto;
import dev.tomas.tfg.rest.user.model.User;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<EventResponseDto> findAll();
    EventResponseDto crearEvent(UUID groupId, EventRequestDto dto, User creator);
    EventResponseDto editarEvent(UUID eventId, EventRequestDto dto, User creator);
    void eliminarEvent(UUID eventId, User creator);
    void unirseEvent(UUID eventId, User user);
    void salirDelEvent(UUID eventId, User user);
    EventResponseDto obtenerEvent(UUID eventId);
    List<EventResponseDto> findAllByGrupoId(UUID grupoId);
}
