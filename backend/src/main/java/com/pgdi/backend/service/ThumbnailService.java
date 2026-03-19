package com.pgdi.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ThumbnailService {

    private static final int THUMB_WIDTH = 200;
    private static final int THUMB_HEIGHT = 200;
    private static final String THUMB_PREFIX = "thumb_";

    @Value("${api.upload.directory}")
    private String uploadDir;

    public void gerarThumbnail(String nomeArquivo) {
        try {
            Path original = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nomeArquivo);
            Path destino = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(THUMB_PREFIX + nomeArquivo);

            BufferedImage imagemOriginal = ImageIO.read(original.toFile());
            if (imagemOriginal == null) {
                System.err.println("Aviso: não foi possível ler a imagem para thumbnail: " + nomeArquivo);
                return;
            }

            BufferedImage thumbnail = new BufferedImage(THUMB_WIDTH, THUMB_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(imagemOriginal, 0, 0, THUMB_WIDTH, THUMB_HEIGHT, null);
            g.dispose();

            String extensao = nomeArquivo.contains(".")
                    ? nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase()
                    : "jpg";

            String formato = extensao.equals("png") ? "png" : "jpg";
            ImageIO.write(thumbnail, formato, destino.toFile());

        } catch (IOException e) {
            System.err.println("Aviso: falha ao gerar thumbnail para " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public void deletarThumbnail(String nomeArquivo) {
        try {
            Path thumb = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(THUMB_PREFIX + nomeArquivo);
            Files.deleteIfExists(thumb);
        } catch (IOException e) {
            System.err.println("Aviso: falha ao deletar thumbnail: " + e.getMessage());
        }
    }

    public String getNomeThumbnail(String nomeArquivo) {
        return THUMB_PREFIX + nomeArquivo;
    }
}