package dev.tomas.tfg.rest.eventos.validator;

import dev.tomas.tfg.rest.eventos.exception.AccesoDenegadoEventoException;
import dev.tomas.tfg.rest.grupo.exceptions.UsuarioNoPerteneceAlGrupoException;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    public void validarEsCreadorDelGrupo(Grupo grupo, User user) {
        if (!grupo.getCreador().getId().equals(user.getId())) {
            throw new AccesoDenegadoEventoException();
        }
    }

    public void validarEsCreadorDelEvento(Event event, User user) {
        if (!event.getCreador().getId().equals(user.getId())) {
            throw new AccesoDenegadoEventoException();
        }
    }

    public void validarUsuarioPerteneceAlGrupo(Event event, User user) {
        boolean pertenece = event.getGrupo().getUsuarios().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (!pertenece) {
            throw new UsuarioNoPerteneceAlGrupoException();
        }
    }
}