package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProdutoService {

    /**
     * Cadastra um novo produto.
     */
    ProdutoResponseDTO criar(
            CriarProdutoRequestDTO dto
    );


    /**
     * Lista produtos com filtros e paginação.
     */
    Page<ProdutoResponseDTO> listar(
            String descricao,
            String codigo,
            CategoriaProduto categoria,
            Long fornecedorId,
            Pageable pageable
    );


    /**
     * Busca produto pelo ID.
     */
    ProdutoResponseDTO buscarPorId(
            Long id
    );


    /**
     * Atualiza dados do produto.
     */
    ProdutoResponseDTO atualizar(
            Long id,
            AtualizarProdutoRequestDTO dto
    );


    /**
     * Desativa produto (soft delete).
     */
    void desativar(
            Long id
    );


    /**
     * Reativa produto.
     */
    ProdutoResponseDTO reativar(
            Long id
    );

}
