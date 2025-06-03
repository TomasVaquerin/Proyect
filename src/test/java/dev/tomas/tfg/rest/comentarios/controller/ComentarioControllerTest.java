package dev.tomas.tfg.rest.comentarios.controller;

import dev.tomas.tfg.rest.comentarios.dto.ComentarioRequestDto;
import dev.tomas.tfg.rest.comentarios.dto.ComentarioResponseDto;
import dev.tomas.tfg.rest.comentarios.service.ComentarioService;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ComentarioController.class)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComentarioService comentarioService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAllComentariosDeEvento_returnsList() throws Exception {
        UUID eventoId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        List<ComentarioResponseDto> comentarios = List.of(
                ComentarioResponseDto.builder().id(UUID.randomUUID()).mensaje("Hola").build()
        );

        Mockito.when(jwtUtil.extractUserDto(anyString())).thenReturn(user);
        Mockito.when(comentarioService.getAllComentariosDeEvento(eq(eventoId), eq(user))).thenReturn(comentarios);

        mockMvc.perform(get("/api/grupos/{grupoId}/eventos/{eventoId}/comentarios", UUID.randomUUID(), eventoId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mensaje").value("Hola"));
    }

    @Test
    void crearComentario_returnsCreated() throws Exception {
        UUID eventoId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        ComentarioRequestDto requestDto = new ComentarioRequestDto(eventoId, user.getId(), "Mensaje");
        ComentarioResponseDto responseDto = ComentarioResponseDto.builder()
                .id(UUID.randomUUID())
                .mensaje("Mensaje")
                .build();

        Mockito.when(jwtUtil.extractUserDto(anyString())).thenReturn(user);
        Mockito.when(comentarioService.crearComentario(any(ComentarioRequestDto.class))).thenReturn(responseDto);

        String json = """
                {
                  "mensaje": "Mensaje"
                }
                """;

        mockMvc.perform(post("/api/grupos/{grupoId}/eventos/{eventoId}/comentarios", UUID.randomUUID(), eventoId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Mensaje"));
    }

    @Test
    void borrarComentario_returnsNoContent() throws Exception {
        UUID comentarioId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        Mockito.when(jwtUtil.extractUserDto(anyString())).thenReturn(user);
        Mockito.doNothing().when(comentarioService).borrarComentario(comentarioId, user);

        mockMvc.perform(delete("/api/grupos/{grupoId}/eventos/{eventoId}/comentarios/{comentarioId}",
                        UUID.randomUUID(), UUID.randomUUID(), comentarioId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }
}