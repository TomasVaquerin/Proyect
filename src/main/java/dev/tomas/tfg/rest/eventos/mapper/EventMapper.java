package dev.tomas.tfg.rest.eventos.mapper;

import dev.tomas.tfg.rest.eventos.dto.EventRequestDto;
import dev.tomas.tfg.rest.eventos.dto.EventResponseDto;
import dev.tomas.tfg.rest.eventos.model.Event;
import dev.tomas.tfg.rest.grupo.model.Grupo;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public Event toEntity(EventRequestDto dto, Grupo grupo, User creador) {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setGrupo(grupo);
        event.setCreador(creador);
        event.setFecha(dto.fecha());
        event.setHoras(dto.horas());
        event.setDuracionMinutos(dto.duracionMinutos());
        event.setUbicacion(dto.ubicacion());
        event.setImagenUrl(dto.imagenUrl());
        event.setEncuestaDeCambioDeHora(dto.encuestaDeCambioDeHora());
        event.setTipo(dto.tipo());
        event.setFechaCreacion(LocalDateTime.now());
        event.setEliminado(false);
        return event;
    }

    public void updateEntityFromDto(Event event, EventRequestDto dto) {
        event.setNombre(dto.nombre());
        event.setFecha(dto.fecha());
        event.setHoras(dto.horas());
        event.setDuracionMinutos(dto.duracionMinutos());
        event.setUbicacion(dto.ubicacion());
        event.setImagenUrl(dto.imagenUrl());
        event.setEncuestaDeCambioDeHora(dto.encuestaDeCambioDeHora());
        event.setTipo(dto.tipo());
    }

    public static EventResponseDto toDto(Event event) {
        Set<String> apuntados = event.getUsuariosApuntados() != null
                ? event.getUsuariosApuntados().stream()
                .map(user -> user.getNombre() + " " + user.getApellidos())
                .collect(Collectors.toSet())
                : Set.of();

        return EventResponseDto.builder()
                .id(event.getId().toString())
                .grupoNombre(event.getGrupo().getNombre())
                .creadorNombre(event.getCreador().getNombre())
                .fecha(event.getFecha())
                .horas(event.getHoras())
                .duracionMinutos(event.getDuracionMinutos())
                .ubicacion(event.getUbicacion())
                .imagenUrl(event.getImagenUrl())
                .encuestaDeCambioDeHora(event.isEncuestaDeCambioDeHora())
                .tipo(event.getTipo())
                .usuariosApuntados(apuntados)
                .fechaCreacion(event.getFechaCreacion())
                .eliminado(event.isEliminado())
                .build();
    }
}
