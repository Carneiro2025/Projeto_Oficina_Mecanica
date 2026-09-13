package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.AlterarSenhaDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.UsuarioMapper;
import com.example.Projeto_Oficina_Mecanica.repository.UsuarioRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.UsuarioService;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de UsuarioServiceImplTest,
 * já que o UsuarioServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA é a auditoria em criar().
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final UsuarioMapper usuarioMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public UsuarioResponseDTO criar(CriarUsuarioRequestDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioLogado(),
                "CRIAR",
                "Usuario",
                salvo.getId(),
                "Usuário cadastrado: " + salvo.getEmail(),
                obterIp()
        );

        return usuarioMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioMapper.toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, AtualizarUsuarioRequestDTO dto) {

        Usuario usuario = buscarEntidadePorId(id);

        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new BusinessException("E-mail já cadastrado.");
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getNome() != null) {
            usuario.setNome(dto.getNome());
        }
        if (dto.getPerfil() != null) {
            usuario.setPerfil(dto.getPerfil());
        }

        Usuario salvo = usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioLogado(),
                "ATUALIZAR",
                "Usuario",
                salvo.getId(),
                "Usuário atualizado: " + salvo.getEmail(),
                obterIp()
        );

        return usuarioMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional
    public void desativar(Long id) {

        Usuario usuario = buscarEntidadePorId(id);

        if (!usuario.getAtivo()) {
            throw new BusinessException("Usuário já está inativo.");
        }

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioLogado(),
                "EXCLUIR",
                "Usuario",
                usuario.getId(),
                "Usuário desativado: " + usuario.getEmail(),
                obterIp()
        );
    }

    @Override
    @Transactional
    public void alterarSenha(Long id, AlterarSenhaDTO dto) {

        Usuario usuario = buscarEntidadePorId(id);

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new BusinessException("Senha atual inválida.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listar(String nome, PerfilUsuario perfil, Pageable pageable) {
        return usuarioRepository.buscarComFiltros(nome, perfil, pageable)
                .map(usuarioMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO reativar(Long id) {

        Usuario usuario = buscarEntidadePorId(id);

        if (usuario.getAtivo()) {
            throw new BusinessException("Usuário já está ativo.");
        }

        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioLogado(),
                "ATIVAR",
                "Usuario",
                salvo.getId(),
                "Usuário reativado: " + salvo.getEmail(),
                obterIp()
        );

        return usuarioMapper.toResponseDTO(salvo);
    }

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    private String usuarioLogado() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (principal instanceof Usuario) ? ((Usuario) principal).getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String obterIp() {
        try {
            var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            return (attrs != null) ? attrs.getRequest().getRemoteAddr() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
