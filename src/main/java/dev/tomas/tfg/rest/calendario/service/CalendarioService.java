package dev.tomas.tfg.rest.calendario.service;

import dev.tomas.tfg.rest.calendario.dto.CalendarioRequestDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendarioService {
    List<CalendarioResponseDto> findAll();
    Optional<CalendarioResponseDto> findById(UUID id);
    CalendarioResponseDto save(CalendarioRequestDto dto, UUID id);
    CalendarioResponseDto update(UUID id, CalendarioRequestDto dto);
    void deleteById(UUID id);

    List<CalendarioResponseDto> findByUserId(UUID userId);

}
