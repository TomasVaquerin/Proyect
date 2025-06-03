package dev.tomas.tfg.rest.calendario.service;

import dev.tomas.tfg.rest.calendario.dto.CalendarioRequestDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.exceptions.CalendarioNotFoundException;
import dev.tomas.tfg.rest.calendario.mapper.CalendarioMapper;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import dev.tomas.tfg.rest.calendario.repository.CalendarioRepository;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CalendarioServiceImpl implements CalendarioService {

    private final CalendarioRepository calendarioRepository;
    private final UserRepository userRepository;

    public CalendarioServiceImpl(CalendarioRepository calendarioRepository,
                                 UserRepository userRepository) {
        this.calendarioRepository = calendarioRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CalendarioResponseDto> findAll() {
        return calendarioRepository.findAll()
                .stream()
                .map(CalendarioMapper::toDto)
                .toList();
    }

    @Override
    public Optional<CalendarioResponseDto> findById(UUID id) {
        return calendarioRepository.findById(id)
                .map(CalendarioMapper::toDto);
    }

    @Override
    public CalendarioResponseDto save(CalendarioRequestDto dto, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CalendarioNotFoundException("Usuario no encontrado"));

        Calendario calendario = CalendarioMapper.toEntity(dto, user);
        Calendario saved = calendarioRepository.save(calendario);
        return CalendarioMapper.toDto(saved);
    }

    @Override
    public CalendarioResponseDto update(UUID id, CalendarioRequestDto dto) {
        Calendario calendario = calendarioRepository.findById(id)
                .orElseThrow(() -> new CalendarioNotFoundException("Calendario no encontrado"));

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new CalendarioNotFoundException("Usuario no encontrado"));

        calendario.setUser(user);
        calendario.setBloquesRecurrentes(
                dto.bloquesRecurrentes() != null ?
                        dto.bloquesRecurrentes().stream()
                                .map(CalendarioMapper::toEntity)
                                .toList()
                        : List.of()
        );
        calendario.setExcepciones(
                dto.excepciones() != null ?
                        dto.excepciones().stream()
                                .map(CalendarioMapper::toEntity)
                                .toList()
                        : List.of()
        );

        Calendario updated = calendarioRepository.save(calendario);
        return CalendarioMapper.toDto(updated);
    }

    @Override
    public void deleteById(UUID id) {
        if (!calendarioRepository.existsById(id)) {
            throw new CalendarioNotFoundException("Calendario no encontrado");
        }
        calendarioRepository.deleteById(id);
    }

    @Override
    public List<CalendarioResponseDto> findByUserId(UUID userId) {
        return calendarioRepository.findAll().stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(userId))
                .map(CalendarioMapper::toDto)
                .toList();
    }
}