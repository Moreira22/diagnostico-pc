package com.example.diagnostico_api.service;

import java.util.List;

import com.example.diagnostico_api.dto.ClienteDTO;
import com.example.diagnostico_api.exeption.BusinessException;
import com.example.diagnostico_api.exeption.ResourceNotFoundException;
import com.example.diagnostico_api.mapper.ClienteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Cliente;
import com.example.diagnostico_api.entity.Empresa;
import com.example.diagnostico_api.repository.ClienteRepository;
import com.example.diagnostico_api.util.TextoUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final EmpresaService empresaService;

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream().map(clienteMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarPorEmpresa(Long empresaId) {
        return clienteRepository.findByEmpresaId(empresaId).stream().map(clienteMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        return clienteMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public ClienteDTO criar(ClienteDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setId(null);
        cliente.setTelefone(TextoUtils.formatarTelefone(dto.getTelefone()));
        cliente.setEmpresa(buscarEmpresa(dto.getEmpresaId()));
        if (cliente.getAtivo() == null) {
            cliente.setAtivo(true);
        }
        return clienteMapper.toDto(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente existente = buscarEntidade(id);

        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setId(id);
        cliente.setTelefone(TextoUtils.formatarTelefone(dto.getTelefone()));
        cliente.setEmpresa(buscarEmpresa(dto.getEmpresaId()));
        if (cliente.getAtivo() == null) {
            cliente.setAtivo(existente.getAtivo());
        }
        return clienteMapper.toDto(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(Long id) {
        clienteRepository.delete(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    private Empresa buscarEmpresa(Long empresaId) {
        if (empresaId == null) {
            throw new BusinessException("empresaId é obrigatório");
        }
        return empresaService.buscarEntidade(empresaId);
    }
}
