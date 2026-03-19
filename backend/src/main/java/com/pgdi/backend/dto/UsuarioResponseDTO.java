package com.pgdi.backend.dto;

import com.pgdi.backend.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record UsuarioResponseDTO(
    @NotNull Long id, 
    String username,
    String email,
    UserRole role,
    boolean ativo
) {}