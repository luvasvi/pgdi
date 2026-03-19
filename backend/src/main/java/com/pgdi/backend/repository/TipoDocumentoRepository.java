package com.pgdi.backend.repository;

import com.pgdi.backend.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    Optional<TipoDocumento> findByNome(String nome);
}