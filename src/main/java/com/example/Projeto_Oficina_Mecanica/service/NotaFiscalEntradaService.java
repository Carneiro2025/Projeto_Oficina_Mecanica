package com.example.Projeto_Oficina_Mecanica.service;


import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.NotaFiscalEntradaResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface NotaFiscalEntradaService {



    /**
     * Cadastra uma nova Nota Fiscal de Entrada.
     *
     * O processo irá:
     * - validar fornecedor;
     * - validar produtos;
     * - salvar nota;
     * - atualizar estoque;
     * - registrar movimentações.
     */
    NotaFiscalEntradaResponseDTO criar(
            CriarNotaFiscalEntradaRequestDTO dto
    );



    /**
     * Busca uma nota fiscal pelo ID.
     */
    NotaFiscalEntradaResponseDTO buscarPorId(
            Long id
    );



    /**
     * Lista notas fiscais paginadas.
     */
    Page<NotaFiscalEntradaResponseDTO> listar(
            Pageable pageable
    );



    /**
     * Atualiza dados administrativos da nota.
     *
     * Não altera itens nem movimentação de estoque.
     */
    NotaFiscalEntradaResponseDTO atualizar(
            Long id,
            AtualizarNotaFiscalEntradaRequestDTO dto
    );



    /**
     * Busca notas de um fornecedor específico.
     */
    Page<NotaFiscalEntradaResponseDTO> buscarPorFornecedor(
            Long fornecedorId,
            Pageable pageable
    );

}
