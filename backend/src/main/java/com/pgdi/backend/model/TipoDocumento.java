package com.pgdi.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipo_documento")
@Data
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;
}