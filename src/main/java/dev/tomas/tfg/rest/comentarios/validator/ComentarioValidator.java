package dev.tomas.tfg.rest.comentarios.validator;

import dev.tomas.tfg.rest.comentarios.exception.AccesoDenegadoComentarioException;
import dev.tomas.tfg.rest.comentarios.exception.ComentarioNotFoundException;
import dev.tomas.tfg.rest.comentarios.model.Comentario;
import dev.tomas.tfg.rest.comentarios.repository.ComentarioRepository;
import dev.tomas.tfg.rest.user.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ComentarioValidator {

    private final ComentarioRepository comentarioRepository;

    public ComentarioValidator(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public Comentario validateComentarioExists(UUID comentarioId) {
        return comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ComentarioNotFoundException(comentarioId));
    }

    public void validatePermisosDeBorrado(Comentario comentario, User solicitante) {
        UUID solicitanteId = solicitante.getId();
        UUID autorId = comentario.getAutor().getId();
        UUID creadorGrupoId = comentario.getEvento().getGrupo().getCreador().getId();

        if (!solicitanteId.equals(autorId) && !solicitanteId.equals(creadorGrupoId)) {
            throw new AccesoDenegadoComentarioException();
        }
    }
}
