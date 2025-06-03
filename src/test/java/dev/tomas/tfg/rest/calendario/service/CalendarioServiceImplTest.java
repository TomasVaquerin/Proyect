package dev.tomas.tfg.rest.calendario.service;

import dev.tomas.tfg.rest.calendario.dto.CalendarioRequestDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.exceptions.CalendarioNotFoundException;
import dev.tomas.tfg.rest.calendario.mapper.CalendarioMapper;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import dev.tomas.tfg.rest.calendario.repository.CalendarioRepository;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarioServiceImplTest {

    @Mock
    private CalendarioRepository calendarioRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CalendarioServiceImpl calendarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_returnsList() {
        Calendario calendario = new Calendario();
        calendario.setId(UUID.randomUUID());
        when(calendarioRepository.findAll()).thenReturn(List.of(calendario));
        try (MockedStatic<CalendarioMapper> mocked = mockStatic(CalendarioMapper.class)) {
            mocked.when(() -> CalendarioMapper.toDto(any(Calendario.class)))
                    .thenReturn(CalendarioResponseDto.builder().id(calendario.getId()).build());
            List<CalendarioResponseDto> result = calendarioService.findAll();
            assertEquals(1, result.size());
        }
    }

    @Test
    void findById_found() {
        UUID id = UUID.randomUUID();
        Calendario calendario = new Calendario();
        calendario.setId(id);
        when(calendarioRepository.findById(id)).thenReturn(Optional.of(calendario));
        try (MockedStatic<CalendarioMapper> mocked = mockStatic(CalendarioMapper.class)) {
            mocked.when(() -> CalendarioMapper.toDto(calendario))
                    .thenReturn(CalendarioResponseDto.builder().id(id).build());
            Optional<CalendarioResponseDto> result = calendarioService.findById(id);
            assertTrue(result.isPresent());
            assertEquals(id, result.get().id());
        }
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        when(calendarioRepository.findById(id)).thenReturn(Optional.empty());
        Optional<CalendarioResponseDto> result = calendarioService.findById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_ok() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        CalendarioRequestDto dto = new CalendarioRequestDto(List.of(), List.of(), userId);
        Calendario calendario = new Calendario();
        calendario.setId(UUID.randomUUID());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        try (MockedStatic<CalendarioMapper> mocked = mockStatic(CalendarioMapper.class)) {
            mocked.when(() -> CalendarioMapper.toEntity(dto, user)).thenReturn(calendario);
            when(calendarioRepository.save(calendario)).thenReturn(calendario);
            mocked.when(() -> CalendarioMapper.toDto(calendario))
                    .thenReturn(CalendarioResponseDto.builder().id(calendario.getId()).build());
            CalendarioResponseDto result = calendarioService.save(dto, userId);
            assertNotNull(result);
            assertEquals(calendario.getId(), result.id());
        }
    }

    @Test
    void save_userNotFound_throws() {
        UUID userId = UUID.randomUUID();
        CalendarioRequestDto dto = new CalendarioRequestDto(List.of(), List.of(), userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(CalendarioNotFoundException.class, () -> calendarioService.save(dto, userId));
    }

    @Test
    void update_ok() {
        UUID calendarioId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Calendario calendario = new Calendario();
        calendario.setId(calendarioId);
        User user = new User();
        user.setId(userId);
        CalendarioRequestDto dto = new CalendarioRequestDto(List.of(), List.of(), userId);
        when(calendarioRepository.findById(calendarioId)).thenReturn(Optional.of(calendario));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(calendarioRepository.save(any())).thenReturn(calendario);
        try (MockedStatic<CalendarioMapper> mocked = mockStatic(CalendarioMapper.class)) {
            mocked.when(() -> CalendarioMapper.toDto(calendario))
                    .thenReturn(CalendarioResponseDto.builder().id(calendarioId).build());
            CalendarioResponseDto result = calendarioService.update(calendarioId, dto);
            assertNotNull(result);
            assertEquals(calendarioId, result.id());
        }
    }

    @Test
    void update_notFound_throws() {
        UUID calendarioId = UUID.randomUUID();
        CalendarioRequestDto dto = new CalendarioRequestDto(List.of(), List.of(), UUID.randomUUID());
        when(calendarioRepository.findById(calendarioId)).thenReturn(Optional.empty());
        assertThrows(CalendarioNotFoundException.class, () -> calendarioService.update(calendarioId, dto));
    }

    @Test
    void deleteById_ok() {
        UUID id = UUID.randomUUID();
        when(calendarioRepository.existsById(id)).thenReturn(true);
        calendarioService.deleteById(id);
        verify(calendarioRepository).deleteById(id);
    }

    @Test
    void deleteById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(calendarioRepository.existsById(id)).thenReturn(false);
        assertThrows(CalendarioNotFoundException.class, () -> calendarioService.deleteById(id));
    }

    @Test
    void findByUserId_returnsList() {
        UUID userId = UUID.randomUUID();
        Calendario calendario = new Calendario();
        User user = new User();
        user.setId(userId);
        calendario.setUser(user);
        when(calendarioRepository.findAll()).thenReturn(List.of(calendario));
        try (MockedStatic<CalendarioMapper> mocked = mockStatic(CalendarioMapper.class)) {
            mocked.when(() -> CalendarioMapper.toDto(calendario))
                    .thenReturn(CalendarioResponseDto.builder().id(calendario.getId()).build());
            List<CalendarioResponseDto> result = calendarioService.findByUserId(userId);
            assertEquals(1, result.size());
        }
    }
}