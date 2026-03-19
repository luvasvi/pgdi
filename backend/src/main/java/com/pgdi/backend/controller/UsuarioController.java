package com.pgdi.backend.controller;

import com.pgdi.backend.dto.UsuarioResponseDTO;
import com.pgdi.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponseDTO> alternarStatus(@PathVariable Long id) {
        Objects.requireNonNull(id, "ID não pode ser nulo");
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuarioService.alterarStatus(id, !usuario.ativo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Objects.requireNonNull(id, "ID não pode ser nulo");
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}