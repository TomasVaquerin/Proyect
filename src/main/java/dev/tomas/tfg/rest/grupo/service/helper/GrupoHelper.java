package dev.tomas.tfg.rest.grupo.service.helper;

import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class GrupoHelper {

    public void removeUserFromGroup(Grupo grupo, User user) {
        grupo.getUsuarios().removeIf(u -> u.getId().equals(user.getId()));
    }
}
