package dev.tomas.tfg.config.websockets;

import java.io.IOException;

public interface WebSocketSender {
    void sendMessage(String message) throws IOException;
}