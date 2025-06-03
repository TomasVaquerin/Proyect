package dev.tomas.tfg.rest.comentarios.service;

import dev.tomas.tfg.rest.comentarios.dto.ComentarioRequestDto;
import dev.tomas.tfg.rest.comentarios.dto.ComentarioResponseDto;
import dev.tomas.tfg.rest.comentarios.exception.AccesoDenegadoComentarioException;
import dev.tomas.tfg.rest.comentarios.mapper.ComentarioMapper;
import dev.tomas.tfg.rest.comentarios.model.Comentario;
import dev.tomas.tfg.rest.comentarios.repository.ComentarioRepository;
import dev.tomas.tfg.rest.comentarios.validator.ComentarioValidator;
import dev.tomas.tfg.rest.eventos.exception.EventNotFoundException;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.eventos.repository.EventRepository;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComentarioServiceImplTest {

    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ComentarioValidator comentarioValidator;

    @InjectMocks
    private ComentarioServiceImpl comentarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllComentariosDeEvento_ok() {
        UUID eventoId = UUID.randomUUID();
        User usuario = new User();
        usuario.setId(UUID.randomUUID());

        Event evento = new Event();
        Grupo grupo = new Grupo();
        grupo.setUsuarios(Set.of(usuario));
        evento.setGrupo(grupo);

        Comentario comentario = new Comentario();
        List<Comentario> comentarios = List.of(comentario);
        ComentarioResponseDto dto = ComentarioResponseDto.builder().id(UUID.randomUUID()).mensaje("Hola").build();

        ComentarioServiceImpl spyService = Mockito.spy(comentarioService);
        doReturn(evento).when(spyService).getEventoOrThrow(eventoId);
        when(comentarioRepository.findByEventoAndEliminadoFalse(evento)).thenReturn(comentarios);

        try (MockedStatic<ComentarioMapper> mocked = mockStatic(ComentarioMapper.class)) {
            mocked.when(() -> ComentarioMapper.toDto(comentario)).thenReturn(dto);

            List<ComentarioResponseDto> result = spyService.getAllComentariosDeEvento(eventoId, usuario);

            assertEquals(1, result.size());
            assertEquals("Hola", result.get(0).mensaje());
        }
    }

    @Test
    void getAllComentariosDeEvento_accesoDenegado() {
        UUID eventoId = UUID.randomUUID();
        User usuario = new User();
        usuario.setId(UUID.randomUUID());

        Event evento = new Event();
        Grupo grupo = new Grupo();
        grupo.setUsuarios(Set.of()); // Usuario no pertenece
        evento.setGrupo(grupo);

        ComentarioServiceImpl spyService = Mockito.spy(comentarioService);
        doReturn(evento).when(spyService).getEventoOrThrow(eventoId);

        assertThrows(AccesoDenegadoComentarioException.class, () -> {
            spyService.getAllComentariosDeEvento(eventoId, usuario);
        });
    }

    @Test
    void getAllComentariosDeEvento_grupoNull() {
        UUID eventoId = UUID.randomUUID();
        User usuario = new User();
        usuario.setId(UUID.randomUUID());

        Event evento = new Event();
        evento.setGrupo(null);

        ComentarioServiceImpl spyService = Mockito.spy(comentarioService);
        doReturn(evento).when(spyService).getEventoOrThrow(eventoId);

        assertThrows(AccesoDenegadoComentarioException.class, () -> {
            spyService.getAllComentariosDeEvento(eventoId, usuario);
        });
    }

    @Test
    void crearComentario_ok() {
        UUID eventoId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        ComentarioRequestDto dto = new ComentarioRequestDto(eventoId, autorId, "Mensaje");
        Event evento = new Event();
        User autor = new User();
        autor.setId(autorId);
        evento.setGrupo(new dev.tomas.tfg.rest.grupo.model.Grupo());
        evento.getGrupo().setUsuarios(Set.of(autor));

        when(eventRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(userRepository.findById(autorId)).thenReturn(Optional.of(autor));
        try (MockedStatic<ComentarioMapper> mocked = mockStatic(ComentarioMapper.class)) {
            Comentario comentario = new Comentario();
            ComentarioResponseDto responseDto = ComentarioResponseDto.builder().mensaje("Mensaje").build();
            mocked.when(() -> ComentarioMapper.toEntity(dto, evento, autor)).thenReturn(comentario);
            mocked.when(() -> ComentarioMapper.toDto(comentario)).thenReturn(responseDto);

            ComentarioResponseDto result = comentarioService.crearComentario(dto);

            assertEquals("Mensaje", result.mensaje());
            verify(comentarioRepository).save(comentario);
        }
    }

    @Test
    void crearComentario_accesoDenegado() {
        UUID eventoId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        ComentarioRequestDto dto = new ComentarioRequestDto(eventoId, autorId, "Mensaje");
        Event evento = new Event();
        User autor = new User();
        autor.setId(autorId);
        evento.setGrupo(new dev.tomas.tfg.rest.grupo.model.Grupo());
        evento.getGrupo().setUsuarios(Set.of()); // El autor no está en el grupo

        when(eventRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(userRepository.findById(autorId)).thenReturn(Optional.of(autor));

        assertThrows(AccesoDenegadoComentarioException.class, () -> {
            comentarioService.crearComentario(dto);
        });
    }

    @Test
    void crearComentario_eventoNoExiste() {
        UUID eventoId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        ComentarioRequestDto dto = new ComentarioRequestDto(eventoId, autorId, "Mensaje");

        when(eventRepository.findById(eventoId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            comentarioService.crearComentario(dto);
        });
    }

    @Test
    void borrarComentario_ok() {
        UUID comentarioId = UUID.randomUUID();
        User solicitante = new User();
        Comentario comentario = new Comentario();

        when(comentarioValidator.validateComentarioExists(comentarioId)).thenReturn(comentario);
        doNothing().when(comentarioValidator).validatePermisosDeBorrado(comentario, solicitante);
        when(comentarioRepository.save(comentario)).thenReturn(comentario);

        comentarioService.borrarComentario(comentarioId, solicitante);

        assertTrue(comentario.isEliminado());
        verify(comentarioRepository).save(comentario);
    }
}