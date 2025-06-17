package dev.tomas.tfg.rest.calendario.controller;

import dev.tomas.tfg.rest.calendario.dto.CalendarioRequestDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.service.CalendarioService;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import dev.tomas.tfg.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador REST para gestionar los calendarios.
 * Proporciona endpoints para realizar operaciones CRUD sobre los calendarios.
 */
@RestController
@RequestMapping("/api/calendarios")
public class CalendarioController {

    private final CalendarioService calendarioService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param calendarioService Servicio para gestionar calendarios.
     * @param userRepository Repositorio para gestionar usuarios.
     * @param jwtUtil Utilidad para manejar JWT.
     */
    public CalendarioController(CalendarioService calendarioService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.calendarioService = calendarioService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Obtiene todos los calendarios.
     *
     * @return Lista de calendarios en formato DTO.
     */
    @GetMapping
    public ResponseEntity<List<CalendarioResponseDto>> findAll() {
        return ResponseEntity.ok(calendarioService.findAll());
    }

    /**
     * Obtiene un calendario por su ID.
     *
     * @param id ID del calendario.
     * @return El calendario encontrado o un estado 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalendarioResponseDto> findById(@PathVariable UUID id) {
        Optional<CalendarioResponseDto> calendario = calendarioService.findById(id);
        return calendario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo calendario.
     *
     * @param dto Datos del calendario a crear.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return El calendario creado en formato DTO.
     */
    @PostMapping
    public ResponseEntity<CalendarioResponseDto> crearCalendario(
            @RequestBody CalendarioRequestDto dto,
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        CalendarioResponseDto response = calendarioService.save(dto, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza un calendario existente.
     *
     * @param dto Datos del calendario a actualizar.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return El calendario actualizado en formato DTO.
     */
    @PutMapping("/update")
    public ResponseEntity<CalendarioResponseDto> update(
            @RequestBody CalendarioRequestDto dto,
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<CalendarioResponseDto> calendarios = calendarioService.findByUserId(user.getId());
        if (calendarios.isEmpty()) {
            throw new RuntimeException("Calendario no encontrado para el usuario");
        }
        UUID calendarioId = user.getCalendario().getId();
        System.out.println(calendarioId);
        CalendarioResponseDto updated = calendarioService.update(calendarioId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un calendario por su ID.
     *
     * @param id ID del calendario a eliminar.
     * @return Respuesta sin contenido si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        calendarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los calendarios asociados a un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de calendarios asociados al usuario.
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<CalendarioResponseDto>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(calendarioService.findByUserId(userId));
    }
}