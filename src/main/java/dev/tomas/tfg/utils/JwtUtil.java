package dev.tomas.tfg.utils;

import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.exceptions.UserNotFoundException;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs = 86400000; // 1 día
    private final UserService userService;

    public JwtUtil(@Value("${jwt.secret}") String secret, UserService userService) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        this.userService = userService;
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public UserResponseDto extractUser(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = extractEmail(token);
            return userService.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));
        }
        throw new RuntimeException("Token no proporcionado o inválido");
    }

    public User extractUserDto(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = extractEmail(token);
            return userService.getUserEntityByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));
        }
        throw new RuntimeException("Token no proporcionado o inválido");
    }

    public UUID extractUserUUID(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = extractEmail(token);
            return userService.findByEmail(email)
                    .map(user -> UUID.fromString(user.id()))
                    .orElseThrow(() -> new UserNotFoundException(email));
        }
        throw new RuntimeException("Token no proporcionado o inválido");
    }
}