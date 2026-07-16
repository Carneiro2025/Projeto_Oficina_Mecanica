package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FornecedorService {

    FornecedorResponseDTO criar(
            CriarFornecedorRequestDTO dto);

    Page<FornecedorResponseDTO> listar(
            String razaoSocial,
            String cnpj,
            String cidade,
            Pageable pageable);

    FornecedorResponseDTO buscarPorId(Long id);

    FornecedorResponseDTO atualizar(
            Long id,
            AtualizarFornecedorRequestDTO dto);

    void desativar(Long id);

    FornecedorResponseDTO reativar(Long id);

}
