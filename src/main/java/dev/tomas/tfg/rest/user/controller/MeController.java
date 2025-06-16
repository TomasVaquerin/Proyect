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

@RestController
@RequestMapping("/api/users/me")
public class MeController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public MeController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> showMe(@RequestHeader("Authorization") String authHeader) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateMyAccount(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserRequestUpdateDTO dto) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        UserResponseDto updated = userService.update(UUID.fromString(user.id()), dto);
        return ResponseEntity.ok(updated);
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

    @DeleteMapping
    public ResponseEntity<Void> deleteMyAccount(@RequestHeader("Authorization") String authHeader) {
        UserResponseDto user = jwtUtil.extractUser(authHeader);
        userService.deleteById(UUID.fromString(user.id()));
        return ResponseEntity.noContent().build();
    }
}
