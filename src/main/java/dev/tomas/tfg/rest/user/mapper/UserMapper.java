package dev.tomas.tfg.rest.user.mapper;

import dev.tomas.tfg.rest.calendario.mapper.CalendarioMapper;
import dev.tomas.tfg.rest.user.dto.UserRequestDto;
import dev.tomas.tfg.rest.user.dto.UserRequestUpdateDTO;
import dev.tomas.tfg.rest.user.dto.UserResponseDto;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.utils.IDGenerator;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User toEntity(UserRequestDto dto) {
        User user = new User();
        user.setId(IDGenerator.generateId());
        user.setEmail(dto.email());
        user.setNombre(dto.nombre());
        user.setApellidos(dto.apellidos());
        user.setFotoPerfil(dto.fotoPerfil());
        user.setFechaNacimiento(dto.fechaNacimiento());
        return user;
    }

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(String.valueOf(user.getId()))
                .email(user.getEmail())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .fotoPerfil(user.getFotoPerfil())
                .fechaNacimiento(user.getFechaNacimiento())
                .calendario(CalendarioMapper.toDto(user.getCalendario()))
                .build();
    }

    public static void updateEntity(User user, UserRequestUpdateDTO dto) {
        user.setNombre(dto.nombre());
        user.setApellidos(dto.apellidos());
        user.setFotoPerfil(dto.fotoPerfil());
        user.setFechaNacimiento(dto.fechaNacimiento());
    }
}