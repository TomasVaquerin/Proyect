package dev.tomas.tfg.storage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/";

    public FileStorageService() {
        // Crear el directorio si no existe
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Error al crear el directorio de almacenamiento", e);
        }
    }

    public String guardarArchivo(MultipartFile file) {
        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        // Generar un nombre único
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        try {
            // Guardar el archivo en el servidor
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName; // Retornar la URL de acceso al archivo
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
    }
}