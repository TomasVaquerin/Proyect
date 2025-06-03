package dev.tomas.tfg.rest.calendario.mapper;

import dev.tomas.tfg.rest.calendario.dto.*;
import dev.tomas.tfg.rest.calendario.model.*;
import dev.tomas.tfg.rest.user.model.User;
import dev.tomas.tfg.utils.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class CalendarioMapper {

    public static Calendario toEntity(CalendarioRequestDto dto, User user) {
        Calendario calendario = new Calendario();
        calendario.setId(IDGenerator.generateId());
        calendario.setUser(user);
        calendario.setBloquesRecurrentes(
                dto.bloquesRecurrentes() != null ?
                        dto.bloquesRecurrentes().stream()
                                .map(CalendarioMapper::toEntity)
                                .collect(Collectors.toList())
                        : List.of()
        );
        calendario.setExcepciones(
                dto.excepciones() != null ?
                        dto.excepciones().stream()
                                .map(CalendarioMapper::toEntity)
                                .collect(Collectors.toList())
                        : List.of()
        );
        return calendario;
    }

    public static CalendarioResponseDto toDto(Calendario calendario) {
        if (calendario == null) return null;
        return CalendarioResponseDto.builder()
                .id(calendario.getId())
                .bloquesRecurrentes(
                        calendario.getBloquesRecurrentes() != null ?
                                calendario.getBloquesRecurrentes().stream()
                                        .map(CalendarioMapper::toDto)
                                        .collect(Collectors.toList())
                                : List.of()
                )
                .excepciones(
                        calendario.getExcepciones() != null ?
                                calendario.getExcepciones().stream()
                                        .map(CalendarioMapper::toDto)
                                        .collect(Collectors.toList())
                                : List.of()
                )
                .userId(calendario.getUser() != null ? calendario.getUser().getId() : null)
                .build();
    }

    public static BloqueRecurrente toEntity(BloqueRecurrenteDto dto) {
        BloqueRecurrente bloque = new BloqueRecurrente();
        bloque.setDiaSemana(dto.diaSemana());
        bloque.setHoraInicio(dto.horaInicio());
        bloque.setHoraFin(dto.horaFin());
        return bloque;
    }

    public static BloqueRecurrenteDto toDto(BloqueRecurrente bloque) {
        return BloqueRecurrenteDto.builder()
                .diaSemana(bloque.getDiaSemana())
                .horaInicio(bloque.getHoraInicio())
                .horaFin(bloque.getHoraFin())
                .build();
    }

    public static ExcepcionCalendario toEntity(ExcepcionCalendarioDto dto) {
        ExcepcionCalendario ex = new ExcepcionCalendario();
        ex.setFechaInicio(dto.fechaInicio());
        ex.setFechaFin(dto.fechaFin());
        ex.setDisponible(dto.disponible());
        return ex;
    }

    public static ExcepcionCalendarioDto toDto(ExcepcionCalendario ex) {
        return ExcepcionCalendarioDto.builder()
                .fechaInicio(ex.getFechaInicio())
                .fechaFin(ex.getFechaFin())
                .disponible(ex.isDisponible())
                .build();
    }

}