package dev.tomas.tfg.rest.grupo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GrupoUpdateDto(
        @NotBlank(message = "El nombre del grupo no puede estar vacío")
        String nombre,
        @NotBlank(message = "La descripción del grupo no puede estar vacía")
        String descripcion
) {}