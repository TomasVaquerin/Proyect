package dev.tomas.tfg.rest.grupo.service;

import dev.tomas.tfg.rest.calendario.dto.BloqueRecurrenteDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.dto.ExcepcionCalendarioDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository grupoRepository;
    private final GrupoMapper grupoMapper;
    private final GrupoValidator grupoValidator;
    private final UserValidator userValidator;
    private final GrupoHelper grupoHelper;
    private final GrupoNotification grupoNotification;
    private final CalendarioRepository calendarioRepository;

    public GrupoServiceImpl(GrupoRepository grupoRepository,
                            GrupoMapper grupoMapper,
                            GrupoValidator grupoValidator,
                            UserValidator userValidator,
                            GrupoHelper grupoHelper, GrupoNotification grupoNotification, CalendarioRepository calendarioRepository) {
        this.grupoRepository = grupoRepository;
        this.grupoMapper = grupoMapper;
        this.grupoValidator = grupoValidator;
        this.userValidator = userValidator;
        this.grupoHelper = grupoHelper;
        this.grupoNotification = grupoNotification;
        this.calendarioRepository = calendarioRepository;
    }

    @Override
    public List<GrupoResponseDto> findAll() {
        return grupoRepository.findAll()
                .stream()
                .map(grupo -> {
                    CalendarioResponseDto calendario = getCalendarioDeGrupo(grupo.getId());
                    return GrupoMapper.toDto(grupo, calendario);
                })
                .toList();
    }


    @Override
    public Grupo getById(UUID id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
    }

    @Override
    public GrupoResponseDto crearGrupo(GrupoRequestDto dto, UUID creadorId) {
        User creador = userValidator.validateUserExists(creadorId);
        Set<User> miembros = new HashSet<>();
        miembros.add(creador);
        Grupo grupo = grupoMapper.toEntity(dto.nombre(), creador, miembros);
        Grupo grupoGuardado = grupoRepository.save(grupo);
        CalendarioResponseDto calendario = getCalendarioDeGrupo(grupoGuardado.getId());
        GrupoResponseDto response = GrupoMapper.toDto(grupoGuardado, calendario);
        grupoNotification.notifyGrupoCreado(response);
        return response;
    }


    @Override
    @Transactional
    public void unirseAGrupo(UUID grupoId, UUID userId) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        User user = userValidator.validateUserExists(userId);

        grupo.getUsuarios().add(user);
        grupoRepository.save(grupo);
    }


    @Override
    @Transactional
    public void salirDeGrupo(UUID grupoId, UUID userId) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        User user = userValidator.validateUserExists(userId);

        grupoValidator.validateNotCreator(grupo, userId);
        grupoHelper.removeUserFromGroup(grupo, user);
        grupoRepository.save(grupo);
    }

    @Override
    @Transactional
    public void expulsarUsuario(UUID grupoId, UUID creadorId, UUID userIdAEliminar) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        User userAEliminar = userValidator.validateUserExists(userIdAEliminar);

        grupoValidator.validateIsCreator(grupo, creadorId);
        grupoHelper.removeUserFromGroup(grupo, userAEliminar);
        grupoRepository.save(grupo);
    }

    @Override
    public CalendarioResponseDto getCalendarioDeGrupo(UUID grupoId) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        List<Calendario> calendarios = grupo.getUsuarios().stream()
                .flatMap(user -> calendarioRepository.findByUserId(user.getId()).stream())
                .toList();

        List<BloqueRecurrenteDto> bloques = calendarios.stream()
                .flatMap(c -> c.getBloquesRecurrentes().stream())
                .map(CalendarioMapper::toDto)
                .toList();

        List<ExcepcionCalendarioDto> excepciones = calendarios.stream()
                .flatMap(c -> c.getExcepciones().stream())
                .map(CalendarioMapper::toDto)
                .toList();

        return CalendarioResponseDto.builder()
                .id(null)
                .bloquesRecurrentes(bloques)
                .excepciones(excepciones)
                .userId(null)
                .build();
    }
}
