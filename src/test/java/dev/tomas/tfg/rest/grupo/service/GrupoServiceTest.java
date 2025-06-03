package dev.tomas.tfg.rest.grupo.service;

import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.mapper.CalendarioMapper;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import dev.tomas.tfg.rest.calendario.repository.CalendarioRepository;
import dev.tomas.tfg.rest.grupo.dto.GrupoRequestDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
import dev.tomas.tfg.rest.grupo.mapper.GrupoMapper;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.grupo.notifications.GrupoNotification;
import dev.tomas.tfg.rest.grupo.repository.GrupoRepository;
import dev.tomas.tfg.rest.grupo.service.helper.GrupoHelper;
import dev.tomas.tfg.rest.grupo.validator.GrupoValidator;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.SAME_THREAD)
class GrupoServiceTest {

    private GrupoRepository grupoRepository;
    private GrupoMapper grupoMapper;
    private GrupoValidator grupoValidator;
    private UserValidator userValidator;
    private GrupoHelper grupoHelper;
    private GrupoNotification grupoNotification;
    private CalendarioRepository calendarioRepository;
    private GrupoServiceImpl grupoService;

    private UUID grupoId;
    private UUID userId;
    private Grupo grupo;
    private User user;

    @BeforeEach
    void setUp() {
        grupoRepository = mock(GrupoRepository.class);
        grupoMapper = mock(GrupoMapper.class);
        grupoValidator = mock(GrupoValidator.class);
        userValidator = mock(UserValidator.class);
        grupoHelper = mock(GrupoHelper.class);
        grupoNotification = mock(GrupoNotification.class);
        calendarioRepository = mock(CalendarioRepository.class);

        grupoService = new GrupoServiceImpl(
                grupoRepository, grupoMapper, grupoValidator, userValidator,
                grupoHelper, grupoNotification, calendarioRepository
        );

        grupoId = UUID.randomUUID();
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        grupo = new Grupo();
        grupo.setId(grupoId);
        grupo.setUsuarios(new HashSet<>(List.of(user)));
        grupo.setCreador(user);
    }

    @Test
    void findAll_devuelveLista() {
        when(grupoRepository.findAll()).thenReturn(List.of(grupo));
        when(grupoValidator.validateGrupoExists(grupoId)).thenReturn(grupo);
        when(calendarioRepository.findByUserId(any())).thenReturn(List.of());

        List<GrupoResponseDto> result = grupoService.findAll();

        assertEquals(1, result.size());
        verify(grupoRepository).findAll();
    }

    @Test
    void getById_existente() {
        when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
        Grupo result = grupoService.getById(grupoId);
        assertEquals(grupo, result);
    }

    @Test
    void getById_noExistente_lanzaExcepcion() {
        when(grupoRepository.findById(grupoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> grupoService.getById(grupoId));
    }

    @Test
    void crearGrupo_ok() {
        // Arrange
        GrupoRequestDto dto = new GrupoRequestDto("Grupo nuevo", "Descripción del grupo");

        Grupo grupoNuevo = new Grupo();
        grupoNuevo.setCreador(user);
        grupoNuevo.setUsuarios(new HashSet<>(Set.of(user)));

        Grupo grupoGuardado = new Grupo();
        grupoGuardado.setId(grupoId);
        grupoGuardado.setCreador(user);
        grupoGuardado.setUsuarios(new HashSet<>(Set.of(user)));

        CalendarioResponseDto calendario = new CalendarioResponseDto(
                UUID.randomUUID(),
                List.of(),
                List.of(),
                userId
        );
        GrupoResponseDto grupoResponseEsperado = new GrupoResponseDto(
                grupoId.toString(),
                "Grupo nuevo",
                "Descripción del grupo",
                Set.of("Usuario1", "Usuario2"),
                calendario
        );

        when(grupoValidator.validateGrupoExists(grupoGuardado.getId())).thenReturn(grupoGuardado);
        when(userValidator.validateUserExists(userId)).thenReturn(user);
        when(grupoMapper.toEntity("Grupo nuevo", user, Set.of(user))).thenReturn(grupoNuevo);
        when(grupoRepository.save(grupoNuevo)).thenReturn(grupoGuardado);
        when(calendarioRepository.findByUserId(any())).thenReturn(List.of()); // Para getCalendarioDeGrupo

        try (
                MockedStatic<GrupoMapper> grupoMapperStatic = Mockito.mockStatic(GrupoMapper.class);
                MockedStatic<CalendarioMapper> calendarioMapperStatic = Mockito.mockStatic(CalendarioMapper.class)
        ) {
            calendarioMapperStatic.when(() -> CalendarioMapper.toDto((Calendario) any())).thenReturn(null);
            grupoMapperStatic.when(() -> GrupoMapper.toDto(grupoGuardado, calendario)).thenReturn(grupoResponseEsperado);

            GrupoResponseDto resultado = grupoService.crearGrupo(dto, userId);

            assertNotNull(resultado);
            assertEquals(grupoResponseEsperado, resultado);
            verify(userValidator).validateUserExists(userId);
            verify(grupoMapper).toEntity("Grupo nuevo", user, Set.of(user));
            verify(grupoRepository).save(grupoNuevo);
            verify(grupoNotification).notifyGrupoCreado(grupoResponseEsperado);
        }
    }


    @Test
    void unirseAGrupo_ok() {
        when(grupoValidator.validateGrupoExists(grupoId)).thenReturn(grupo);
        when(userValidator.validateUserExists(userId)).thenReturn(user);

        grupoService.unirseAGrupo(grupoId, userId);

        verify(grupoRepository).save(grupo);
        assertTrue(grupo.getUsuarios().contains(user));
    }

    @Test
    void salirDeGrupo_ok() {
        when(grupoValidator.validateGrupoExists(grupoId)).thenReturn(grupo);
        when(userValidator.validateUserExists(userId)).thenReturn(user);

        grupoService.salirDeGrupo(grupoId, userId);

        verify(grupoValidator).validateNotCreator(grupo, userId);
        verify(grupoHelper).removeUserFromGroup(grupo, user);
        verify(grupoRepository).save(grupo);
    }

    @Test
    void expulsarUsuario_ok() {
        UUID userIdAEliminar = UUID.randomUUID();
        User userAEliminar = new User();
        userAEliminar.setId(userIdAEliminar);

        when(grupoValidator.validateGrupoExists(grupoId)).thenReturn(grupo);
        when(userValidator.validateUserExists(userIdAEliminar)).thenReturn(userAEliminar);

        grupoService.expulsarUsuario(grupoId, userId, userIdAEliminar);

        verify(grupoValidator).validateIsCreator(grupo, userId);
        verify(grupoHelper).removeUserFromGroup(grupo, userAEliminar);
        verify(grupoRepository).save(grupo);
    }

    @Test
    void getCalendarioDeGrupo_ok() {
        when(grupoValidator.validateGrupoExists(grupoId)).thenReturn(grupo);
        Calendario calendario = new Calendario();
        calendario.setBloquesRecurrentes(List.of());
        calendario.setExcepciones(List.of());
        when(calendarioRepository.findByUserId(any())).thenReturn(List.of(calendario));

        CalendarioResponseDto result = grupoService.getCalendarioDeGrupo(grupoId);

        assertNotNull(result);
        assertNotNull(result.bloquesRecurrentes());
        assertNotNull(result.excepciones());
    }
}