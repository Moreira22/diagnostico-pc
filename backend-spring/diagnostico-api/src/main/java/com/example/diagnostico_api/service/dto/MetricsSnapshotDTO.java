package com.example.diagnostico_api.service.dto;

import java.time.LocalDateTime;

public record MetricsSnapshotDTO(
        Double cpu_percent,
        Double ram_used_gb,
        Double ram_percent,
        Double disk_used_gb,
        Double disk_percent,
        Double uptime_hours,
        LocalDateTime timestamp
) {}
