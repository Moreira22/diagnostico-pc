package com.example.diagnostico_api.service;

import java.util.List;

import com.example.diagnostico_api.dto.SetorDTO;
import com.example.diagnostico_api.exeption.BusinessException;
import com.example.diagnostico_api.exeption.ResourceNotFoundException;
import com.example.diagnostico_api.mapper.SetorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Cliente;
import com.example.diagnostico_api.entity.Setor;
import com.example.diagnostico_api.repository.SetorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetorService {

    private final SetorRepository setorRepository;
    private final SetorMapper setorMapper;
    private final ClienteService clienteService;

    @Transactional(readOnly = true)
    public List<SetorDTO> listarTodos() {
        return setorRepository.findAll().stream().map(setorMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<SetorDTO> listarPorCliente(Long clienteId) {
        return setorRepository.findByClienteId(clienteId).stream().map(setorMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SetorDTO buscarPorId(Long id) {
        return setorMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public SetorDTO criar(SetorDTO dto) {
        Setor setor = setorMapper.toEntity(dto);
        setor.setId(null);
        setor.setCliente(buscarCliente(dto.getClienteId()));
        if (setor.getAtivo() == null) {
            setor.setAtivo(true);
        }
        return setorMapper.toDto(setorRepository.save(setor));
    }

    @Transactional
    public SetorDTO atualizar(Long id, SetorDTO dto) {
        Setor existente = buscarEntidade(id);

        Setor setor = setorMapper.toEntity(dto);
        setor.setId(id);
        setor.setCliente(buscarCliente(dto.getClienteId()));
        if (setor.getAtivo() == null) {
            setor.setAtivo(existente.getAtivo());
        }
        return setorMapper.toDto(setorRepository.save(setor));
    }

    @Transactional
    public void deletar(Long id) {
        setorRepository.delete(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Setor buscarEntidade(Long id) {
        return setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor", id));
    }

    private Cliente buscarCliente(Long clienteId) {
        if (clienteId == null) {
            throw new BusinessException("clienteId é obrigatório");
        }
        return clienteService.buscarEntidade(clienteId);
    }
}