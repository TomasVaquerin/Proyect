package dev.tomas.tfg.rest.grupo.validator;

import dev.tomas.tfg.rest.grupo.exceptions.*;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.grupo.repository.GrupoRepository;
import dev.tomas.tfg.rest.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrupoValidatorTest {

    private GrupoRepository grupoRepository;
    private GrupoValidator grupoValidator;

    @BeforeEach
    void setUp() {
        grupoRepository = mock(GrupoRepository.class);
        grupoValidator = new GrupoValidator(grupoRepository);
    }

    @Test
    void validateGrupoExists_returnsGrupo() {
        UUID id = UUID.randomUUID();
        Grupo grupo = createGrupo(id, UUID.randomUUID());
        when(grupoRepository.findById(id)).thenReturn(Optional.of(grupo));

        Grupo result = grupoValidator.validateGrupoExists(id);

        assertThat(result).isEqualTo(grupo);
    }

    @Test
    void validateGrupoExists_throwsGrupoNotFoundException() {
        UUID id = UUID.randomUUID();
        when(grupoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> grupoValidator.validateGrupoExists(id))
                .isInstanceOf(GrupoNotFoundException.class);
    }

    @Test
    void validateIsCreator_doesNotThrow() {
        UUID userId = UUID.randomUUID();
        Grupo grupo = createGrupo(UUID.randomUUID(), userId);

        assertThatCode(() -> grupoValidator.validateIsCreator(grupo, userId))
                .doesNotThrowAnyException();
    }

    @Test
    void validateIsCreator_throwsExceptionIfNotCreator() {
        Grupo grupo = createGrupo(UUID.randomUUID(), UUID.randomUUID());
        UUID otroUserId = UUID.randomUUID();

        assertThatThrownBy(() -> grupoValidator.validateIsCreator(grupo, otroUserId))
                .isInstanceOf(AccesoDenegadoGrupoException.class);
    }

    @Test
    void validateNotCreator_doesNotThrowIfUserNotCreator() {
        Grupo grupo = createGrupo(UUID.randomUUID(), UUID.randomUUID());
        UUID otroUserId = UUID.randomUUID();

        assertThatCode(() -> grupoValidator.validateNotCreator(grupo, otroUserId))
                .doesNotThrowAnyException();
    }

    @Test
    void validateNotCreator_throwsExceptionIfUserIsCreator() {
        UUID creadorId = UUID.randomUUID();
        Grupo grupo = createGrupo(UUID.randomUUID(), creadorId);

        assertThatThrownBy(() -> grupoValidator.validateNotCreator(grupo, creadorId))
                .isInstanceOf(CreadorNoPuedeAbandonarGrupoException.class);
    }

    @Test
    void validateUserInGroup_doesNotThrowIfUserInGroup() {
        UUID userId = UUID.randomUUID();
        Grupo grupo = createGrupoConUsuario(userId);

        assertThatCode(() -> grupoValidator.validateUserInGroup(grupo, userId))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUserInGroup_throwsIfUserNotInGroup() {
        UUID userId = UUID.randomUUID();
        Grupo grupo = createGrupoConUsuario(UUID.randomUUID());

        assertThatThrownBy(() -> grupoValidator.validateUserInGroup(grupo, userId))
                .isInstanceOf(UsuarioNoPerteneceAlGrupoException.class);
    }


    private Grupo createGrupo(UUID grupoId, UUID creadorId) {
        User creador = new User();
        creador.setId(creadorId);
        creador.setNombre("Nombre");
        creador.setApellidos("Apellido");

        Grupo grupo = new Grupo();
        grupo.setId(grupoId);
        grupo.setNombre("Grupo");
        grupo.setCreador(creador);
        grupo.setUsuarios(Set.of(creador));
        return grupo;
    }

    private Grupo createGrupoConUsuario(UUID userId) {
        User user = new User();
        user.setId(userId);

        Grupo grupo = new Grupo();
        grupo.setId(UUID.randomUUID());
        grupo.setNombre("Grupo");
        grupo.setCreador(new User());
        grupo.setUsuarios(Set.of(user));
        return grupo;
    }
}
