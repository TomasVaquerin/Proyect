package dev.tomas.tfg.rest.user.service;

import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.mapper.UserMapper;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import dev.tomas.tfg.storage.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, new UserMapper(), fileStorageService);

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("user1@test.com");
        user1.setNombre("User1");
        user1.setApellidos("Uno");
        user1.setFotoPerfil("url1");
        user1.setFechaNacimiento(LocalDate.of(2000, 1, 1));

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@test.com");
        user2.setNombre("User2");
        user2.setApellidos("Dos");
        user2.setFotoPerfil("url2");
        user2.setFechaNacimiento(LocalDate.of(2001, 2, 2));
    }

    @Test
    void findAll_returnsUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponseDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).email());
        assertEquals("user2@test.com", result.get(1).email());
    }

    @Test
    void findById_returnsUser() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        Optional<UserResponseDto> result = userService.findById(user1.getId());

        assertTrue(result.isPresent());
        assertEquals(user1.getId().toString(), result.get().id());
    }

    @Test
    void save_createsUser() {
        UserRequestDto dto = UserRequestDto.builder()
                .email("nuevo@test.com")
                .nombre("Nuevo")
                .apellidos("Usuario")
                .fotoPerfil("url3")
                .fechaNacimiento(LocalDate.of(1999, 3, 3))
                .build();
        User user = UserMapper.toEntity(dto);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.save(dto);

        assertEquals(dto.email(), result.email());
        assertEquals(dto.nombre(), result.nombre());
    }

    @Test
    void update_updatesUser() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserRequestUpdateDTO dto = UserRequestUpdateDTO.builder()
                .nombre("Modificado")
                .apellidos("ApellidoMod")
                .fotoPerfil("urlMod")
                .fechaNacimiento(LocalDate.of(1998, 4, 4))
                .build();

        UserResponseDto result = userService.update(user1.getId(), dto);

        assertEquals("Modificado", result.nombre());
        assertEquals("ApellidoMod", result.apellidos());
    }

    @Test
    void deleteById_deletesUser() {
        userService.deleteById(user2.getId());
        verify(userRepository).deleteById(user2.getId());
    }

    @Test
    void findByEmail_returnsUser() {
        when(userRepository.findByEmail("user1@test.com")).thenReturn(Optional.of(user1));

        Optional<UserResponseDto> result = userService.findByEmail("user1@test.com");

        assertTrue(result.isPresent());
        assertEquals("user1@test.com", result.get().email());
    }

    @Test
    void getUserEntityByEmail_returnsUserEntity() {
        when(userRepository.findByEmail("user2@test.com")).thenReturn(Optional.of(user2));

        Optional<User> result = userService.getUserEntityByEmail("user2@test.com");

        assertTrue(result.isPresent());
        assertEquals("user2@test.com", result.get().getEmail());
    }

    @Test
    void guardarFoto_guardaFotoYActualizaUsuario() {
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(fileStorageService.guardarArchivo(file)).thenReturn("url/foto.jpg");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        String url = userService.guardarFoto(user1.getId(), file);

        assertEquals("url/foto.jpg", url);
        assertEquals("url/foto.jpg", user1.getFotoPerfil());
    }

    @Test
    void findById_usuarioNoExiste_lanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findById(idInexistente));
    }

    @Test
    void update_usuarioNoExiste_lanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        UserRequestUpdateDTO dto = UserRequestUpdateDTO.builder()
                .nombre("Nombre")
                .apellidos("Apellido")
                .fotoPerfil("url")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(RuntimeException.class, () -> userService.update(idInexistente, dto));
    }

    @Test
    void guardarFoto_usuarioNoExiste_lanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(RuntimeException.class, () -> userService.guardarFoto(idInexistente, file));
    }
}