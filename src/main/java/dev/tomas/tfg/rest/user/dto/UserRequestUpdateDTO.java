package dev.tomas.tfg.rest.user.dto;

import dev.tomas.tfg.rest.calendario.dto.CalendarioRequestDto;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserRequestUpdateDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotBlank(message = "Los apellidos no pueden estar vacíos")
        String apellidos,

        @NotBlank(message = "La URL de la foto de perfil no puede estar vacía")
        String fotoPerfil,

        @NotNull(message = "NO puedes poner una fecha de nacimiento nula")
        LocalDate fechaNacimiento,

        CalendarioRequestDto calendario
){}