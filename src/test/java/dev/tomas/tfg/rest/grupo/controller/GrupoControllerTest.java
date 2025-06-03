package dev.tomas.tfg.rest.grupo.controller;

import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoRequestDto;
import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.grupo.service.GrupoService;
import dev.tomas.tfg.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GrupoController.class)
@AutoConfigureMockMvc(addFilters = false)
class GrupoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrupoService grupoService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAll_returnsList() throws Exception {
        GrupoResponseDto dto = GrupoResponseDto.builder().id("1").nombre("Grupo").build();
        when(grupoService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/grupos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Grupo"));
    }

    @Test
    void getById_returnsGrupo() throws Exception {
        UUID grupoId = UUID.randomUUID();
        Grupo grupo = new Grupo();
        grupo.setId(grupoId);
        grupo.setNombre("Grupo");
        CalendarioResponseDto calendario = CalendarioResponseDto.builder().build();
        GrupoResponseDto response = GrupoResponseDto.builder().id(grupoId.toString()).nombre("Grupo").build();

        when(grupoService.getById(grupoId)).thenReturn(grupo);
        when(grupoService.getCalendarioDeGrupo(grupoId)).thenReturn(calendario);
        Mockito.mockStatic(dev.tomas.tfg.rest.grupo.mapper.GrupoMapper.class)
                .when(() -> dev.tomas.tfg.rest.grupo.mapper.GrupoMapper.toDto(grupo, calendario))
                .thenReturn(response);

        mockMvc.perform(get("/api/grupos/" + grupoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Grupo"));
    }

    @Test
    void crear_returnsCreatedGrupo() throws Exception {
        String token = "Bearer testtoken";
        UUID creadorId = UUID.randomUUID();
        GrupoRequestDto dto = new GrupoRequestDto("Grupo", "desc");
        GrupoResponseDto response = GrupoResponseDto.builder().id("1").nombre("Grupo").build();

        when(jwtUtil.extractUserUUID(token)).thenReturn(creadorId);
        when(grupoService.crearGrupo(any(), eq(creadorId))).thenReturn(response);

        mockMvc.perform(post("/api/grupos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Grupo\",\"desc\":\"desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Grupo"));
    }

    @Test
    void unirse_returnsNoContent() throws Exception {
        String token = "Bearer testtoken";
        UUID grupoId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(jwtUtil.extractUserUUID(token)).thenReturn(userId);

        mockMvc.perform(post("/api/grupos/" + grupoId + "/unirse")
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        Mockito.verify(grupoService).unirseAGrupo(grupoId, userId);
    }

    @Test
    void salir_returnsNoContent() throws Exception {
        String token = "Bearer testtoken";
        UUID grupoId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(jwtUtil.extractUserUUID(token)).thenReturn(userId);

        mockMvc.perform(post("/api/grupos/" + grupoId + "/salir")
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        Mockito.verify(grupoService).salirDeGrupo(grupoId, userId);
    }

    @Test
    void expulsar_returnsNoContent() throws Exception {
        String token = "Bearer testtoken";
        UUID grupoId = UUID.randomUUID();
        UUID creadorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(jwtUtil.extractUserUUID(token)).thenReturn(creadorId);

        mockMvc.perform(delete("/api/grupos/" + grupoId + "/expulsar/" + userId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        Mockito.verify(grupoService).expulsarUsuario(grupoId, creadorId, userId);
    }
}