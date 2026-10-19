package com.example.diagnostico_api.controller;

import java.net.URI;
import java.util.List;

import com.example.diagnostico_api.dto.SetorDTO;
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

import com.example.diagnostico_api.service.SetorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/setores")
@RequiredArgsConstructor
public class SetorController {

    private final SetorService setorService;

    @GetMapping
    public ResponseEntity<List<SetorDTO>> listar(@RequestParam(required = false) Long clienteId) {
        List<SetorDTO> setores = clienteId != null
                ? setorService.listarPorCliente(clienteId)
                : setorService.listarTodos();
        return ResponseEntity.ok(setores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetorDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(setorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SetorDTO> criar(@RequestBody SetorDTO dto) {
        SetorDTO criado = setorService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetorDTO> atualizar(@PathVariable Long id, @RequestBody SetorDTO dto) {
        return ResponseEntity.ok(setorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        setorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}