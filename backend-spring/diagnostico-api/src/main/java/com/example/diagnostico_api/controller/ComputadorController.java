package com.example.diagnostico_api.controller;

import java.net.URI;
import java.util.List;

import com.example.diagnostico_api.dto.ComputadorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.diagnostico_api.service.ComputadorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/computadores")
@RequiredArgsConstructor
public class ComputadorController {

    private final ComputadorService computadorService;

    /**
     * Filtros opcionais (usa o mais específico informado): setorId, clienteId ou empresaId.
     */
    @GetMapping
    public ResponseEntity<List<ComputadorDTO>> listar(
            @RequestParam(required = false) Long setorId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long empresaId) {

        List<ComputadorDTO> computadores;
        if (setorId != null) {
            computadores = computadorService.listarPorSetor(setorId);
        } else if (clienteId != null) {
            computadores = computadorService.listarPorCliente(clienteId);
        } else if (empresaId != null) {
            computadores = computadorService.listarPorEmpresa(empresaId);
        } else {
            computadores = computadorService.listarTodos();
        }
        return ResponseEntity.ok(computadores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComputadorDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(computadorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ComputadorDTO> criar(@RequestBody ComputadorDTO dto) {
        ComputadorDTO criado = computadorService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComputadorDTO> atualizar(@PathVariable Long id, @RequestBody ComputadorDTO dto) {
        return ResponseEntity.ok(computadorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        computadorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}