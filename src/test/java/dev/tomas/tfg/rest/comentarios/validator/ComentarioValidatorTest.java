package dev.tomas.tfg.rest.comentarios.validator;

import dev.tomas.tfg.rest.comentarios.exception.AccesoDenegadoComentarioException;
import dev.tomas.tfg.rest.comentarios.exception.ComentarioNotFoundException;
import dev.tomas.tfg.rest.comentarios.model.Comentario;
import dev.tomas.tfg.rest.comentarios.repository.ComentarioRepository;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComentarioValidatorTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private ComentarioValidator comentarioValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateComentarioExists_ok() {
        UUID comentarioId = UUID.randomUUID();
        Comentario comentario = new Comentario();
        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentario));

        Comentario result = comentarioValidator.validateComentarioExists(comentarioId);

        assertEquals(comentario, result);
    }

    @Test
    void validateComentarioExists_notFound() {
        UUID comentarioId = UUID.randomUUID();
        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.empty());

        assertThrows(ComentarioNotFoundException.class, () -> {
            comentarioValidator.validateComentarioExists(comentarioId);
        });
    }

    @Test
    void validatePermisosDeBorrado_ok() {
        User user = new User();
        user.setId(UUID.randomUUID());

        // Creador del grupo
        User creadorGrupo = new User();
        creadorGrupo.setId(UUID.randomUUID());

        // Grupo
        Grupo grupo = new Grupo();
        grupo.setId(UUID.randomUUID());
        grupo.setCreador(creadorGrupo);

        // Evento
        Event evento = new Event();
        evento.setId(UUID.randomUUID());
        evento.setGrupo(grupo);

        // Comentario
        Comentario comentario = new Comentario();
        comentario.setAutor(user);
        comentario.setEvento(evento);

        assertDoesNotThrow(() -> comentarioValidator.validatePermisosDeBorrado(comentario, user));
    }

    @Test
    void validatePermisosDeBorrado_denegado() {
        User user = new User();
        user.setId(UUID.randomUUID());
        User otro = new User();
        otro.setId(UUID.randomUUID());

        User creadorGrupo = new User();
        creadorGrupo.setId(UUID.randomUUID());
        Grupo grupo = new Grupo();
        grupo.setId(UUID.randomUUID());
        grupo.setCreador(creadorGrupo);

        Event evento = new Event();
        evento.setId(UUID.randomUUID());
        evento.setGrupo(grupo);

        Comentario comentario = new Comentario();
        comentario.setAutor(otro);
        comentario.setEvento(evento);

        assertThrows(AccesoDenegadoComentarioException.class, () -> {
            comentarioValidator.validatePermisosDeBorrado(comentario, user);
        });
    }
}