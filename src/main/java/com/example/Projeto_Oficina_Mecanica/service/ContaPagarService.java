package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ContaPagarService {

    ContaPagarResponseDTO criar(
            CriarContaPagarRequestDTO dto
    );

    ContaPagarResponseDTO buscarPorId(
            Long id
    );

    Page<ContaPagarResponseDTO> listar(
            Pageable pageable
    );

    List<ContaPagarResponseDTO> buscarPorStatus(
            StatusContaPagar status
    );

    List<ContaPagarResponseDTO> buscarPorFornecedor(
            Long fornecedorId
    );

    List<ContaPagarResponseDTO> listarPendentes();

    ContaPagarResponseDTO atualizar(
            Long id,
            AtualizarContaPagarRequestDTO dto
    );

    ContaPagarResponseDTO registrarPagamento(
            Long id,
            AtualizarContaPagarRequestDTO dto
    );

    void excluir(
            Long id
    );

}