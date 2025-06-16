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

@RestController
@RequestMapping("/api/calendarios")
public class CalendarioController {

    private final CalendarioService calendarioService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public CalendarioController(CalendarioService calendarioService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.calendarioService = calendarioService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<CalendarioResponseDto>> findAll() {
        return ResponseEntity.ok(calendarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarioResponseDto> findById(@PathVariable UUID id) {
        Optional<CalendarioResponseDto> calendario = calendarioService.findById(id);
        return calendario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        calendarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<CalendarioResponseDto>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(calendarioService.findByUserId(userId));
    }
}
