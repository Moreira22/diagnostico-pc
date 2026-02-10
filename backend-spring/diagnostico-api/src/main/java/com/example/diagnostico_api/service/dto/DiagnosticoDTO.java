package com.example.diagnostico_api.service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoDTO {
    private Long id;
    private Double cpuUso;
    private Double ramUso;
    private Double ramTotal;
    private Double discoLivre;
    private LocalDateTime dataColeta;
    private String cpuNome;
    private String gpu;
    
}
