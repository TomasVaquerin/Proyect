package dev.tomas.tfg.rest.eventos.service.helper;

import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.exception.EventNotFoundException;
import dev.tomas.tfg.rest.eventos.exception.UsuarioNoApuntadoAlEventoException;
import dev.tomas.tfg.rest.eventos.mapper.EventMapper;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.eventos.repository.EventRepository;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventHelper {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventHelper(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public Optional<Event> findEventIfExists(UUID eventId) {
        return eventRepository.findById(eventId)
                .filter(event -> !event.isEliminado());
    }

    public Event getEventOrThrow(UUID eventId) {
        return findEventIfExists(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public void addUserToEvent(Event event, User user) {
        event.getUsuariosApuntados().add(user);
    }

    public boolean removerUsuarioDelEvento(Event event, User user) {
        return event.getUsuariosApuntados().removeIf(u -> u.getId().equals(user.getId()));
    }

    public Event construirEventoDesdeDto(EventRequestDto dto, Grupo grupo, User creator) {
        Event event = eventMapper.toEntity(dto, grupo, creator);
        event.setUsuariosApuntados(new HashSet<>());
        return event;
    }
}
