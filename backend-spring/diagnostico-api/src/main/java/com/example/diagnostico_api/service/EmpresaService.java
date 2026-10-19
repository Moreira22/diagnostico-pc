package com.example.diagnostico_api.service;


import java.util.List;

import com.example.diagnostico_api.dto.EmpresaDTO;
import com.example.diagnostico_api.exeption.ResourceNotFoundException;
import com.example.diagnostico_api.mapper.EmpresaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Empresa;
import com.example.diagnostico_api.repository.EmpresaRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarTodas() {
        return empresaRepository.findAll().stream().map(empresaMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarAtivas() {
        return empresaRepository.findByAtivoTrue().stream().map(empresaMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaDTO buscarPorId(Long id) {
        return empresaMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public EmpresaDTO criar(EmpresaDTO dto) {
        Empresa empresa = empresaMapper.toEntity(dto);
        empresa.setId(null);
        if (empresa.getAtivo() == null) {
            empresa.setAtivo(true);
        }
        return empresaMapper.toDto(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaDTO atualizar(Long id, EmpresaDTO dto) {
        Empresa existente = buscarEntidade(id);

        Empresa empresa = empresaMapper.toEntity(dto);
        empresa.setId(id);
        if (empresa.getAtivo() == null) {
            empresa.setAtivo(existente.getAtivo());
        }
        return empresaMapper.toDto(empresaRepository.save(empresa));
    }

    @Transactional
    public void deletar(Long id) {
        empresaRepository.delete(buscarEntidade(id));
    }

    /** Usado por outros services que precisam da entidade gerenciada. */
    @Transactional(readOnly = true)
    public Empresa buscarEntidade(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", id));
    }
}