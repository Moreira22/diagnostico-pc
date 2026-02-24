package com.example.diagnostico_api.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.diagnostico_api.entity.Diagnostico;
import com.example.diagnostico_api.repository.DiagnosticoRepository;
import com.example.diagnostico_api.service.dto.ColetaDTO;
import com.example.diagnostico_api.service.dto.DiagnosticoDTO;
import com.example.diagnostico_api.service.dto.MachineRecordDTO;
import com.example.diagnostico_api.service.dto.MetricsSnapshotDTO;
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
    public DiagnosticoDTO findById(Long id){
        return mapper.toDto(findEntity(id));
    }
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
        return "offline";   // crítico = offline

    if (dto.getRam_percent() > 85)
        return "warning";   // alerta = warning

    if (dto.getDisk_percent() > 90)
        return "warning";   // alerta = warning

    return "online";
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

    public List<MachineRecordDTO> listarMaquinas() {

    List<Diagnostico> diagnosticos = repository.findAll();

    Map<String, List<Diagnostico>> agrupado =
            diagnosticos.stream()
                    .collect(Collectors.groupingBy(Diagnostico::getMachineId));

    return agrupado.values().stream().map(lista -> {

        Diagnostico ultimo = lista.get(lista.size() - 1);

        List<MetricsSnapshotDTO> history = lista.stream()
                .map(d -> new MetricsSnapshotDTO(
                        d.getCpuPercent(),
                        d.getRamUsedGb(),
                        d.getRamPercent(),
                        d.getDiskUsedGb(),
                        d.getDiskPercent(),
                        d.getUptimeHours(),
                        d.getTimestamp()
                ))
                .toList();

        return new MachineRecordDTO(
                ultimo.getId(),
                ultimo.getMachineId(),
                ultimo.getHostname(),
                ultimo.getOs(),
                ultimo.getCpuName(),
                ultimo.getCpuCores(),
                ultimo.getRamTotalGb(),
                ultimo.getDiskTotalGb(),
                ultimo.getGpuName(),
                ultimo.getIpAddress(),
                ultimo.getStatus(),
                ultimo.getTimestamp(),
                history
            );

        }).toList();
    }

    public MachineRecordDTO findByMachineId(String machineId) {

    List<Diagnostico> lista =
            repository.findByMachineIdOrderByTimestampAsc(machineId);

    if (lista.isEmpty()) {
        return null;
    }

    Diagnostico ultimo = lista.get(lista.size() - 1);

    List<MetricsSnapshotDTO> history = lista.stream()
            .map(d -> new MetricsSnapshotDTO(
                    d.getCpuPercent(),
                    d.getRamUsedGb(),
                    d.getRamPercent(),
                    d.getDiskUsedGb(),
                    d.getDiskPercent(),
                    d.getUptimeHours(),
                    d.getTimestamp()
            ))
            .toList();

    return new MachineRecordDTO(
            ultimo.getId(),
            ultimo.getMachineId(),
            ultimo.getHostname(),
            ultimo.getOs(),
            ultimo.getCpuName(),
            ultimo.getCpuCores(),
            ultimo.getRamTotalGb(),
            ultimo.getDiskTotalGb(),
            ultimo.getGpuName(),
            ultimo.getIpAddress(),
            ultimo.getStatus(),
            ultimo.getTimestamp(),
            history
    );
}
    
    
}
