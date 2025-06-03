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

@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ComentarioValidator comentarioValidator;

    public ComentarioServiceImpl(ComentarioRepository comentarioRepository,
                                 EventRepository eventRepository,
                                 UserRepository userRepository,
                                 ComentarioValidator comentarioValidator) {
        this.comentarioRepository = comentarioRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.comentarioValidator = comentarioValidator;
    }

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

    @Transactional
    public void borrarComentario(UUID comentarioId, User solicitante) {
        Comentario comentario = comentarioValidator.validateComentarioExists(comentarioId);
        comentarioValidator.validatePermisosDeBorrado(comentario, solicitante);
        comentario.setEliminado(true);
        comentarioRepository.save(comentario);
    }


    Event getEventoOrThrow(UUID eventoId) {
        return eventRepository.findById(eventoId)
                .orElseThrow(() -> new EventNotFoundException(eventoId));
    }

    private User getUsuarioOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));
    }
}
