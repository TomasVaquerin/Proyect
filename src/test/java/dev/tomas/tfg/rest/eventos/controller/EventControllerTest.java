package dev.tomas.tfg.rest.eventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.dto.EventResponseDto;
import dev.tomas.tfg.rest.eventos.service.EventService;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest {

    @Mock
    private EventService eventService;
    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        EventController controller = new EventController(eventService, jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll_returnsList() throws Exception {
        UUID grupoId = UUID.randomUUID();
        EventResponseDto dto = EventResponseDto.builder().id("1").build();
        when(eventService.findAllByGrupoId(grupoId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/grupos/{grupoId}/eventos", grupoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void getById_returnsEvent() throws Exception {
        UUID grupoId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        EventResponseDto dto = EventResponseDto.builder().id(eventId.toString()).build();
        when(eventService.obtenerEvent(eventId)).thenReturn(dto);

        mockMvc.perform(get("/api/grupos/{grupoId}/eventos/{eventId}", grupoId, eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()));
    }

    @Test
    void crearEvento_returnsCreated() throws Exception {
        UUID grupoId = UUID.randomUUID();
        String token = "Bearer testtoken";
        User creador = new User();
        EventResponseDto resp = EventResponseDto.builder().id("1").build();

        EventRequestDto req = EventRequestDto.builder()
                .nombre("Evento")
                .ubicacion("Aula 1")
                .tipo("Reunión")
                .fecha(LocalDate.now())
                .horas(List.of(LocalTime.of(10, 0)))
                .build();

        when(jwtUtil.extractUserDto(token)).thenReturn(creador);
        when(eventService.crearEvent(eq(grupoId), any(EventRequestDto.class), eq(creador))).thenReturn(resp);

        mockMvc.perform(post("/api/grupos/{grupoId}/eventos/crear", grupoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void editarEvento_returnsEdited() throws Exception {
        UUID grupoId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String token = "Bearer testtoken";
        EventRequestDto req = EventRequestDto.builder().nombre("Editado").build();
        User creador = new User();
        EventResponseDto resp = EventResponseDto.builder().id(eventId.toString()).build();

        when(jwtUtil.extractUserDto(token)).thenReturn(creador);
        when(eventService.editarEvent(eq(eventId), any(EventRequestDto.class), eq(creador))).thenReturn(resp);

        mockMvc.perform(put("/api/grupos/{grupoId}/eventos/{eventId}/editar", grupoId, eventId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()));
    }

    @Test
    void eliminarEvento_returnsNoContent() throws Exception {
        UUID grupoId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String token = "Bearer testtoken";
        User creador = new User();

        when(jwtUtil.extractUserDto(token)).thenReturn(creador);
        doNothing().when(eventService).eliminarEvent(eventId, creador);

        mockMvc.perform(delete("/api/grupos/{grupoId}/eventos/{eventId}/eliminar", grupoId, eventId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void unirseEvento_returnsNoContent() throws Exception {
        UUID grupoId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String token = "Bearer testtoken";
        User user = new User();

        when(jwtUtil.extractUserDto(token)).thenReturn(user);
        doNothing().when(eventService).unirseEvent(eventId, user);

        mockMvc.perform(post("/api/grupos/{grupoId}/eventos/{eventId}/unirse", grupoId, eventId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void salirEvento_returnsNoContent() throws Exception {
        UUID grupoId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String token = "Bearer testtoken";
        User user = new User();

        when(jwtUtil.extractUserDto(token)).thenReturn(user);
        doNothing().when(eventService).salirDelEvent(eventId, user);

        mockMvc.perform(post("/api/grupos/{grupoId}/eventos/{eventId}/salir", grupoId, eventId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }
}