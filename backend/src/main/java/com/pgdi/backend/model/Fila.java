package com.pgdi.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fila")
@Data
public class Fila {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private boolean restrita = false;
}