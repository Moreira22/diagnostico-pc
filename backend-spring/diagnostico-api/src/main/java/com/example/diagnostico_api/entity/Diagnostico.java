package com.example.diagnostico_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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