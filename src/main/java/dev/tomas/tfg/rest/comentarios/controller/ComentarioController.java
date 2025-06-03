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

@RestController
@RequestMapping("/api/grupos/{grupoId}/eventos/{eventoId}/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final JwtUtil jwtUtil;

    public ComentarioController(ComentarioService comentarioService, JwtUtil jwtUtil) {
        this.comentarioService = comentarioService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponseDto>> getAllComentariosDeEvento(
            @PathVariable UUID eventoId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = jwtUtil.extractUserDto(authHeader);
        return ResponseEntity.ok(comentarioService.getAllComentariosDeEvento(eventoId, user));
    }

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