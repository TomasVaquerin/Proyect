package dev.tomas.tfg.rest.grupo.service.helper;

import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

class GrupoHelperTest {

    private final GrupoHelper grupoHelper = new GrupoHelper();

    @Test
    void removeUserFromGroup_removesUserIfPresent() {
        Grupo grupo = new Grupo();
        User user = new User();

        UUID userId = UUID.randomUUID();
        user.setId(userId);

        grupo.getUsuarios().add(user);

        grupoHelper.removeUserFromGroup(grupo, user);

        assertFalse(grupo.getUsuarios().contains(user));
    }

}

