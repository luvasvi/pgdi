package com.pgdi.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${api.upload.directory}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            System.out.println(">>> Pasta de uploads verificada/criada em: " + uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar a pasta de uploads", e);
        }
    }
}