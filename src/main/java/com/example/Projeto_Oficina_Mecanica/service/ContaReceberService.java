package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaReceberResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;

import java.util.List;

public interface ContaReceberService {

    ContaReceberResponseDTO criar(
            CriarContaReceberRequestDTO dto
    );

    ContaReceberResponseDTO buscarPorId(
            Long id
    );

    List<ContaReceberResponseDTO> listar();

    List<ContaReceberResponseDTO> buscarPorCliente(
            Long clienteId
    );

    List<ContaReceberResponseDTO> buscarPorStatus(
            StatusContaReceber status
    );

    ContaReceberResponseDTO atualizar(
            Long id,
            AtualizarContaReceberRequestDTO dto
    );

    ContaReceberResponseDTO registrarPagamento(
            Long id,
            AtualizarContaReceberRequestDTO dto
    );

    void excluir(
            Long id
    );

}