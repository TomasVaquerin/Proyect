package dev.tomas.tfg.rest.comentarios.service;

import dev.tomas.tfg.rest.comentarios.dto.ComentarioRequestDto;
import dev.tomas.tfg.rest.comentarios.dto.ComentarioResponseDto;
import dev.tomas.tfg.rest.user.model.User;

import java.util.List;
import java.util.UUID;

public interface ComentarioService {
    List<ComentarioResponseDto> getAllComentariosDeEvento(UUID eventoId, User user);
    ComentarioResponseDto crearComentario(ComentarioRequestDto dto);
    void borrarComentario(UUID comentarioId, User usuarioSolicitante);
}
