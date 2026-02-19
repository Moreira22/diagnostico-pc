package com.example.diagnostico_api.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Diagnostico;
import com.example.diagnostico_api.repository.DiagnosticoRepository;
import com.example.diagnostico_api.service.dto.ColetaDTO;
import com.example.diagnostico_api.service.dto.DiagnosticoDTO;
import com.example.diagnostico_api.service.mapper.DiagnosticoMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.Document;




@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosticoService {
    private final DiagnosticoRepository repository;
    private final DiagnosticoMapper mapper;

    public Diagnostico findEntity(Long id){ return repository.findById(id).orElse(null); }

    public Diagnostico processar(ColetaDTO dto) {

        Diagnostico d = new Diagnostico();
    
        d.setMachineId(dto.getMachine_id());
        d.setHostname(dto.getHostname());
        d.setOs(dto.getOs());
        d.setIpAddress(dto.getIp_address());
    
        d.setCpuName(dto.getCpu_name());
        d.setCpuPercent(dto.getCpu_percent());
        d.setCpuCores(dto.getCpu_cores());
    
        d.setRamTotalGb(dto.getRam_total_gb());
        d.setRamUsedGb(dto.getRam_used_gb());
        d.setRamPercent(dto.getRam_percent());
    
        d.setDiskTotalGb(dto.getDisk_total_gb());
        d.setDiskUsedGb(dto.getDisk_used_gb());
        d.setDiskPercent(dto.getDisk_percent());
    
        d.setGpuName(dto.getGpu_name());
        d.setUptimeHours(dto.getUptime_hours());
    
        d.setTimestamp(dto.getTimestamp());
        d.setStatus(analisar(dto));
    
        return repository.save(d);
    }

    private String analisar(ColetaDTO dto) {

        if (dto.getCpu_percent() > 90)
            return "CRÍTICO";
    
        if (dto.getRam_percent() > 85)
            return "ALERTA RAM";
    
        if (dto.getDisk_percent() > 90)
            return "ALERTA DISCO";
    
        return "ONLINE";
    }

    public List<DiagnosticoDTO> listar() {
        return mapper.toDto(repository.findAll());
    }

    public byte[] gerarPDF(Long id) {
        Diagnostico d = findEntity(id);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Máquina: " + d.getHostname()));
        doc.add(new Paragraph("IP: " + d.getIpAddress()));
        doc.add(new Paragraph("Sistema: " + d.getOs()));
        doc.add(new Paragraph("CPU: " + d.getCpuPercent() + "%"));
        doc.add(new Paragraph("RAM: " + d.getRamPercent() + "%"));
        doc.add(new Paragraph("Disco: " + d.getDiskPercent() + "%"));
        doc.add(new Paragraph("Uptime (h): " + d.getUptimeHours()));
        doc.add(new Paragraph("Status: " + d.getStatus()));
        doc.close();
        
        return out.toByteArray();
    }
    
    
}
