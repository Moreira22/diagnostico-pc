package com.example.diagnostico_api.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ComputadorDTO {

    private Long id;
    private Long setorId;
    private String nome;
    private String memoria;
    private String ssd;
    private String cpu;
    private String gpu;
    private Double ssdTotal;
    private Double ssdLivre;
    private Double memoriaTotal;
    private Double memoriaLivre;
    private Double memoriaUso;
    private String os;
    private Integer cpuNucleo;
    private Double cpuPorcentagem;
    private Boolean ativo;
    private LocalDateTime dataColeta;
}