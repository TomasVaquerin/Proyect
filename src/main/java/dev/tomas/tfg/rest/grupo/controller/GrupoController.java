package dev.tomas.tfg.rest.grupo.controller;

import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoRequestDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
import dev.tomas.tfg.rest.grupo.mapper.GrupoMapper;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.grupo.service.GrupoService;
import dev.tomas.tfg.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoService grupoService;
    private final JwtUtil jwtUtil;

    public GrupoController(GrupoService grupoService, JwtUtil jwtUtil) {
        this.grupoService = grupoService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<GrupoResponseDto>> getAll() {
        return ResponseEntity.ok(grupoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDto> getById(
            @PathVariable UUID id
    ) {
        Grupo grupo = grupoService.getById(id);
        CalendarioResponseDto calendario = grupoService.getCalendarioDeGrupo(id);
        GrupoResponseDto response = GrupoMapper.toDto(grupo, calendario);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDto> crear(
            @RequestBody GrupoRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID creadorId = jwtUtil.extractUserUUID(authHeader);
        return ResponseEntity.ok(grupoService.crearGrupo(dto, creadorId));
    }

    @PostMapping("/{grupoId}/unirse")
    public ResponseEntity<Void> unirse(
            @PathVariable UUID grupoId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = jwtUtil.extractUserUUID(authHeader);
        grupoService.unirseAGrupo(grupoId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{grupoId}/salir")
    public ResponseEntity<Void> salir(
            @PathVariable UUID grupoId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = jwtUtil.extractUserUUID(authHeader);
        grupoService.salirDeGrupo(grupoId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{grupoId}/expulsar/{userId}")
    public ResponseEntity<Void> expulsar(
            @PathVariable UUID grupoId,
            @RequestHeader("Authorization") String creadorToken,
            @PathVariable UUID userId
    ) {
        UUID creadorId = jwtUtil.extractUserUUID(creadorToken);
        grupoService.expulsarUsuario(grupoId, creadorId, userId);
        return ResponseEntity.noContent().build();
    }

}