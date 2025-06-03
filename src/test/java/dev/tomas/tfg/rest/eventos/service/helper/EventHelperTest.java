package dev.tomas.tfg.rest.eventos.service.helper;

import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.exception.EventNotFoundException;
import dev.tomas.tfg.rest.eventos.mapper.EventMapper;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.eventos.repository.EventRepository;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventHelperTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;

    private EventHelper eventHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventHelper = new EventHelper(eventRepository, eventMapper);
    }

    @Test
    void findEventIfExists_found() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setEliminado(false);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        Optional<Event> result = eventHelper.findEventIfExists(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void findEventIfExists_notFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Event> result = eventHelper.findEventIfExists(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void getEventOrThrow_found() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setEliminado(false);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        Event result = eventHelper.getEventOrThrow(id);

        assertEquals(id, result.getId());
    }

    @Test
    void getEventOrThrow_notFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventHelper.getEventOrThrow(id));
    }

    @Test
    void addUserToEvent_addsUser() {
        Event event = new Event();
        User user = new User();
        user.setId(UUID.randomUUID());
        event.setUsuariosApuntados(new HashSet<>());

        eventHelper.addUserToEvent(event, user);

        assertTrue(event.getUsuariosApuntados().contains(user));
    }

    @Test
    void removerUsuarioDelEvento_removesUser() {
        Event event = new Event();
        User user = new User();
        user.setId(UUID.randomUUID());
        Set<User> usuarios = new HashSet<>();
        usuarios.add(user);
        event.setUsuariosApuntados(usuarios);

        boolean removed = eventHelper.removerUsuarioDelEvento(event, user);

        assertTrue(removed);
        assertFalse(event.getUsuariosApuntados().contains(user));
    }

    @Test
    void construirEventoDesdeDto_setsFields() {
        EventRequestDto dto = EventRequestDto.builder().nombre("Test").build();
        Grupo grupo = new Grupo();
        User creator = new User();
        Event event = new Event();

        when(eventMapper.toEntity(dto, grupo, creator)).thenReturn(event);

        Event result = eventHelper.construirEventoDesdeDto(dto, grupo, creator);

        assertNotNull(result);
        assertNotNull(result.getUsuariosApuntados());
        assertTrue(result.getUsuariosApuntados().isEmpty());
    }
}