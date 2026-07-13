package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.AlterarSenhaDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    UsuarioResponseDTO criar(CriarUsuarioRequestDTO dto);

    UsuarioResponseDTO buscarPorId(Long id);

    Page<UsuarioResponseDTO> listar(
            String nome,
            PerfilUsuario perfil,
            Pageable pageable);

    UsuarioResponseDTO atualizar(
            Long id,
            AtualizarUsuarioRequestDTO dto);

    void alterarSenha(
            Long id,
            AlterarSenhaDTO dto);

    void desativar(Long id);

    UsuarioResponseDTO reativar(Long id);
}
