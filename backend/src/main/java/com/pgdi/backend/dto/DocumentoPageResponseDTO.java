package com.pgdi.backend.dto;

import java.util.List;

public record DocumentoPageResponseDTO(
    List<DocumentoResponseDTO> conteudo,
    int paginaAtual,
    int totalPaginas,
    long totalItens,
    boolean primeira,
    boolean ultima
) {}