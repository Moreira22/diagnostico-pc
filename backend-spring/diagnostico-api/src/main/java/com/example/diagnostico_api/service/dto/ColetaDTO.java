package com.example.diagnostico_api.service.dto;

import lombok.Data;

@Data
public class ColetaDTO {
    private Double cpuUso;
    private Double ramUso;
    private Double ramTotal;
    private Double discoLivre;
}
