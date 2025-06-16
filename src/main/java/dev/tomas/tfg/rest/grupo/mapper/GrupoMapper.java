    package dev.tomas.tfg.rest.grupo.mapper;

    import dev.tomas.tfg.rest.calendario.dto.CalendarioResponseDto;
    import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
    import dev.tomas.tfg.rest.grupo.model.Grupo;
    import dev.tomas.tfg.rest.user.model.User;
    import dev.tomas.tfg.utils.IDGenerator;
    import org.springframework.stereotype.Component;

    import java.util.Set;
    import java.util.stream.Collectors;

    @Component
    public class GrupoMapper {

        public Grupo toEntity(String nombre, User creador) {
            Grupo grupo = new Grupo();
            grupo.setId(IDGenerator.generateId());
            grupo.setNombre(nombre);
            grupo.setCreador(creador);
            return grupo;
        }


        public static GrupoResponseDto toDto(Grupo grupo, CalendarioResponseDto calendario) {
            String admin = grupo.getCreador().getNombre() + " " + grupo.getCreador().getApellidos();

            Set<String> miembros = grupo.getUsuarios().stream()
                    .map(u -> u.getNombre() + " " + u.getApellidos())
                    .collect(Collectors.toSet());

            return new GrupoResponseDto(
                    grupo.getId().toString(),
                    grupo.getNombre(),
                    admin,
                    miembros,
                    calendario
            );
        }

    }
