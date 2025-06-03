package dev.tomas.tfg.rest.calendario.controller;

import dev.tomas.tfg.rest.calendario.dto.CalendarioRequestDto;
import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.calendario.model.Calendario;
import dev.tomas.tfg.rest.calendario.service.CalendarioService;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import dev.tomas.tfg.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarioControllerTest {

    @Mock
    private CalendarioService calendarioService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CalendarioController calendarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        calendarioController = new CalendarioController(calendarioService, userRepository, jwtUtil);
    }

    @Test
    void findAll_returnsList() {
        CalendarioResponseDto dto = CalendarioResponseDto.builder().id(UUID.randomUUID()).build();
        when(calendarioService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<CalendarioResponseDto>> response = calendarioController.findAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void findById_found() {
        UUID id = UUID.randomUUID();
        CalendarioResponseDto dto = CalendarioResponseDto.builder().id(id).build();
        when(calendarioService.findById(id)).thenReturn(Optional.of(dto));

        ResponseEntity<CalendarioResponseDto> response = calendarioController.findById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(id, response.getBody().id());
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        when(calendarioService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<CalendarioResponseDto> response = calendarioController.findById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void save_ok() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String authHeader = "Bearer token";
        CalendarioRequestDto request = new CalendarioRequestDto(List.of(), List.of(), userId);

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(jwtUtil.extractEmail("token")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(calendarioService.save(request, userId)).thenReturn(
                CalendarioResponseDto.builder().id(UUID.randomUUID()).userId(userId).build()
        );

        ResponseEntity<CalendarioResponseDto> response = calendarioController.crearCalendario(request, authHeader);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void update_ok() {
        UUID calendarioId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String authHeader = "Bearer token";
        CalendarioRequestDto request = new CalendarioRequestDto(List.of(), List.of(), userId);
        CalendarioResponseDto responseDto = CalendarioResponseDto.builder().id(calendarioId).build();

        User user = new User();
        user.setId(userId);
        Calendario calendario = new Calendario();
        calendario.setId(calendarioId);
        user.setCalendario(calendario);

        when(jwtUtil.extractEmail("token")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(calendarioService.findByUserId(userId)).thenReturn(List.of(responseDto));
        when(calendarioService.update(calendarioId, request)).thenReturn(responseDto);

        ResponseEntity<CalendarioResponseDto> response = calendarioController.update(request, authHeader);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(calendarioId, response.getBody().id());
    }

    @Test
    void deleteById_ok() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = calendarioController.delete(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(calendarioService).deleteById(id);
    }

    @Test
    void findByUserId_returnsList() {
        UUID userId = UUID.randomUUID();
        CalendarioResponseDto dto = CalendarioResponseDto.builder().id(UUID.randomUUID()).build();
        when(calendarioService.findByUserId(userId)).thenReturn(List.of(dto));

        ResponseEntity<List<CalendarioResponseDto>> response = calendarioController.findByUserId(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}