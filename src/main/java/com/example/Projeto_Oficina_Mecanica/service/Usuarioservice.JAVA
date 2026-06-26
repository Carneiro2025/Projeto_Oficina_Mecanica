package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.UsuarioMapper;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de gerenciamento de Usuários.
 *
 * Regras de Negócio:
 *  RN01 - Apenas ADMIN gerencia usuários (garantido no SecurityConfig + @PreAuthorize)
 *  RN02 - E-mail deve ser único no sistema
 *  RN03 - Não excluir fisicamente — soft-delete (ativo = false)
 *  RN04 - Senha sempre criptografada com BCrypt antes de salvar
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // ── Criar ─────────────────────────────────────────────────────────────────

    @Transactional
    public UsuarioResponseDTO criar(CriarUsuarioRequestDTO dto) {
        // RN02 — E-mail único
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + dto.getEmail());
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // RN04
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuário criado: {} [{}]", salvo.getEmail(), salvo.getPerfil());
        return usuarioMapper.toResponseDTO(salvo);
    }

    // ── Listar ────────────────────────────────────────────────────────────────

    public Page<UsuarioResponseDTO> listar(String nome, PerfilUsuario perfil, Pageable pageable) {
        return usuarioRepository
                .buscarComFiltros(nome, perfil, pageable)
                .map(usuarioMapper::toResponseDTO);
    }

    // ── Buscar por ID ─────────────────────────────────────────────────────────

    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioMapper.toResponseDTO(buscarEntidade(id));
    }

    // ── Atualizar ─────────────────────────────────────────────────────────────

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, AtualizarUsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);

        // Valida novo e-mail se foi alterado
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new BusinessException("E-mail já cadastrado: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getNome() != null)   usuario.setNome(dto.getNome());
        if (dto.getPerfil() != null) usuario.setPerfil(dto.getPerfil());
        if (dto.getSenha() != null)  usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        log.info("Usuário atualizado: ID {}", id);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    // ── Desativar (soft-delete) ───────────────────────────────────────────────

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        if (!usuario.getAtivo()) {
            throw new BusinessException("Usuário já está inativo");
        }
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuário desativado: ID {}", id);
    }

    // ── Reativar ──────────────────────────────────────────────────────────────

    @Transactional
    public UsuarioResponseDTO reativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(true);
        log.info("Usuário reativado: ID {}", id);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }
}