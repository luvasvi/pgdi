package com.pgdi.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "O usuário não pode estar vazio")
    String username, 
    
    @NotBlank(message = "A senha não pode estar vazia")
    String password
) {}