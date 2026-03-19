package com.pgdi.backend.controller;

import com.pgdi.backend.model.TipoDocumento;
import com.pgdi.backend.repository.TipoDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/tipos")
public class TipoDocumentoController {

    @Autowired
    private TipoDocumentoRepository repository;

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<TipoDocumento> criar(@RequestBody TipoDocumento tipo) {
        TipoDocumento novoTipo = repository.save(Objects.requireNonNull(tipo));
        return ResponseEntity.ok(novoTipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (id == null || !repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}