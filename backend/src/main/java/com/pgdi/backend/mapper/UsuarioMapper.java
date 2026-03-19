package com.pgdi.backend.mapper;

import com.pgdi.backend.dto.UsuarioResponseDTO;
import com.pgdi.backend.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getUsername(),
            usuario.getEmail(),
            usuario.getRole(),
            usuario.isAtivo()
        );
    }
}