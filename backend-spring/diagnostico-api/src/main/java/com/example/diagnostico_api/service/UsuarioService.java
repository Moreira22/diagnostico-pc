package com.example.diagnostico_api.service;

import java.util.List;

import com.example.diagnostico_api.dto.UsuarioDTO;
import com.example.diagnostico_api.exeption.BusinessException;
import com.example.diagnostico_api.exeption.ConflictException;
import com.example.diagnostico_api.exeption.ResourceNotFoundException;
import com.example.diagnostico_api.mapper.UsuarioMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.diagnostico_api.entity.Perfil;
import com.example.diagnostico_api.entity.Usuario;
import com.example.diagnostico_api.repository.PerfilRepository;
import com.example.diagnostico_api.repository.UsuarioRepository;
import com.example.diagnostico_api.util.SenhaUtils;
import com.example.diagnostico_api.util.TextoUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorPerfil(Long perfilId) {
        return usuarioRepository.findByPerfilId(perfilId).stream().map(usuarioMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        return usuarioMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public UsuarioDTO criar(UsuarioDTO dto, String senha) {
        if (senha == null || senha.isBlank()) {
            throw new BusinessException(
                    "A senha é obrigatória");
        }
        validarSenhaForte(senha);
        normalizarEValidar(dto);

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("E-mail já cadastrado: " + dto.getEmail());
        }
        if (usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new ConflictException("Login já cadastrado: " + dto.getLogin());
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setId(null);
        usuario.setPerfil(buscarPerfil(dto.getPerfilId()));
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPrimeiroAcesso(true);
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario existente = buscarEntidade(id);
        normalizarEValidar(dto);

        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail())
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("E-mail já cadastrado: " + dto.getEmail());
        }
        if (!existente.getLogin().equalsIgnoreCase(dto.getLogin())
                && usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new ConflictException("Login já cadastrado: " + dto.getLogin());
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setId(id);
        usuario.setSenha(existente.getSenha()); // senha não é alterada aqui
        usuario.setPerfil(buscarPerfil(dto.getPerfilId()));
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(existente.getAtivo());
        }
        if (usuario.getPrimeiroAcesso() == null) {
            usuario.setPrimeiroAcesso(existente.getPrimeiroAcesso());
        }
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public void alterarSenha(Long id, String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new BusinessException("A nova senha é obrigatória");
        }
        validarSenhaForte(novaSenha);

        Usuario usuario = buscarEntidade(id);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        usuarioRepository.delete(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    /** O perfil é opcional na entidade; se informado, precisa existir e estar ativo. */
    private Perfil buscarPerfil(Long perfilId) {
        if (perfilId == null) {
            return null;
        }
        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", perfilId));
        if (Boolean.FALSE.equals(perfil.getAtivo())) {
            throw new BusinessException("O perfil informado está inativo");
        }
        return perfil;
    }

    private void normalizarEValidar(UsuarioDTO dto) {
        dto.setEmail(TextoUtils.normalizarEmail(dto.getEmail()));
        dto.setLogin(dto.getLogin() == null ? null : dto.getLogin().trim().toLowerCase());
        dto.setTelefone(TextoUtils.formatarTelefone(dto.getTelefone()));

        if (TextoUtils.isNuloOuVazio(dto.getNome())) {
            throw new BusinessException("O nome é obrigatório");
        }
        if (TextoUtils.isNuloOuVazio(dto.getEmail())) {
            throw new BusinessException("O e-mail é obrigatório");
        }
        if (TextoUtils.isNuloOuVazio(dto.getLogin())) {
            throw new BusinessException("O login é obrigatório");
        }
    }

    private void validarSenhaForte(String senha) {
        if (!SenhaUtils.senhaForte(senha)) {
            throw new BusinessException(
                    "A senha deve ter no mínimo 8 caracteres, com letra maiúscula, minúscula e número");
        }
    }
}