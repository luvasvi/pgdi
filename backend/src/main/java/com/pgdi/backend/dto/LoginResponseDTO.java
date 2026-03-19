package com.pgdi.backend.dto;

// Essencial para o Angular saber o perfil do usuário logado
public record LoginResponseDTO(String token, String role) {}