package dev.tomas.tfg.rest.eventos.validator;

import dev.tomas.tfg.rest.eventos.exception.AccesoDenegadoEventoException;
import dev.tomas.tfg.rest.grupo.exceptions.UsuarioNoPerteneceAlGrupoException;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventValidatorTest {

    private final EventValidator validator = new EventValidator();

    @Test
    void validarEsCreadorDelGrupo_ok() {
        Grupo grupo = new Grupo();
        User user = new User();
        user.setId(UUID.randomUUID());
        grupo.setCreador(user);

        assertDoesNotThrow(() -> validator.validarEsCreadorDelGrupo(grupo, user));
    }

    @Test
    void validarEsCreadorDelGrupo_throw() {
        Grupo grupo = new Grupo();
        User user = new User();
        user.setId(UUID.randomUUID());
        User otro = new User();
        otro.setId(UUID.randomUUID());
        grupo.setCreador(otro);

        assertThrows(AccesoDenegadoEventoException.class, () -> validator.validarEsCreadorDelGrupo(grupo, user));
    }

    @Test
    void validarEsCreadorDelEvento_ok() {
        Event event = new Event();
        User user = new User();
        user.setId(UUID.randomUUID());
        event.setCreador(user);

        assertDoesNotThrow(() -> validator.validarEsCreadorDelEvento(event, user));
    }

    @Test
    void validarEsCreadorDelEvento_throw() {
        Event event = new Event();
        User user = new User();
        user.setId(UUID.randomUUID());
        User otro = new User();
        otro.setId(UUID.randomUUID());
        event.setCreador(otro);

        assertThrows(AccesoDenegadoEventoException.class, () -> validator.validarEsCreadorDelEvento(event, user));
    }

    @Test
    void validarUsuarioPerteneceAlGrupo_ok() {
        Event event = new Event();
        User user = new User();
        user.setId(UUID.randomUUID());

        // Crear grupo y añadir el usuario
        Grupo grupo = new Grupo();
        Set<User> usuarios = new HashSet<>();
        usuarios.add(user);
        grupo.setUsuarios(usuarios);

        // Asignar el grupo al evento
        event.setGrupo(grupo);

        assertDoesNotThrow(() -> validator.validarUsuarioPerteneceAlGrupo(event, user));
    }
    @Test
    void validarUsuarioPerteneceAlGrupo_throw() {
        Event event = new Event();
        User user = new User();
        user.setId(UUID.randomUUID());

        // Crear grupo vacío y asignarlo al evento
        Grupo grupo = new Grupo();
        grupo.setUsuarios(new HashSet<>()); // Sin usuarios
        event.setGrupo(grupo);

        assertThrows(UsuarioNoPerteneceAlGrupoException.class, () -> validator.validarUsuarioPerteneceAlGrupo(event, user));
    }
}