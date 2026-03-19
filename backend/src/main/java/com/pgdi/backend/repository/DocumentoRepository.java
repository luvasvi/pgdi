package com.pgdi.backend.repository;

import com.pgdi.backend.model.Documento;
import com.pgdi.backend.model.StatusDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {


    List<Documento> findByStatus(StatusDocumento status);
    List<Documento> findByTipoDocumentoId(Long tipoId);
    List<Documento> findByStatusAndTipoDocumentoId(StatusDocumento status, Long tipoId);
    List<Documento> findByFilaId(Long filaId);
    List<Documento> findByFilaIdAndStatus(Long filaId, StatusDocumento status);


    @Query("""
        SELECT d FROM Documento d
        WHERE (:busca IS NULL OR LOWER(d.nomeArquivo) LIKE LOWER(CONCAT('%', :busca, '%')))
        AND (:status IS NULL OR d.status = :status)
        AND (:tipoId IS NULL OR d.tipoDocumento.id = :tipoId)
        AND (:filaId IS NULL OR d.fila.id = :filaId)
    """)
    Page<Documento> buscarComFiltros(
        @Param("busca") String busca,
        @Param("status") StatusDocumento status,
        @Param("tipoId") Long tipoId,
        @Param("filaId") Long filaId,
        Pageable pageable
    );
}