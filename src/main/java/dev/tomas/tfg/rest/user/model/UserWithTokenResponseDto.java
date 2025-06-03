package dev.tomas.tfg.rest.user.model;

import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import lombok.Builder;

@Builder
public record UserWithTokenResponseDto(
        UserResponseDto user,
        String token
) {}