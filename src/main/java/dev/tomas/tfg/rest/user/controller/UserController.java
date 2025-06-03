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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id, @Valid @RequestBody UserRequestUpdateDTO dto) {
        UserResponseDto updated = userService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> findByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> showMe(@RequestHeader("Authorization") String authHeader) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyAccount(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserRequestUpdateDTO dto) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        UserResponseDto updated = userService.update(UUID.fromString(user.id()), dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@RequestHeader("Authorization") String authHeader) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        userService.deleteById(UUID.fromString(user.id()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/foto")
    public ResponseEntity<String> subirFoto(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file
    ) {
        UUID id = jwtUtil.extractUserUUID(authHeader);
        String fotoUrl = userService.guardarFoto(id, file);
        return ResponseEntity.ok(fotoUrl);
    }
}