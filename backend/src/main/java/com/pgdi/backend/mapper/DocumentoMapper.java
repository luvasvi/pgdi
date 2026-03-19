package com.pgdi.backend.mapper;

import com.pgdi.backend.dto.DocumentoResponseDTO;
import com.pgdi.backend.model.Documento;
import org.springframework.stereotype.Component;

@Component
public class DocumentoMapper {

    public DocumentoResponseDTO toDTO(Documento doc) {
        return new DocumentoResponseDTO(
            doc.getId(),
            doc.getNomeArquivo(),
            doc.getStatus(),
            doc.getMotivoReprovacao(),
            doc.getTipoDocumento().getNome(),
            doc.getUsuario().getUsername(),
            doc.getFila().getNome(),
            doc.getDataUpload()
        );
    }
}