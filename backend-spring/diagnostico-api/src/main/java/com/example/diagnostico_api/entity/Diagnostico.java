package com.example.diagnostico_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Diagnostico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double cpuUso;
    private Double ramUso;
    private Double ramTotal;
    private Double discoLivre;

    private String status;

    private LocalDateTime dataHora;
    
}
