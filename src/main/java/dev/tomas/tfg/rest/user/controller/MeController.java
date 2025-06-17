package dev.tomas.tfg.rest.user.controller;

import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.service.UserService;
import dev.tomas.tfg.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controlador REST para gestionar las operaciones relacionadas con el usuario autenticado.
 * Proporciona endpoints para obtener información del usuario, actualizar su cuenta, subir una foto y eliminar la cuenta.
 */
@RestController
@RequestMapping("/api/users/me")
public class MeController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param userService Servicio para gestionar usuarios.
     * @param jwtUtil Utilidad para manejar JWT.
     */
    public MeController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Obtiene la información del usuario autenticado.
     *
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Información del usuario en formato DTO.
     */
    @GetMapping
    public ResponseEntity<UserResponseDto> showMe(@RequestHeader("Authorization") String authHeader) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        return ResponseEntity.ok(user);
    }

    /**
     * Actualiza la información de la cuenta del usuario autenticado.
     *
     * @param authHeader Cabecera de autorización con el token JWT.
     * @param dto Datos actualizados del usuario.
     * @return Información actualizada del usuario en formato DTO.
     */
    @PutMapping
    public ResponseEntity<UserResponseDto> updateMyAccount(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserRequestUpdateDTO dto) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        UserResponseDto updated = userService.update(UUID.fromString(user.id()), dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Permite al usuario autenticado subir una foto de perfil.
     *
     * @param authHeader Cabecera de autorización con el token JWT.
     * @param file Archivo de la foto a subir.
     * @return URL de la foto subida.
     */
    @PostMapping("/foto")
    public ResponseEntity<String> subirFoto(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file
    ) {
        UUID id = jwtUtil.extractUserUUID(authHeader);
        String fotoUrl = userService.guardarFoto(id, file);
        return ResponseEntity.ok(fotoUrl);
    }

    /**
     * Elimina la cuenta del usuario autenticado.
     *
     * @param authHeader Cabecera de autorización con el token JWT.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMyAccount(@RequestHeader("Authorization") String authHeader) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        userService.deleteById(UUID.fromString(user.id()));
        return ResponseEntity.noContent().build();
    }
}