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

/**
 * Controlador REST para gestionar eventos.
 * Proporciona endpoints para realizar operaciones CRUD sobre eventos y gestionar la participación de usuarios.
 */
@RestController
@RequestMapping("/api/grupos/{grupoId}/eventos")
public class EventController {

    private final EventService eventService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param eventService Servicio para gestionar eventos.
     * @param jwtUtil Utilidad para manejar JWT.
     */
    public EventController(EventService eventService, JwtUtil jwtUtil) {
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Obtiene todos los eventos asociados a un grupo.
     *
     * @param grupoId ID del grupo al que pertenecen los eventos.
     * @return Lista de eventos en formato DTO.
     */
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAll(@PathVariable UUID grupoId) {
        return ResponseEntity.ok(eventService.findAllByGrupoId(grupoId));
    }

    /**
     * Obtiene un evento por su ID.
     *
     * @param eventId ID del evento a buscar.
     * @return El evento encontrado en formato DTO.
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getById(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.obtenerEvent(eventId));
    }

    /**
     * Crea un nuevo evento asociado a un grupo.
     *
     * @param grupoId ID del grupo al que se asocia el evento.
     * @param dto Datos del evento a crear.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return El evento creado en formato DTO.
     */
    @PostMapping("/crear")
    public ResponseEntity<EventResponseDto> crearEvento(
            @PathVariable UUID grupoId,
            @Valid @RequestBody EventRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User creador = jwtUtil.extractUserDto(authHeader);
        return ResponseEntity.ok(eventService.crearEvent(grupoId, dto, creador));
    }

    /**
     * Edita un evento existente.
     *
     * @param eventId ID del evento a editar.
     * @param dto Datos actualizados del evento.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return El evento editado en formato DTO.
     */
    @PutMapping("/{eventId}/editar")
    public ResponseEntity<EventResponseDto> editarEvento(
            @PathVariable UUID eventId,
            @RequestBody EventRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        User creador = jwtUtil.extractUserDto(authHeader);
        return ResponseEntity.ok(eventService.editarEvent(eventId, dto, creador));
    }

    /**
     * Elimina un evento por su ID.
     *
     * @param eventId ID del evento a eliminar.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     */
    @DeleteMapping("/{eventId}/eliminar")
    public ResponseEntity<Void> eliminarEvento(
            @PathVariable UUID eventId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User creador = jwtUtil.extractUserDto(authHeader);
        eventService.eliminarEvent(eventId, creador);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite a un usuario unirse a un evento.
     *
     * @param eventId ID del evento al que el usuario desea unirse.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
    @PostMapping("/{eventId}/unirse")
    public ResponseEntity<Void> unirseEvento(
            @PathVariable UUID eventId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = jwtUtil.extractUserDto(authHeader);
        eventService.unirseEvent(eventId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite a un usuario salir de un evento.
     *
     * @param eventId ID del evento del que el usuario desea salir.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
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