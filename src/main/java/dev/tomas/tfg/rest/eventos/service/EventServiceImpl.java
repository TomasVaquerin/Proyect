package dev.tomas.tfg.rest.eventos.service;

import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.dto.EventResponseDto;
import dev.tomas.tfg.rest.eventos.exception.UsuarioNoApuntadoAlEventoException;
import dev.tomas.tfg.rest.eventos.mapper.EventMapper;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.eventos.repository.EventRepository;
import dev.tomas.tfg.rest.eventos.service.helper.EventHelper;
import dev.tomas.tfg.rest.eventos.validator.EventValidator;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.grupo.validator.GrupoValidator;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final EventHelper eventHelper;
    private final GrupoValidator grupoValidator;

    public EventServiceImpl(EventRepository eventRepository,
                            EventMapper eventMapper,
                            EventValidator eventValidator,
                            EventHelper eventHelper,
                            GrupoValidator grupoValidator) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventValidator = eventValidator;
        this.eventHelper = eventHelper;
        this.grupoValidator = grupoValidator;
    }

    @Override
    public List<EventResponseDto> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "eventosPorGrupo", key = "#grupoId")
    public List<EventResponseDto> findAllByGrupoId(UUID grupoId) {
        return eventRepository.findByGrupoId(grupoId).stream()
                .map(EventMapper::toDto)
                .toList();
    }

    @Override
    @CachePut(value = "eventosPorGrupo", key = "#result.id()")
    public EventResponseDto crearEvent(UUID groupId, EventRequestDto dto, User creator) {
        Grupo grupo = grupoValidator.validateGrupoExists(groupId);
        eventValidator.validarEsCreadorDelGrupo(grupo, creator);
        Event event = eventHelper.construirEventoDesdeDto(dto, grupo, creator);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @CachePut(value = "eventosPorGrupo", key = "#eventId")
    public EventResponseDto editarEvent(UUID eventId, EventRequestDto dto, User creator) {
        Event event = eventHelper.getEventOrThrow(eventId);
        eventValidator.validarEsCreadorDelEvento(event, creator);
        eventMapper.updateEntityFromDto(event, dto);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @CacheEvict(value = "eventosPorGrupo", key = "#eventId")
    public void eliminarEvent(UUID eventId, User creator) {
        Event event = eventHelper.getEventOrThrow(eventId);
        eventValidator.validarEsCreadorDelEvento(event, creator);
        event.setEliminado(true);
        eventRepository.save(event);
    }

    @Override
    public void unirseEvent(UUID eventId, User user) {
        Event event = eventHelper.getEventOrThrow(eventId);
        eventValidator.validarUsuarioPerteneceAlGrupo(event, user);
        eventHelper.addUserToEvent(event, user);
        eventRepository.save(event);
    }

    @Override
    @CacheEvict(value = "eventosPorGrupo", key = "#eventId")
    public void salirDelEvent(UUID eventId, User user) {
        Event event = eventHelper.getEventOrThrow(eventId);
        if (!eventHelper.removerUsuarioDelEvento(event, user)) {
            throw new UsuarioNoApuntadoAlEventoException();
        }
        eventRepository.save(event);
    }

    @Override
    public EventResponseDto obtenerEvent(UUID eventId) {
        Event event = eventHelper.getEventOrThrow(eventId);
        return EventMapper.toDto(event);
    }
}