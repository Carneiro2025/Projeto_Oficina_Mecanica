package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFluxoCaixaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFluxoCaixaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FluxoCaixaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface FluxoCaixaService {

    FluxoCaixaResponseDTO criar(
            CriarFluxoCaixaRequestDTO dto
    );

    FluxoCaixaResponseDTO buscarPorId(
            Long id
    );

    Page<FluxoCaixaResponseDTO> listar(
            Pageable pageable
    );

    List<FluxoCaixaResponseDTO> buscarPorTipo(
            TipoMovimentacaoCaixa tipo
    );

    List<FluxoCaixaResponseDTO> buscarPorPeriodo(
            LocalDate inicio,
            LocalDate fim
    );

    List<FluxoCaixaResponseDTO> buscarPorCliente(
            Long clienteId
    );

    List<FluxoCaixaResponseDTO> buscarPorFornecedor(
            Long fornecedorId
    );

    FluxoCaixaResponseDTO atualizar(
            Long id,
            AtualizarFluxoCaixaRequestDTO dto
    );

    void excluir(
            Long id
    );

}
