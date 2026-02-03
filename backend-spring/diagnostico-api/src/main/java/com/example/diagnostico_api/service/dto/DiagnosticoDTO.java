package com.example.diagnostico_api.service.dto;

import java.time.LocalDateTime;

public class DiagnosticoDTO {
    private Long id;
    private Double cpuUso;
    private Double ramUso;
    private Double ramTotal;
    private Double discoLivre;
    private LocalDateTime dataColeta;

    // getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCpuUso() {
        return cpuUso;
    }

    public void setCpuUso(Double cpuUso) {
        this.cpuUso = cpuUso;
    }

    public Double getRamUso() {
        return ramUso;
    }

    public void setRamUso(Double ramUso) {
        this.ramUso = ramUso;
    }

    public Double getRamTotal() {
        return ramTotal;
    }

    public void setRamTotal(Double ramTotal) {
        this.ramTotal = ramTotal;
    }

    public Double getDiscoLivre() {
        return discoLivre;
    }

    public void setDiscoLivre(Double discoLivre) {
        this.discoLivre = discoLivre;
    }

    public LocalDateTime getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(LocalDateTime dataColeta) {
        this.dataColeta = dataColeta;
    }
}
