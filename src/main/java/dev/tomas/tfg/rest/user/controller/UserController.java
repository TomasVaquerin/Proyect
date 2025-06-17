package dev.tomas.tfg.rest.user.controller;

import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.model.UserWithTokenResponseDto;
import dev.tomas.tfg.rest.user.service.UserService;
import dev.tomas.tfg.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar usuarios.
 * Proporciona endpoints para realizar operaciones CRUD sobre usuarios.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param userService Servicio para gestionar usuarios.
     * @param jwtUtil Utilidad para manejar JWT.
     */
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return Lista de usuarios en formato DTO.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario a buscar.
     * @return El usuario encontrado en formato DTO o una respuesta 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param dto Datos del usuario a crear.
     * @return El usuario creado junto con un token JWT en formato DTO.
     */
    @PostMapping
    public ResponseEntity<UserWithTokenResponseDto> save(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto created = userService.save(dto);
        String token = jwtUtil.generateToken(created.email());
        UserWithTokenResponseDto response = UserWithTokenResponseDto.builder()
                .user(created)
                .token(token)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id ID del usuario a actualizar.
     * @param dto Datos actualizados del usuario.
     * @return El usuario actualizado en formato DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id, @Valid @RequestBody UserRequestUpdateDTO dto) {
        UserResponseDto updated = userService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar.
     * @return Respuesta sin contenido si la operación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario a buscar.
     * @return El usuario encontrado en formato DTO o una respuesta 404 si no se encuentra.
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> findByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}