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
    // Identificação da máquina
    private String machineId;
    private String hostname;
    private String os;
    private String ipAddress;

    // CPU
    private String cpuName;
    private Double cpuPercent;
    private Integer cpuCores;

    // RAM
    private Double ramTotalGb;
    private Double ramUsedGb;
    private Double ramPercent;

    // Disco
    private Double diskTotalGb;
    private Double diskUsedGb;
    private Double diskPercent;

    // GPU
    private String gpuName;

    // Sistema
    private Double uptimeHours;

    // Controle
    private String status;

    private LocalDateTime timestamp;
    
}
