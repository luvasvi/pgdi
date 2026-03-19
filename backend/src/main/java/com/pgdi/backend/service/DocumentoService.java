package com.pgdi.backend.service;

import com.pgdi.backend.dto.DocumentoPageResponseDTO;
import com.pgdi.backend.dto.DocumentoResponseDTO;
import com.pgdi.backend.mapper.DocumentoMapper;
import com.pgdi.backend.model.*;
import com.pgdi.backend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentoService {

    @Value("${api.upload.directory}")
    private String uploadDir;

    private final DocumentoRepository documentoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final FilaRepository filaRepository;
    private final DocumentoMapper documentoMapper;
    private final ThumbnailService thumbnailService;

    public DocumentoService(DocumentoRepository documentoRepository,
                            TipoDocumentoRepository tipoDocumentoRepository,
                            FilaRepository filaRepository,
                            DocumentoMapper documentoMapper,
                            ThumbnailService thumbnailService) {
        this.documentoRepository = documentoRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.filaRepository = filaRepository;
        this.documentoMapper = documentoMapper;
        this.thumbnailService = thumbnailService;
    }

    public DocumentoResponseDTO salvarDocumento(MultipartFile arquivo, Long tipoId, Long filaId, Usuario usuario) throws IOException {
        Objects.requireNonNull(tipoId, "O ID do tipo não pode ser nulo");
        Objects.requireNonNull(filaId, "O ID da fila não pode ser nulo");

        TipoDocumento tipo = tipoDocumentoRepository.findById(tipoId)
                .orElseThrow(() -> new RuntimeException("Tipo de documento não encontrado"));

        Fila fila = filaRepository.findById(filaId)
                .orElseThrow(() -> new RuntimeException("Fila não encontrada"));

        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = (nomeOriginal != null && nomeOriginal.contains(".")) ?
                nomeOriginal.substring(nomeOriginal.lastIndexOf(".")) : "";

        String novoNome = UUID.randomUUID().toString() + extensao;
        Path caminhoDestino = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(novoNome);

        Files.createDirectories(caminhoDestino.getParent());
        Files.copy(arquivo.getInputStream(), caminhoDestino);

        // Gera thumbnail após salvar o arquivo original
        thumbnailService.gerarThumbnail(novoNome);

        Documento documento = new Documento();
        documento.setNomeArquivo(nomeOriginal);
        documento.setCaminhoArquivo(novoNome);
        documento.setTipoDocumento(tipo);
        documento.setFila(fila);
        documento.setUsuario(usuario);
        documento.setStatus(StatusDocumento.PENDENTE);

        return documentoMapper.toDTO(documentoRepository.save(documento));
    }

    public List<DocumentoResponseDTO> listarFila(StatusDocumento status, Long tipoId, Long filaId) {
        List<Documento> docs;

        if (filaId != null && status != null) {
            docs = documentoRepository.findByFilaIdAndStatus(filaId, status);
        } else if (filaId != null) {
            docs = documentoRepository.findByFilaId(filaId);
        } else if (status != null && tipoId != null) {
            docs = documentoRepository.findByStatusAndTipoDocumentoId(status, tipoId);
        } else if (status != null) {
            docs = documentoRepository.findByStatus(status);
        } else if (tipoId != null) {
            docs = documentoRepository.findByTipoDocumentoId(tipoId);
        } else {
            docs = documentoRepository.findAll();
        }

        return docs.stream().map(documentoMapper::toDTO).collect(Collectors.toList());
    }

    public DocumentoPageResponseDTO buscarPaginado(
            String busca,
            StatusDocumento status,
            Long tipoId,
            Long filaId,
            int pagina,
            int tamanho) {

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataUpload").descending());
        Page<Documento> page = documentoRepository.buscarComFiltros(
                busca != null && busca.isBlank() ? null : busca,
                status,
                tipoId,
                filaId,
                pageable
        );

        return new DocumentoPageResponseDTO(
                page.getContent().stream().map(documentoMapper::toDTO).toList(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public DocumentoResponseDTO atualizarStatus(Long id, StatusDocumento novoStatus, String motivo) {
        Objects.requireNonNull(id, "O ID não pode ser nulo");

        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        documento.setStatus(novoStatus);
        documento.setMotivoReprovacao(novoStatus == StatusDocumento.REPROVADO ? motivo : null);

        return documentoMapper.toDTO(documentoRepository.save(documento));
    }

    public void deletar(@NonNull Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        try {
            Path caminho = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(documento.getCaminhoArquivo());
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            System.err.println("Aviso: não foi possível remover o arquivo físico: " + e.getMessage());
        }

        // Deleta o thumbnail junto
        thumbnailService.deletarThumbnail(documento.getCaminhoArquivo());

        documentoRepository.deleteById(id);
    }

    public Resource carregarArquivo(@NonNull Long id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        try {
            Path caminho = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(doc.getCaminhoArquivo());

            Resource resource = new UrlResource(
                    Objects.requireNonNull(caminho.toUri(), "URI não pode ser nula"));

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Arquivo não encontrado: " + caminho);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao processar caminho", e);
        }
    }

    public Resource carregarThumbnail(@NonNull Long id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        try {
            String nomeThumbnail = thumbnailService.getNomeThumbnail(doc.getCaminhoArquivo());
            Path caminho = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nomeThumbnail);

            Resource resource = new UrlResource(
                    Objects.requireNonNull(caminho.toUri(), "URI não pode ser nula"));

            // Se thumbnail não existir, retorna a imagem original
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            return carregarArquivo(id);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao processar caminho do thumbnail", e);
        }
    }
}