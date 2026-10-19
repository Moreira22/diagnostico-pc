package com.example.diagnostico_api.service;

import java.util.List;

import com.example.diagnostico_api.dto.ComputadorDTO;
import com.example.diagnostico_api.exeption.BusinessException;
import com.example.diagnostico_api.exeption.ResourceNotFoundException;
import com.example.diagnostico_api.mapper.ComputadorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Computador;
import com.example.diagnostico_api.entity.Setor;
import com.example.diagnostico_api.repository.ComputadorRepository;
import com.example.diagnostico_api.util.DataUtils;
import com.example.diagnostico_api.util.HardwareUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComputadorService {

    private final ComputadorRepository computadorRepository;
    private final ComputadorMapper computadorMapper;
    private final SetorService setorService;

    @Transactional(readOnly = true)
    public List<ComputadorDTO> listarTodos() {
        return computadorRepository.findAll().stream().map(computadorMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ComputadorDTO> listarPorSetor(Long setorId) {
        return computadorRepository.findBySetorId(setorId).stream().map(computadorMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ComputadorDTO> listarPorCliente(Long clienteId) {
        return computadorRepository.findBySetorClienteId(clienteId).stream().map(computadorMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ComputadorDTO> listarPorEmpresa(Long empresaId) {
        return computadorRepository.findBySetorClienteEmpresaId(empresaId).stream().map(computadorMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ComputadorDTO buscarPorId(Long id) {
        return computadorMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public ComputadorDTO criar(ComputadorDTO dto) {
        Computador computador = computadorMapper.toEntity(dto);
        computador.setId(null);
        computador.setSetor(buscarSetor(dto.getSetorId()));
        if (computador.getAtivo() == null) {
            computador.setAtivo(true);
        }
        preencherMetricas(computador);
        return computadorMapper.toDto(computadorRepository.save(computador));
    }

    @Transactional
    public ComputadorDTO atualizar(Long id, ComputadorDTO dto) {
        Computador existente = buscarEntidade(id);

        Computador computador = computadorMapper.toEntity(dto);
        computador.setId(id);
        computador.setSetor(buscarSetor(dto.getSetorId()));
        if (computador.getAtivo() == null) {
            computador.setAtivo(existente.getAtivo());
        }
        preencherMetricas(computador);
        return computadorMapper.toDto(computadorRepository.save(computador));
    }

    @Transactional
    public void deletar(Long id) {
        computadorRepository.delete(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Computador buscarEntidade(Long id) {
        return computadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Computador", id));
    }

    /**
     * Completa/normaliza as métricas antes de salvar:
     * - dataColeta: agora, se não informada
     * - memoriaUso: total - livre, se não informada
     * - cpuPorcentagem: limitada entre 0 e 100
     */
    private void preencherMetricas(Computador computador) {
        if (computador.getDataColeta() == null) {
            computador.setDataColeta(DataUtils.agora());
        }
        if (computador.getMemoriaUso() == null) {
            computador.setMemoriaUso(
                    HardwareUtils.calcularUso(computador.getMemoriaTotal(), computador.getMemoriaLivre()));
        }
        if (computador.getCpuPorcentagem() != null) {
            computador.setCpuPorcentagem(HardwareUtils.limitarPercentual(computador.getCpuPorcentagem()));
        }
    }

    private Setor buscarSetor(Long setorId) {
        if (setorId == null) {
            throw new BusinessException("setorId é obrigatório");
        }
        return setorService.buscarEntidade(setorId);
    }
}