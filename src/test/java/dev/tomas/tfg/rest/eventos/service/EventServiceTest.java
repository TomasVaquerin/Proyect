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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventHelper eventHelper;
    @Mock
    private GrupoValidator grupoValidator;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_returnsList() {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        List<Event> events = List.of(event);

        when(eventRepository.findAll()).thenReturn(events);
        try (MockedStatic<EventMapper> mocked = mockStatic(EventMapper.class)) {
            mocked.when(() -> EventMapper.toDto(any(Event.class)))
                    .thenReturn(EventResponseDto.builder().id(event.getId().toString()).build());

            List<EventResponseDto> result = eventService.findAll();

            assertEquals(1, result.size());
            verify(eventRepository).findAll();
        }
    }

    @Test
    void findAllByGrupoId_returnsList() {
        UUID grupoId = UUID.randomUUID();
        Event event = new Event();
        event.setId(UUID.randomUUID());
        List<Event> events = List.of(event);

        when(eventRepository.findByGrupoId(grupoId)).thenReturn(events);
        try (MockedStatic<EventMapper> mocked = mockStatic(EventMapper.class)) {
            mocked.when(() -> EventMapper.toDto(any(Event.class)))
                    .thenReturn(EventResponseDto.builder().id(event.getId().toString()).build());

            List<EventResponseDto> result = eventService.findAllByGrupoId(grupoId);

            assertEquals(1, result.size());
            verify(eventRepository).findByGrupoId(grupoId);
        }
    }

    @Test
    void crearEvent_ok() {
        UUID grupoId = UUID.randomUUID();
        User creador = new User();
        EventRequestDto dto = EventRequestDto.builder().nombre("Evento").build();
        Grupo grupo = new Grupo();
        Event event = new Event();
        event.setId(UUID.randomUUID());
        EventResponseDto responseDto = EventResponseDto.builder().id(event.getId().toString()).creadorNombre("Evento").build();

        when(grupoValidator.validateGrupoExists(grupoId)).thenReturn(grupo);
        doNothing().when(eventValidator).validarEsCreadorDelGrupo(grupo, creador);
        when(eventHelper.construirEventoDesdeDto(dto, grupo, creador)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);

        try (MockedStatic<EventMapper> mocked = mockStatic(EventMapper.class)) {
            mocked.when(() -> EventMapper.toDto(event)).thenReturn(responseDto);

            EventResponseDto result = eventService.crearEvent(grupoId, dto, creador);

            assertNotNull(result);
            assertEquals("Evento", result.creadorNombre());
            verify(eventRepository).save(event);
        }
    }

    @Test
    void editarEvent_ok() {
        UUID eventId = UUID.randomUUID();
        User creador = new User();
        EventRequestDto dto = EventRequestDto.builder().nombre("Editado").build();
        Event event = new Event();
        event.setId(eventId);
        EventResponseDto responseDto = EventResponseDto.builder().id(eventId.toString()).creadorNombre("Editado").build();

        when(eventHelper.getEventOrThrow(eventId)).thenReturn(event);
        doNothing().when(eventValidator).validarEsCreadorDelEvento(event, creador);
        doNothing().when(eventMapper).updateEntityFromDto(event, dto);
        when(eventRepository.save(event)).thenReturn(event);

        try (MockedStatic<EventMapper> mocked = mockStatic(EventMapper.class)) {
            mocked.when(() -> EventMapper.toDto(event)).thenReturn(responseDto);

            EventResponseDto result = eventService.editarEvent(eventId, dto, creador);

            assertNotNull(result);
            assertEquals("Editado", result.creadorNombre());
            verify(eventRepository).save(event);
        }
    }

    @Test
    void eliminarEvent_ok() {
        UUID eventId = UUID.randomUUID();
        User creador = new User();
        Event event = new Event();
        event.setId(eventId);

        when(eventHelper.getEventOrThrow(eventId)).thenReturn(event);
        doNothing().when(eventValidator).validarEsCreadorDelEvento(event, creador);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.eliminarEvent(eventId, creador);

        assertTrue(event.isEliminado());
        verify(eventRepository).save(event);
    }

    @Test
    void unirseEvent_ok() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        Event event = new Event();
        event.setId(eventId);

        when(eventHelper.getEventOrThrow(eventId)).thenReturn(event);
        doNothing().when(eventValidator).validarUsuarioPerteneceAlGrupo(event, user);
        doNothing().when(eventHelper).addUserToEvent(event, user);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.unirseEvent(eventId, user);

        verify(eventRepository).save(event);
    }

    @Test
    void salirDelEvent_ok() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        Event event = new Event();
        event.setId(eventId);

        when(eventHelper.getEventOrThrow(eventId)).thenReturn(event);
        when(eventHelper.removerUsuarioDelEvento(event, user)).thenReturn(true);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.salirDelEvent(eventId, user);

        verify(eventRepository).save(event);
    }

    @Test
    void obtenerEvent_ok() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        EventResponseDto responseDto = EventResponseDto.builder().id(eventId.toString()).creadorNombre("Evento").build();

        when(eventHelper.getEventOrThrow(eventId)).thenReturn(event);

        try (MockedStatic<EventMapper> mocked = mockStatic(EventMapper.class)) {
            mocked.when(() -> EventMapper.toDto(event)).thenReturn(responseDto);

            EventResponseDto result = eventService.obtenerEvent(eventId);

            assertNotNull(result);
            assertEquals("Evento", result.creadorNombre());
        }
    }

    @Test
    void salirDelEvent_usuarioNoApuntado_lanzaExcepcion() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Event event = new Event();
        event.setId(eventId);

        when(eventHelper.getEventOrThrow(eventId)).thenReturn(event);
        when(eventHelper.removerUsuarioDelEvento(event, user)).thenReturn(false);

        assertThrows(UsuarioNoApuntadoAlEventoException.class, () -> {
            eventService.salirDelEvent(eventId, user);
        });
    }
}