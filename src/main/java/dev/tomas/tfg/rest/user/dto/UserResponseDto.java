package dev.tomas.tfg.rest.user.dto;

import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import lombok.Builder;
import org.hibernate.engine.spi.LoadQueryInfluencers;

import java.time.LocalDate;

@Builder
public record UserResponseDto (
    String id,
    String email,
    String nombre,
    String apellidos,
    String fotoPerfil,
    LocalDate fechaNacimiento,
    CalendarioResponseDto calendario
){}