package dev.tomas.tfg.rest.grupo.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tomas.tfg.config.websockets.WebSocketHandler;
import dev.tomas.tfg.rest.grupo.dto.GrupoResponseDto;
import dev.tomas.tfg.websocket.model.Notificacion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class GrupoNotification {
    private final WebSocketHandler webSocketGruposHandler;
    private final ObjectMapper objectMapper;

    public GrupoNotification(WebSocketHandler webSocketGruposHandler, ObjectMapper objectMapper) {
        this.webSocketGruposHandler = webSocketGruposHandler;
        this.objectMapper = objectMapper;
    }

    public void notifyGrupoCreado(GrupoResponseDto grupo) {
        Notificacion<GrupoResponseDto> notificacion = new Notificacion<>(
                "Grupo",
                Notificacion.Tipo.CREATE,
                grupo,
                Instant.now().toString()
        );
        try {
            String json = objectMapper.writeValueAsString(notificacion);
            webSocketGruposHandler.sendMessage(json);
        } catch (IOException e) {
            // Manejo de error: log o lanzar excepción custom si lo prefieres
            e.printStackTrace();
        }
    }
}
