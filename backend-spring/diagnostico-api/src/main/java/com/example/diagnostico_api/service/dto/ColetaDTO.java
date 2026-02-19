package com.example.diagnostico_api.service.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ColetaDTO {

    private String machine_id;
    private String hostname;
    private String os;
    private String cpu_name;
    private Double cpu_percent;
    private Integer cpu_cores;

    private Double ram_total_gb;
    private Double ram_used_gb;
    private Double ram_percent;

    private Double disk_total_gb;
    private Double disk_used_gb;
    private Double disk_percent;

    private String gpu_name;
    private Double uptime_hours;

    private String ip_address;
    private LocalDateTime timestamp;
}