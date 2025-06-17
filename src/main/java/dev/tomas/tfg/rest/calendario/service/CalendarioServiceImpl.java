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

/**
 * Implementación del servicio para gestionar calendarios.
 * Proporciona métodos para realizar operaciones CRUD sobre los calendarios.
 */
@Service
public class CalendarioServiceImpl implements CalendarioService {

    private final CalendarioRepository calendarioRepository;
    private final UserRepository userRepository;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param calendarioRepository Repositorio para gestionar calendarios.
     * @param userRepository Repositorio para gestionar usuarios.
     */
    public CalendarioServiceImpl(CalendarioRepository calendarioRepository,
                                 UserRepository userRepository) {
        this.calendarioRepository = calendarioRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene todos los calendarios.
     *
     * @return Lista de calendarios en formato DTO.
     */
    @Override
    public List<CalendarioResponseDto> findAll() {
        return calendarioRepository.findAll()
                .stream()
                .map(CalendarioMapper::toDto)
                .toList();
    }

    /**
     * Busca un calendario por su ID.
     *
     * @param id ID del calendario.
     * @return Un Optional con el calendario encontrado en formato DTO, o vacío si no existe.
     */
    @Override
    public Optional<CalendarioResponseDto> findById(UUID id) {
        return calendarioRepository.findById(id)
                .map(CalendarioMapper::toDto);
    }

    /**
     * Crea y guarda un nuevo calendario.
     *
     * @param dto Datos del calendario a crear.
     * @param id ID del usuario asociado al calendario.
     * @return El calendario creado en formato DTO.
     * @throws CalendarioNotFoundException Si el usuario no existe.
     */
    @Override
    public CalendarioResponseDto save(CalendarioRequestDto dto, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CalendarioNotFoundException("Usuario no encontrado"));

        Calendario calendario = CalendarioMapper.toEntity(dto, user);
        Calendario saved = calendarioRepository.save(calendario);
        return CalendarioMapper.toDto(saved);
    }

    /**
     * Actualiza un calendario existente.
     *
     * @param id ID del calendario a actualizar.
     * @param dto Datos actualizados del calendario.
     * @return El calendario actualizado en formato DTO.
     * @throws CalendarioNotFoundException Si el calendario o el usuario no existen.
     */
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

    /**
     * Elimina un calendario por su ID.
     *
     * @param id ID del calendario a eliminar.
     * @throws CalendarioNotFoundException Si el calendario no existe.
     */
    @Override
    public void deleteById(UUID id) {
        if (!calendarioRepository.existsById(id)) {
            throw new CalendarioNotFoundException("Calendario no encontrado");
        }
        calendarioRepository.deleteById(id);
    }

    /**
     * Busca todos los calendarios asociados a un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de calendarios asociados al usuario en formato DTO.
     */
    @Override
    public List<CalendarioResponseDto> findByUserId(UUID userId) {
        return calendarioRepository.findAll().stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(userId))
                .map(CalendarioMapper::toDto)
                .toList();
    }
}