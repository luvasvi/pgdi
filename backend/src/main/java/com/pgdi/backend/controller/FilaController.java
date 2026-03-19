package com.pgdi.backend.controller;

import com.pgdi.backend.model.Fila;
import com.pgdi.backend.model.Usuario;
import com.pgdi.backend.model.UserRole;
import com.pgdi.backend.repository.FilaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/filas")
public class FilaController {

    private final FilaRepository filaRepository;

    public FilaController(FilaRepository filaRepository) {
        this.filaRepository = filaRepository;
    }

    @PostMapping
    public ResponseEntity<Fila> criar(@RequestBody Fila fila) {
        Fila safeFila = Objects.requireNonNull(fila, "Os dados da fila não podem ser nulos");
        return ResponseEntity.ok(filaRepository.save(safeFila));
    }

    @GetMapping
    public ResponseEntity<List<Fila>> listar(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado.getRole() == UserRole.ADMIN) {
            return ResponseEntity.ok(filaRepository.findAll());
        }
        
        return ResponseEntity.ok(filaRepository.findByRestritaFalse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (id == null || !filaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        filaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}