package dev.tomas.tfg.rest.user.service;

import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<UserResponseDto> findAll();

    Optional<UserResponseDto> findById(UUID id);

    UserResponseDto save(UserRequestDto usuarioRequestDto);

    UserResponseDto update(UUID id, UserRequestUpdateDTO dto);

    void deleteById(UUID id);

    Optional<UserResponseDto> findByEmail(String email);

    Optional<User> getUserEntityByEmail(String email);

    String guardarFoto (UUID id, MultipartFile file);

}
