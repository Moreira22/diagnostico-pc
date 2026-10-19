package com.example.diagnostico_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "computador")
@Getter
@Setter
@NoArgsConstructor
public class Computador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;

    @Column(nullable = false)
    private String nome;

    // Descrições do hardware
    private String memoria;
    private String ssd;
    private String cpu;
    private String gpu;

    // Métricas de armazenamento (GB)
    @Column(name = "ssd_total")
    private Double ssdTotal;

    @Column(name = "ssd_livre")
    private Double ssdLivre;

    // Métricas de memória (GB)
    @Column(name = "memoria_total")
    private Double memoriaTotal;

    @Column(name = "memoria_livre")
    private Double memoriaLivre;

    @Column(name = "memoria_uso")
    private Double memoriaUso;

    private String os;

    @Column(name = "cpu_nucleo")
    private Integer cpuNucleo;

    @Column(name = "cpu_porcentagem")
    private Double cpuPorcentagem;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_coleta")
    private LocalDateTime dataColeta;
}
