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
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestionar comentarios.
 * Proporciona métodos para obtener, crear y eliminar comentarios asociados a eventos.
 */
@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ComentarioValidator comentarioValidator;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param comentarioRepository Repositorio para gestionar comentarios.
     * @param eventRepository Repositorio para gestionar eventos.
     * @param userRepository Repositorio para gestionar usuarios.
     * @param comentarioValidator Validador para verificar reglas de negocio de los comentarios.
     */
    public ComentarioServiceImpl(ComentarioRepository comentarioRepository,
                                 EventRepository eventRepository,
                                 UserRepository userRepository,
                                 ComentarioValidator comentarioValidator) {
        this.comentarioRepository = comentarioRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.comentarioValidator = comentarioValidator;
    }

    /**
     * Obtiene todos los comentarios asociados a un evento.
     *
     * @param eventoId ID del evento al que pertenecen los comentarios.
     * @param usuario Usuario que realiza la solicitud.
     * @return Lista de comentarios en formato DTO.
     * @throws AccesoDenegadoComentarioException Si el usuario no pertenece al grupo del evento.
     */
    @Override
    public List<ComentarioResponseDto> getAllComentariosDeEvento(UUID eventoId, User usuario) {
        Event evento = getEventoOrThrow(eventoId);
        if (evento.getGrupo() == null || evento.getGrupo().getUsuarios() == null ||
                evento.getGrupo().getUsuarios().stream().noneMatch(u -> u.getId().equals(usuario.getId()))) {
            throw new AccesoDenegadoComentarioException();
        }
        return comentarioRepository.findByEventoAndEliminadoFalse(evento)
                .stream()
                .map(ComentarioMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo comentario asociado a un evento.
     *
     * @param dto Datos del comentario a crear.
     * @return El comentario creado en formato DTO.
     * @throws AccesoDenegadoComentarioException Si el autor no pertenece al grupo del evento.
     */
    @Override
    public ComentarioResponseDto crearComentario(ComentarioRequestDto dto) {
        Event evento = getEventoOrThrow(dto.eventoId());
        User autor = getUsuarioOrThrow(dto.autorId());

        if (evento.getGrupo() == null || evento.getGrupo().getUsuarios() == null ||
                evento.getGrupo().getUsuarios().stream().noneMatch(u -> u.getId().equals(autor.getId()))) {
            throw new AccesoDenegadoComentarioException();
        }

        Comentario comentario = ComentarioMapper.toEntity(dto, evento, autor);
        comentarioRepository.save(comentario);
        return ComentarioMapper.toDto(comentario);
    }

    /**
     * Marca un comentario como eliminado.
     *
     * @param comentarioId ID del comentario a eliminar.
     * @param solicitante Usuario que realiza la solicitud de eliminación.
     * @throws AccesoDenegadoComentarioException Si el usuario no tiene permisos para eliminar el comentario.
     */
    @Transactional
    public void borrarComentario(UUID comentarioId, User solicitante) {
        Comentario comentario = comentarioValidator.validateComentarioExists(comentarioId);
        comentarioValidator.validatePermisosDeBorrado(comentario, solicitante);
        comentario.setEliminado(true);
        comentarioRepository.save(comentario);
    }

    /**
     * Obtiene un evento por su ID o lanza una excepción si no existe.
     *
     * @param eventoId ID del evento a buscar.
     * @return El evento encontrado.
     * @throws EventNotFoundException Si el evento no existe.
     */
    Event getEventoOrThrow(UUID eventoId) {
        return eventRepository.findById(eventoId)
                .orElseThrow(() -> new EventNotFoundException(eventoId));
    }

    /**
     * Obtiene un usuario por su ID o lanza una excepción si no existe.
     *
     * @param userId ID del usuario a buscar.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si el usuario no existe.
     */
    private User getUsuarioOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));
    }
}