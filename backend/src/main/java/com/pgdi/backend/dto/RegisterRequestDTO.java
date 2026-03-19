package com.pgdi.backend.dto;

public record RegisterRequestDTO(
    String username,
    String email,
    String password,
    String role
) {}