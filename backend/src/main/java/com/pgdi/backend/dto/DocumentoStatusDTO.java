package com.pgdi.backend.dto;

import com.pgdi.backend.model.StatusDocumento;

public record DocumentoStatusDTO(
    StatusDocumento status,
    String motivoReprovacao
) {}