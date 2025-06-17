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

/**
 * Implementación del servicio para gestionar eventos.
 * Proporciona métodos para realizar operaciones CRUD sobre eventos y gestionar la participación de usuarios.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final EventHelper eventHelper;
    private final GrupoValidator grupoValidator;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param eventRepository Repositorio para gestionar eventos.
     * @param eventMapper Mapper para convertir entre entidades y DTOs de eventos.
     * @param eventValidator Validador para verificar reglas de negocio de los eventos.
     * @param eventHelper Helper para operaciones auxiliares relacionadas con eventos.
     * @param grupoValidator Validador para verificar reglas de negocio de los grupos.
     */
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

    /**
     * Obtiene todos los eventos.
     *
     * @return Lista de eventos en formato DTO.
     */
    @Override
    public List<EventResponseDto> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los eventos asociados a un grupo.
     * Los resultados se almacenan en caché.
     *
     * @param grupoId ID del grupo al que pertenecen los eventos.
     * @return Lista de eventos en formato DTO.
     */
    @Override
    @Cacheable(value = "eventosPorGrupo", key = "#grupoId")
    public List<EventResponseDto> findAllByGrupoId(UUID grupoId) {
        return eventRepository.findByGrupoId(grupoId).stream()
                .map(EventMapper::toDto)
                .toList();
    }

    /**
     * Crea un nuevo evento asociado a un grupo.
     * Actualiza la caché con el nuevo evento.
     *
     * @param groupId ID del grupo al que se asocia el evento.
     * @param dto Datos del evento a crear.
     * @param creator Usuario que crea el evento.
     * @return El evento creado en formato DTO.
     */
    @Override
    @CachePut(value = "eventosPorGrupo", key = "#result.id()")
    public EventResponseDto crearEvent(UUID groupId, EventRequestDto dto, User creator) {
        Grupo grupo = grupoValidator.validateGrupoExists(groupId);
        eventValidator.validarEsCreadorDelGrupo(grupo, creator);
        Event event = eventHelper.construirEventoDesdeDto(dto, grupo, creator);
        return EventMapper.toDto(eventRepository.save(event));
    }

    /**
     * Edita un evento existente.
     * Actualiza la caché con el evento editado.
     *
     * @param eventId ID del evento a editar.
     * @param dto Datos actualizados del evento.
     * @param creator Usuario que edita el evento.
     * @return El evento editado en formato DTO.
     */
    @Override
    @CachePut(value = "eventosPorGrupo", key = "#eventId")
    public EventResponseDto editarEvent(UUID eventId, EventRequestDto dto, User creator) {
        Event event = eventHelper.getEventOrThrow(eventId);
        eventValidator.validarEsCreadorDelEvento(event, creator);
        eventMapper.updateEntityFromDto(event, dto);
        return EventMapper.toDto(eventRepository.save(event));
    }

    /**
     * Elimina un evento marcándolo como eliminado.
     * Invalida la caché del evento eliminado.
     *
     * @param eventId ID del evento a eliminar.
     * @param creator Usuario que elimina el evento.
     */
    @Override
    @CacheEvict(value = "eventosPorGrupo", key = "#eventId")
    public void eliminarEvent(UUID eventId, User creator) {
        Event event = eventHelper.getEventOrThrow(eventId);
        eventValidator.validarEsCreadorDelEvento(event, creator);
        event.setEliminado(true);
        eventRepository.save(event);
    }

    /**
     * Permite a un usuario unirse a un evento.
     *
     * @param eventId ID del evento al que el usuario desea unirse.
     * @param user Usuario que desea unirse al evento.
     */
    @Override
    public void unirseEvent(UUID eventId, User user) {
        Event event = eventHelper.getEventOrThrow(eventId);
        eventValidator.validarUsuarioPerteneceAlGrupo(event, user);
        eventHelper.addUserToEvent(event, user);
        eventRepository.save(event);
    }

    /**
     * Permite a un usuario salir de un evento.
     * Invalida la caché del evento modificado.
     *
     * @param eventId ID del evento del que el usuario desea salir.
     * @param user Usuario que desea salir del evento.
     * @throws UsuarioNoApuntadoAlEventoException Si el usuario no está inscrito en el evento.
     */
    @Override
    @CacheEvict(value = "eventosPorGrupo", key = "#eventId")
    public void salirDelEvent(UUID eventId, User user) {
        Event event = eventHelper.getEventOrThrow(eventId);
        if (!eventHelper.removerUsuarioDelEvento(event, user)) {
            throw new UsuarioNoApuntadoAlEventoException();
        }
        eventRepository.save(event);
    }

    /**
     * Obtiene un evento por su ID.
     *
     * @param eventId ID del evento a buscar.
     * @return El evento encontrado en formato DTO.
     */
    @Override
    public EventResponseDto obtenerEvent(UUID eventId) {
        Event event = eventHelper.getEventOrThrow(eventId);
        return EventMapper.toDto(event);
    }
}