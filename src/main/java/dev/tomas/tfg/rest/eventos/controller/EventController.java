package dev.tomas.tfg.rest.eventos.controller;

import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.dto.EventResponseDto;
import dev.tomas.tfg.rest.eventos.service.EventService;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grupos/{grupoId}/eventos")
public class EventController {

    private final EventService eventService;
    private final JwtUtil jwtUtil;

    public EventController(EventService eventService, JwtUtil jwtUtil) {
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAll(@PathVariable UUID grupoId) {
        return ResponseEntity.ok(eventService.findAllByGrupoId(grupoId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getById(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.obtenerEvent(eventId));
    }

    @PostMapping("/crear")
    public ResponseEntity<EventResponseDto> crearEvento(
            @PathVariable UUID grupoId,
            @Valid @RequestBody EventRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User creador = jwtUtil.extractUserDto(authHeader);
        return ResponseEntity.ok(eventService.crearEvent(grupoId, dto, creador));
    }

    @PutMapping("/{eventId}/editar")
    public ResponseEntity<EventResponseDto> editarEvento(
            @PathVariable UUID eventId,
            @RequestBody EventRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User creador = jwtUtil.extractUserDto(authHeader);
        return ResponseEntity.ok(eventService.editarEvent(eventId, dto, creador));
    }

    @DeleteMapping("/{eventId}/eliminar")
    public ResponseEntity<Void> eliminarEvento(
            @PathVariable UUID eventId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User creador = jwtUtil.extractUserDto(authHeader);
        eventService.eliminarEvent(eventId, creador);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/unirse")
    public ResponseEntity<Void> unirseEvento(
            @PathVariable UUID eventId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = jwtUtil.extractUserDto(authHeader);
        eventService.unirseEvent(eventId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/salir")
    public ResponseEntity<Void> salirEvento(
            @PathVariable UUID eventId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = jwtUtil.extractUserDto(authHeader);
        eventService.salirDelEvent(eventId, user);
        return ResponseEntity.noContent().build();
    }
}