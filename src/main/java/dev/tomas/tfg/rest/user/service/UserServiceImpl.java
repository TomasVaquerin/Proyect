package dev.tomas.tfg.rest.user.service;

import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.mapper.UserMapper;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.rest.user.repository.UserRepository;
import dev.tomas.tfg.rest.user.validator.UserValidator;
import dev.tomas.tfg.storage.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userValidator = new UserValidator(userRepository);
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public Optional<UserResponseDto> findById(UUID id) {
        userValidator.validateUserExists(id);
        return userRepository.findById(id)
                .map(UserMapper::toDto);
    }

    @Override
    public UserResponseDto save(UserRequestDto dto) {
        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestUpdateDTO dto) {
        User user = userValidator.validateUserExists(id);
        UserMapper.updateEntity(user, dto);
        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<UserResponseDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDto);
    }

    @Override
    public Optional<User> getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String guardarFoto(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fotoUrl = fileStorageService.guardarArchivo(file);
        user.setFotoPerfil(fotoUrl);
        userRepository.save(user);

        return fotoUrl;
    }
}
