package com.example.diagnostico_api.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.diagnostico_api.service.DiagnosticoService;
import com.example.diagnostico_api.service.dto.ColetaDTO;
import com.example.diagnostico_api.service.dto.DiagnosticoDTO;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class DiagnosticoController {
    private final DiagnosticoService service;

    @PostMapping("/coleta")
    public ResponseEntity<Void> receber(@RequestBody ColetaDTO dto) {
        service.processar(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/historico")
    public List<DiagnosticoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/diagnostico/{id}/pdf")
public ResponseEntity<byte[]> pdf(@PathVariable Long id) {

    byte[] pdf = service.gerarPDF(id);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diagnostico.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdf);
    }
    
}
