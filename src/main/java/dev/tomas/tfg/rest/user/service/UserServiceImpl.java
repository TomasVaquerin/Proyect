package dev.tomas.tfg.rest.user.service;

import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.mapper.UserMapper;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import dev.tomas.tfg.rest.user.validator.UserValidator;
import dev.tomas.tfg.rest.storage.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio para gestionar usuarios.
 * Proporciona métodos para realizar operaciones CRUD sobre usuarios y gestionar fotos de perfil.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final FileStorageService fileStorageService;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param userRepository Repositorio para gestionar usuarios.
     * @param userMapper Mapper para convertir entre entidades y DTOs de usuarios.
     * @param fileStorageService Servicio para gestionar el almacenamiento de archivos.
     */
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userValidator = new UserValidator(userRepository);
        this.fileStorageService = fileStorageService;
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return Lista de usuarios en formato DTO.
     */
    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario a buscar.
     * @return El usuario encontrado en formato DTO, si existe.
     */
    @Override
    public Optional<UserResponseDto> findById(UUID id) {
        userValidator.validateUserExists(id);
        return userRepository.findById(id)
                .map(UserMapper::toDto);
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param dto Datos del usuario a crear.
     * @return El usuario creado en formato DTO.
     */
    @Override
    public UserResponseDto save(UserRequestDto dto) {
        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id ID del usuario a actualizar.
     * @param dto Datos actualizados del usuario.
     * @return El usuario actualizado en formato DTO.
     */
    @Override
    public UserResponseDto update(UUID id, UserRequestUpdateDTO dto) {
        User user = userValidator.validateUserExists(id);
        UserMapper.updateEntity(user, dto);
        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar.
     */
    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario a buscar.
     * @return El usuario encontrado en formato DTO, si existe.
     */
    @Override
    public Optional<UserResponseDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDto);
    }

    /**
     * Obtiene la entidad de usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario a buscar.
     * @return La entidad de usuario encontrada, si existe.
     */
    @Override
    public Optional<User> getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Guarda una foto de perfil para un usuario.
     *
     * @param userId ID del usuario.
     * @param file Archivo de la foto a guardar.
     * @return URL de la foto guardada.
     */
    @Override
    public String guardarFoto(UUID userId, MultipartFile file) {
        User user = userValidator.validateUserExists(userId);
        String fotoUrl = fileStorageService.guardarArchivo(file);
        user.setFotoPerfil(fotoUrl);
        userRepository.save(user);
        return fotoUrl;
    }
}