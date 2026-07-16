package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrdemServicoService {

    OrdemServicoResponseDTO criar(
            CriarOrdemServicoRequestDTO dto
    );

    OrdemServicoResponseDTO buscarPorId(
            Long id
    );

    Page<OrdemServicoResponseDTO> listar(
            Pageable pageable
    );

    Page<OrdemServicoResponseDTO> buscarPorCliente(
            Long clienteId,
            Pageable pageable
    );

    Page<OrdemServicoResponseDTO> buscarPorVeiculo(
            Long veiculoId,
            Pageable pageable
    );

    Page<OrdemServicoResponseDTO> buscarPorStatus(
            StatusOrdemServico status,
            Pageable pageable
    );

    Page<OrdemServicoResponseDTO> buscarComFiltros(
            String numero,
            StatusOrdemServico status,
            Long clienteId,
            Long veiculoId,
            LocalDate dataInicial,
            LocalDate dataFinal,
            Pageable pageable
    );

    OrdemServicoResponseDTO atualizar(
            Long id,
            AtualizarOrdemServicoRequestDTO dto
    );

    OrdemServicoResponseDTO finalizar(
            Long id
    );

    void cancelar(
            Long id
    );

}