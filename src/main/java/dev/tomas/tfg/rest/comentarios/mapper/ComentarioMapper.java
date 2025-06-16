package dev.tomas.tfg.rest.comentarios.mapper;

import dev.tomas.tfg.rest.comentarios.dto.ComentarioRequestDto;
import dev.tomas.tfg.rest.comentarios.dto.ComentarioResponseDto;
import dev.tomas.tfg.rest.comentarios.model.Comentario;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComentarioMapper {

    public static Comentario toEntity(ComentarioRequestDto dto, Event evento, User autor) {
        Comentario comentario = new Comentario();
        comentario.setId(UUID.randomUUID());
        comentario.setEvento(evento);
        comentario.setAutor(autor);
        comentario.setMensaje(dto.mensaje());
        comentario.setFechaCreacion(LocalDateTime.now());
        return comentario;
    }

    public static ComentarioResponseDto toDto(Comentario comentario) {
        return ComentarioResponseDto.builder()
                .id(comentario.getId())
                .eventoId(comentario.getEvento().getId())
                .autorId(comentario.getAutor().getId())
                .mensaje(comentario.getMensaje())
                .fechaCreacion(comentario.getFechaCreacion())
                .build();
    }
}