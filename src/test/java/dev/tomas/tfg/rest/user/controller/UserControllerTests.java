package dev.tomas.tfg.rest.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.service.UserService;
import dev.tomas.tfg.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDto userResponse;
    private UserRequestDto userRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userRequest = UserRequestDto.builder()
                .email("test@test.com")
                .nombre("Test")
                .apellidos("User")
                .fotoPerfil("url")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();
        userResponse = UserResponseDto.builder()
                .id(userId.toString())
                .email("test@test.com")
                .nombre("Test")
                .apellidos("User")
                .fotoPerfil("url")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void findAll_returnsList() throws Exception {
        Mockito.when(userService.findAll()).thenReturn(List.of(userResponse));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    void findById_found() throws Exception {
        Mockito.when(userService.findById(userId)).thenReturn(Optional.of(userResponse));
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    void findById_notFound() throws Exception {
        Mockito.when(userService.findById(userId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_createsUserAndReturnsToken() throws Exception {
        Mockito.when(userService.save(any())).thenReturn(userResponse);
        Mockito.when(jwtUtil.generateToken(anyString())).thenReturn("token123");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@test.com"))
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    void update_updatesUser() throws Exception {
        UserRequestUpdateDTO updateDTO = UserRequestUpdateDTO.builder()
                .nombre("Nuevo")
                .apellidos("Apellido")
                .fotoPerfil("url2")
                .fechaNacimiento(LocalDate.of(1999, 2, 2))
                .build();
        Mockito.when(userService.update(eq(userId), any())).thenReturn(userResponse);
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void delete_deletesUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).deleteById(userId);
    }

    @Test
    void findByEmail_found() throws Exception {
        Mockito.when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(userResponse));
        mockMvc.perform(get("/api/users/by-email")
                        .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void findByEmail_notFound() throws Exception {
        Mockito.when(userService.findByEmail("test@test.com")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/users/by-email")
                        .param("email", "test@test.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void showMe_returnsUser() throws Exception {
        Mockito.when(jwtUtil.extractUser(anyString())).thenReturn(userResponse);
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void updateMyAccount_updatesUser() throws Exception {
        UserRequestUpdateDTO updateDTO = UserRequestUpdateDTO.builder()
                .nombre("Nuevo")
                .apellidos("Apellido")
                .fotoPerfil("url2")
                .fechaNacimiento(LocalDate.of(1999, 2, 2))
                .build();
        Mockito.when(jwtUtil.extractUser(anyString())).thenReturn(userResponse);
        Mockito.when(userService.update(eq(userId), any())).thenReturn(userResponse);
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void deleteMyAccount_deletesUser() throws Exception {
        Mockito.when(jwtUtil.extractUser(anyString())).thenReturn(userResponse);
        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).deleteById(UUID.fromString(userResponse.id()));
    }

    @Test
    void subirFoto_retornaUrlFoto() throws Exception {
        String token = "Bearer token";
        UUID userId = UUID.randomUUID();
        String fotoUrl = "http://url-foto.com/foto.jpg";
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "contenido".getBytes());

        Mockito.when(jwtUtil.extractUserUUID(token)).thenReturn(userId);
        Mockito.when(userService.guardarFoto(userId, file)).thenReturn(fotoUrl);

        mockMvc.perform(multipart("/api/users/foto")
                        .file(file)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string(fotoUrl));
    }
}