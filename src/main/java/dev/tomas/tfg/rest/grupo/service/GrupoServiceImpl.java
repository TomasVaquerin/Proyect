package dev.tomas.tfg.rest.grupo.service;

import dev.tomas.tfg.rest.calendario.dto.BloqueRecurrenteDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.dto.ExcepcionCalendarioDto;
import dev.tomas.tfg.rest.calendario.mapper.CalendarioMapper;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import dev.tomas.tfg.rest.calendario.repository.CalendarioRepository;
import dev.tomas.tfg.rest.grupo.dto.GrupoRequestDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoUpdateDto;
import dev.tomas.tfg.rest.grupo.exceptions.GrupoNotFoundException;
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

/**
 * Implementación del servicio para gestionar grupos.
 * Proporciona métodos para realizar operaciones CRUD sobre grupos y gestionar la participación de usuarios.
 */
@Service
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository grupoRepository;
    private final GrupoMapper grupoMapper;
    private final GrupoValidator grupoValidator;
    private final UserValidator userValidator;
    private final GrupoHelper grupoHelper;
    private final GrupoNotification grupoNotification;
    private final CalendarioRepository calendarioRepository;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param grupoRepository Repositorio para gestionar grupos.
     * @param grupoMapper Mapper para convertir entre entidades y DTOs de grupos.
     * @param grupoValidator Validador para verificar reglas de negocio de los grupos.
     * @param userValidator Validador para verificar reglas de negocio de los usuarios.
     * @param grupoHelper Helper para operaciones auxiliares relacionadas con grupos.
     * @param grupoNotification Notificaciones relacionadas con grupos.
     * @param calendarioRepository Repositorio para gestionar calendarios.
     */
    public GrupoServiceImpl(
            GrupoRepository grupoRepository,
            GrupoMapper grupoMapper,
            GrupoValidator grupoValidator,
            UserValidator userValidator,
            GrupoHelper grupoHelper,
            GrupoNotification grupoNotification,
            CalendarioRepository calendarioRepository
    ) {
        this.grupoRepository = grupoRepository;
        this.grupoMapper = grupoMapper;
        this.grupoValidator = grupoValidator;
        this.userValidator = userValidator;
        this.grupoHelper = grupoHelper;
        this.grupoNotification = grupoNotification;
        this.calendarioRepository = calendarioRepository;
    }

    /**
     * Obtiene todos los grupos.
     *
     * @return Lista de grupos en formato DTO.
     */
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

    /**
     * Obtiene un grupo por su ID.
     *
     * @param id ID del grupo a buscar.
     * @return El grupo encontrado.
     * @throws GrupoNotFoundException Si el grupo no existe.
     */
    @Override
    public Grupo getById(UUID id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new GrupoNotFoundException(id));
    }

    /**
     * Crea un nuevo grupo.
     *
     * @param dto Datos del grupo a crear.
     * @param creadorId ID del usuario creador del grupo.
     * @return El grupo creado en formato DTO.
     */
    @Override
    public GrupoResponseDto crearGrupo(GrupoRequestDto dto, UUID creadorId) {
        User creador = userValidator.validateUserExists(creadorId);

        Grupo grupo = grupoMapper.toEntity(dto.nombre(), creador);
        Grupo grupoGuardado = grupoRepository.save(grupo);

        grupoGuardado.getUsuarios().add(creador);
        grupoRepository.save(grupoGuardado);

        CalendarioResponseDto calendario = getCalendarioDeGrupo(grupoGuardado.getId());
        GrupoResponseDto response = GrupoMapper.toDto(grupoGuardado, calendario);
        grupoNotification.notifyGrupoCreado(response);
        return response;
    }

    /**
     * Actualiza un grupo existente.
     *
     * @param grupoId ID del grupo a actualizar.
     * @param dto Datos actualizados del grupo.
     * @param userId ID del usuario que realiza la actualización.
     * @return El grupo actualizado en formato DTO.
     */
    @Override
    @Transactional
    public GrupoResponseDto updateGrupo(UUID grupoId, GrupoUpdateDto dto, UUID userId) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        userValidator.validateUserExists(userId);
        grupoValidator.validateIsCreator(grupo, userId);

        grupo.setNombre(dto.nombre());
        grupo.setDescription(dto.descripcion());
        grupoRepository.save(grupo);

        CalendarioResponseDto calendario = getCalendarioDeGrupo(grupo.getId());
        return GrupoMapper.toDto(grupo, calendario);
    }

    /**
     * Permite a un usuario unirse a un grupo.
     *
     * @param grupoId ID del grupo al que el usuario desea unirse.
     * @param userId ID del usuario que desea unirse.
     */
    @Override
    @Transactional
    public void unirseAGrupo(UUID grupoId, UUID userId) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        User user = userValidator.validateUserExists(userId);

        grupo.getUsuarios().add(user);
        grupoRepository.save(grupo);
    }

    /**
     * Permite a un usuario salir de un grupo.
     *
     * @param grupoId ID del grupo del que el usuario desea salir.
     * @param userId ID del usuario que desea salir.
     */
    @Override
    @Transactional
    public void salirDeGrupo(UUID grupoId, UUID userId) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        User user = userValidator.validateUserExists(userId);

        grupoValidator.validateNotCreator(grupo, userId);
        grupoHelper.removeUserFromGroup(grupo, user);
        grupoRepository.save(grupo);
    }

    /**
     * Expulsa a un usuario de un grupo.
     *
     * @param grupoId ID del grupo del que se desea expulsar al usuario.
     * @param creadorId ID del creador del grupo.
     * @param userIdAEliminar ID del usuario a expulsar.
     */
    @Override
    @Transactional
    public void expulsarUsuario(UUID grupoId, UUID creadorId, UUID userIdAEliminar) {
        Grupo grupo = grupoValidator.validateGrupoExists(grupoId);
        User userAEliminar = userValidator.validateUserExists(userIdAEliminar);

        grupoValidator.validateIsCreator(grupo, creadorId);
        grupoHelper.removeUserFromGroup(grupo, userAEliminar);
        grupoRepository.save(grupo);
    }

    /**
     * Obtiene el calendario asociado a un grupo.
     *
     * @param grupoId ID del grupo.
     * @return El calendario del grupo en formato DTO.
     */
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