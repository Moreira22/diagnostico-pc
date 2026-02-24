package com.example.diagnostico_api.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MachineRecordDTO(
        Long id,
        String machine_id,
        String hostname,
        String os,
        String cpu_name,
        Integer cpu_cores,
        Double ram_total_gb,
        Double disk_total_gb,
        String gpu_name,
        String ip_address,
        String status,
        LocalDateTime last_seen,
        List<MetricsSnapshotDTO> metrics_history
) {}
