package com.pgdi.backend.controller;

import com.pgdi.backend.dto.DocumentoPageResponseDTO;
import com.pgdi.backend.dto.DocumentoResponseDTO;
import com.pgdi.backend.dto.DocumentoStatusDTO;
import com.pgdi.backend.model.StatusDocumento;
import com.pgdi.backend.model.Usuario;
import com.pgdi.backend.service.DocumentoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipoId") Long tipoId,
            @RequestParam("filaId") Long filaId,
            @AuthenticationPrincipal Usuario usuarioLogado) throws IOException {

        String contentType = file.getContentType();
        List<String> extensoesPermitidas = Arrays.asList("image/jpeg", "image/jpg", "image/png");

        if (contentType == null || !extensoesPermitidas.contains(contentType)) {
            return ResponseEntity.status(422).body("Apenas arquivos .jpg, .jpeg e .png são permitidos.");
        }

        return ResponseEntity.ok(documentoService.salvarDocumento(file, tipoId, filaId, usuarioLogado));
    }

    @GetMapping("/buscar")
    public ResponseEntity<DocumentoPageResponseDTO> buscar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) StatusDocumento status,
            @RequestParam(required = false) Long tipoId,
            @RequestParam(required = false) Long filaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(documentoService.buscarPaginado(busca, status, tipoId, filaId, pagina, tamanho));
    }

    @GetMapping("/fila")
    public ResponseEntity<List<DocumentoResponseDTO>> listarFila(
            @RequestParam(required = false) StatusDocumento status,
            @RequestParam(required = false) Long tipoId,
            @RequestParam(required = false) Long filaId) {
        return ResponseEntity.ok(documentoService.listarFila(status, tipoId, filaId));
    }

    @GetMapping("/arquivo/{id}")
    public ResponseEntity<Resource> buscarArquivo(@PathVariable @NonNull Long id) {
        Resource resource = documentoService.carregarArquivo(id);
        return buildImageResponse(resource);
    }

    @GetMapping("/arquivo/{id}/thumbnail")
    public ResponseEntity<Resource> buscarThumbnail(@PathVariable @NonNull Long id) {
        Resource resource = documentoService.carregarThumbnail(id);
        return buildImageResponse(resource);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DocumentoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody DocumentoStatusDTO statusDTO) {
        return ResponseEntity.ok(documentoService.atualizarStatus(id, statusDTO.status(), statusDTO.motivoReprovacao()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @NonNull Long id) {
        documentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Resource> buildImageResponse(Resource resource) {
        String filename = Objects.requireNonNullElse(resource.getFilename(), "").toLowerCase();
        String mimeType = (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
                ? "image/jpeg"
                : "image/png";
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + Objects.requireNonNullElse(resource.getFilename(), "arquivo") + "\"")
                .body(resource);
    }
}