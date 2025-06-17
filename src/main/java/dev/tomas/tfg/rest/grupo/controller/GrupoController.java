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

/**
 * Controlador REST para gestionar grupos.
 * Proporciona endpoints para realizar operaciones CRUD sobre grupos y gestionar la participación de usuarios.
 */
@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoService grupoService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param grupoService Servicio para gestionar grupos.
     * @param jwtUtil Utilidad para manejar JWT.
     */
    public GrupoController(GrupoService grupoService, JwtUtil jwtUtil) {
        this.grupoService = grupoService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Obtiene todos los grupos.
     *
     * @return Lista de grupos en formato DTO.
     */
    @GetMapping
    public ResponseEntity<List<GrupoResponseDto>> getAll() {
        return ResponseEntity.ok(grupoService.findAll());
    }

    /**
     * Obtiene un grupo por su ID.
     *
     * @param id ID del grupo a buscar.
     * @return El grupo encontrado junto con su calendario en formato DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDto> getById(
            @PathVariable UUID id
    ) {
        Grupo grupo = grupoService.getById(id);
        CalendarioResponseDto calendario = grupoService.getCalendarioDeGrupo(id);
        GrupoResponseDto response = GrupoMapper.toDto(grupo, calendario);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea un nuevo grupo.
     *
     * @param dto Datos del grupo a crear.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return El grupo creado en formato DTO.
     */
    @PostMapping
    public ResponseEntity<GrupoResponseDto> crear(
            @RequestBody GrupoRequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID creadorId = jwtUtil.extractUserUUID(authHeader);
        return ResponseEntity.ok(grupoService.crearGrupo(dto, creadorId));
    }

    /**
     * Permite a un usuario unirse a un grupo.
     *
     * @param grupoId ID del grupo al que el usuario desea unirse.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
    @PostMapping("/{grupoId}/unirse")
    public ResponseEntity<Void> unirse(
            @PathVariable UUID grupoId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = jwtUtil.extractUserUUID(authHeader);
        grupoService.unirseAGrupo(grupoId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite a un usuario salir de un grupo.
     *
     * @param grupoId ID del grupo del que el usuario desea salir.
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
    @PostMapping("/{grupoId}/salir")
    public ResponseEntity<Void> salir(
            @PathVariable UUID grupoId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = jwtUtil.extractUserUUID(authHeader);
        grupoService.salirDeGrupo(grupoId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Expulsa a un usuario de un grupo.
     *
     * @param grupoId ID del grupo del que se desea expulsar al usuario.
     * @param creadorToken Cabecera de autorización con el token JWT del creador del grupo.
     * @param userId ID del usuario a expulsar.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
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