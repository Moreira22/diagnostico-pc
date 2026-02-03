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
        d.setCpuUso(dto.getCpuUso());
        d.setRamUso(dto.getRamUso());
        d.setRamTotal(dto.getRamTotal());
        d.setDiscoLivre(dto.getDiscoLivre());
        d.setDataHora(LocalDateTime.now());
        d.setStatus(analisar(dto));

        return repository.save(d);
    }

    private String analisar(ColetaDTO dto) {

        if (dto.getCpuUso() > 90)
            return "CRÍTICO: CPU muito alta";

        if (dto.getRamUso() > 85)
            return "ALERTA: RAM elevada";

        if (dto.getDiscoLivre() < 20)
            return "ALERTA: Pouco espaço em disco";

        return "OK";
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

        doc.add(new Paragraph("Relatório de Diagnóstico"));
        doc.add(new Paragraph("Data: " + d.getDataHora()));
        doc.add(new Paragraph("CPU: " + d.getCpuUso() + "%"));
        doc.add(new Paragraph("RAM: " + d.getRamUso() + "%"));
        doc.add(new Paragraph("Disco Livre: " + d.getDiscoLivre() + " GB"));
        doc.add(new Paragraph("Status: " + d.getStatus()));

        doc.close();
        return out.toByteArray();
    }
    
    
}
