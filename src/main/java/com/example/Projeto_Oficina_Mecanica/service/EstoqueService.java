package com.example.Projeto_Oficina_Mecanica.service;


import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.MovimentacaoEstoqueResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface EstoqueService {


    /**
     * Registra uma movimentação de estoque.
     *
     * ENTRADA:
     * aumenta estoque
     *
     * SAIDA:
     * reduz estoque
     *
     * DEVOLUCAO:
     * retorna produto ao estoque
     *
     * AJUSTE:
     * corrige saldo
     */
    MovimentacaoEstoqueResponseDTO movimentar(
            CriarMovimentacaoEstoqueRequestDTO dto
    );



    /**
     * Consulta uma movimentação específica.
     */
    MovimentacaoEstoqueResponseDTO buscarPorId(
            Long id
    );



    /**
     * Histórico de movimentações de um produto.
     */
    Page<MovimentacaoEstoqueResponseDTO> buscarHistoricoProduto(
            Long produtoId,
            Pageable pageable
    );



    /**
     * Lista movimentações por tipo.
     */
    Page<MovimentacaoEstoqueResponseDTO> buscarPorTipo(
            String tipo,
            Pageable pageable
    );

}
