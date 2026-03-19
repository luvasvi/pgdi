package com.pgdi.backend.service;

import com.pgdi.backend.dto.UsuarioResponseDTO;
import com.pgdi.backend.mapper.UsuarioMapper;
import com.pgdi.backend.model.Usuario;
import com.pgdi.backend.repository.UsuarioRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(@NonNull Long id) {
        return usuarioMapper.toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado")));
    }

    public UsuarioResponseDTO alterarStatus(@NonNull Long id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(ativo);
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    public void deletar(@NonNull Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}