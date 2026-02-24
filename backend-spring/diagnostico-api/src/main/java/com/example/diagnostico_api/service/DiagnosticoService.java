package com.example.diagnostico_api.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Diagnostico;
import com.example.diagnostico_api.repository.DiagnosticoRepository;
import com.example.diagnostico_api.service.dto.ColetaDTO;
import com.example.diagnostico_api.service.dto.DiagnosticoDTO;
import com.example.diagnostico_api.service.dto.MachineRecordDTO;
import com.example.diagnostico_api.service.dto.MetricsSnapshotDTO;
import com.example.diagnostico_api.service.mapper.DiagnosticoMapper;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.Document;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosticoService {
    private final DiagnosticoRepository repository;
    private final DiagnosticoMapper mapper;

    public Diagnostico findEntity(Long id) {
        return repository.findById(id).orElse(null);
    }

    public DiagnosticoDTO findById(Long id) {
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
            return "offline"; // crítico = offline

        if (dto.getRam_percent() > 85)
            return "warning"; // alerta = warning

        if (dto.getDisk_percent() > 90)
            return "warning"; // alerta = warning

        return "online";
    }

    public List<DiagnosticoDTO> listar() {
        return mapper.toDto(repository.findAll());
    }

    public List<MachineRecordDTO> listarMaquinas() {

        List<Diagnostico> diagnosticos = repository.findAll();

        Map<String, List<Diagnostico>> agrupado = diagnosticos.stream()
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
                            d.getTimestamp()))
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
                    history);

        }).toList();
    }

    public MachineRecordDTO findByMachineId(String machineId) {

        List<Diagnostico> lista = repository.findByMachineIdOrderByTimestampAsc(machineId);

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
                        d.getTimestamp()))
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
                history);
    }

    public byte[] gerarPDF(Long id) {

        Diagnostico d = findEntity(id);

        if (d == null) {
            throw new RuntimeException("Diagnóstico não encontrado");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        // ==========================
        // TÍTULO
        // ==========================
        Paragraph titulo = new Paragraph("RELATÓRIO DE DIAGNÓSTICO DO SISTEMA")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER);

        doc.add(titulo);
        doc.add(new Paragraph("\n"));

        // ==========================
        // INFORMAÇÕES DA MÁQUINA
        // ==========================
        doc.add(new Paragraph("INFORMAÇÕES DA MÁQUINA")
                .setBold()
                .setFontSize(14));

        Table infoTable = new Table(2).useAllAvailableWidth();

        infoTable.addCell("Hostname");
        infoTable.addCell(d.getHostname());

        infoTable.addCell("IP");
        infoTable.addCell(d.getIpAddress());

        infoTable.addCell("Sistema Operacional");
        infoTable.addCell(d.getOs());

        infoTable.addCell("CPU");
        infoTable.addCell(d.getCpuName() + " (" + d.getCpuCores() + " núcleos)");

        infoTable.addCell("Data da Coleta");
        infoTable.addCell(d.getTimestamp().toString());

        doc.add(infoTable);

        doc.add(new Paragraph("\n"));

        // ==========================
        // MÉTRICAS
        // ==========================
        doc.add(new Paragraph("MÉTRICAS DE DESEMPENHO")
                .setBold()
                .setFontSize(14));

        Table metricsTable = new Table(2).useAllAvailableWidth();

        metricsTable.addCell("CPU");
        metricsTable.addCell(criarBarra(d.getCpuPercent()));

        metricsTable.addCell("RAM");
        metricsTable.addCell(criarBarra(d.getRamPercent()));

        metricsTable.addCell("Disco");
        metricsTable.addCell(criarBarra(d.getDiskPercent()));

        metricsTable.addCell("Uptime");
        metricsTable.addCell(String.format("%.2f horas", d.getUptimeHours()));

        doc.add(metricsTable);

        doc.add(new Paragraph("\n"));

        // ==========================
        // STATUS DESTACADO
        // ==========================
        Color statusColor = switch (d.getStatus().toLowerCase()) {
            case "online" -> ColorConstants.GREEN;
            case "warning" -> ColorConstants.ORANGE;
            case "offline" -> ColorConstants.RED;
            default -> ColorConstants.BLACK;
        };

        Paragraph status = new Paragraph("STATUS ATUAL: " + d.getStatus().toUpperCase())
                .setBold()
                .setFontSize(14)
                .setFontColor(statusColor);

        doc.add(status);

        doc.add(new Paragraph("\n"));

        // ==========================
        // RODAPÉ
        // ==========================
        doc.add(new Paragraph("Relatório gerado automaticamente pelo Sistema de Diagnóstico Inteligente.")
                .setFontSize(10)
                .setItalic());

        doc.close();

        return out.toByteArray();
    }

    private String criarBarra(Double valor) {
        int blocos = (int) (valor / 5); // 20 blocos máximo
        StringBuilder barra = new StringBuilder();

        for (int i = 0; i < blocos; i++) {
            barra.append("█");
        }

        return barra + " " + String.format("%.1f %%", valor);
    }

}
