package com.pgdi.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento")
@Data
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String caminhoArquivo; // Onde o arquivo está salvo no disco

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumento status = StatusDocumento.PENDENTE;

    private String motivoReprovacao;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoDocumento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "fila_id", nullable = false)
    private Fila fila;

    private LocalDateTime dataUpload = LocalDateTime.now();
}