package dev.tomas.tfg.rest.comentarios.controller;

import dev.tomas.tfg.rest.comentarios.dto.ComentarioRequestDto;
import dev.tomas.tfg.rest.comentarios.dto.ComentarioResponseDto;
import dev.tomas.tfg.rest.comentarios.service.ComentarioService;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar los comentarios de eventos.
 * Proporciona endpoints para obtener, crear y eliminar comentarios asociados a un evento.
 */
@RestController
@RequestMapping("/api/grupos/{grupoId}/eventos/{eventoId}/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param comentarioService Servicio para gestionar comentarios.
     * @param jwtUtil Utilidad para manejar JWT.
     */
    public ComentarioController(ComentarioService comentarioService, JwtUtil jwtUtil) {
        this.comentarioService = comentarioService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Obtiene todos los comentarios asociados a un evento.
     *
     * @param eventoId ID del evento al que pertenecen los comentarios.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Lista de comentarios en formato DTO.
     */
    @GetMapping
    public ResponseEntity<List<ComentarioResponseDto>> getAllComentariosDeEvento(
            @PathVariable UUID eventoId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = jwtUtil.extractUserDto(authHeader);
        return ResponseEntity.ok(comentarioService.getAllComentariosDeEvento(eventoId, user));
    }

    /**
     * Crea un nuevo comentario asociado a un evento.
     *
     * @param eventoId ID del evento al que se asocia el comentario.
     * @param dto Datos del comentario a crear.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return El comentario creado en formato DTO.
     */
    @PostMapping
    public ResponseEntity<ComentarioResponseDto> crearComentario(
            @PathVariable UUID eventoId,
            @RequestBody ComentarioRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User autor = jwtUtil.extractUserDto(authHeader);
        ComentarioRequestDto dtoConAutor = new ComentarioRequestDto(
                eventoId,
                autor.getId(),
                dto.mensaje()
        );
        return ResponseEntity.ok(comentarioService.crearComentario(dtoConAutor));
    }

    /**
     * Elimina un comentario por su ID.
     *
     * @param comentarioId ID del comentario a eliminar.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     */
    @DeleteMapping("/{comentarioId}")
    public ResponseEntity<Void> borrarComentario(
            @PathVariable UUID comentarioId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User solicitante = jwtUtil.extractUserDto(authHeader);
        comentarioService.borrarComentario(comentarioId, solicitante);
        return ResponseEntity.noContent().build();
    }
}