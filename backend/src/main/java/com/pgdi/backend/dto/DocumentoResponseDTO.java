package com.pgdi.backend.dto;

import com.pgdi.backend.model.StatusDocumento;
import java.time.LocalDateTime;

public record DocumentoResponseDTO(
    Long id,
    String nomeArquivo,
    StatusDocumento status,
    String motivoReprovacao,
    String tipoDocumentoNome,
    String usuarioUsername,
    String filaNome,
    LocalDateTime dataUpload
) {}