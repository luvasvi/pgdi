package com.pgdi.backend.repository;

import com.pgdi.backend.model.Fila;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FilaRepository extends JpaRepository<Fila, Long> {
    List<Fila> findByRestritaFalse();
}