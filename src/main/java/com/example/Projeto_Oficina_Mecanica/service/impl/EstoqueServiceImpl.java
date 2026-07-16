package com.example.Projeto_Oficina_Mecanica.service.impl;


import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.MovimentacaoEstoqueResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.MovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.MovimentacaoEstoqueMapper;
import com.example.Projeto_Oficina_Mecanica.repository.MovimentacaoEstoqueRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.service.EstoqueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstoqueServiceImpl implements EstoqueService {



    private final ProdutoRepository produtoRepository;

    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    private final MovimentacaoEstoqueMapper mapper;



    // ==========================================================
    // REGISTRAR MOVIMENTAÇÃO
    // ==========================================================

    @Override
    @Transactional
    public MovimentacaoEstoqueResponseDTO movimentar(
            CriarMovimentacaoEstoqueRequestDTO dto) {


        Produto produto =
                buscarProduto(dto.getProdutoId());



        validarQuantidade(dto.getQuantidade());



        aplicarMovimentacao(
                produto,
                dto.getTipo(),
                dto.getQuantidade()
        );



        produtoRepository.save(produto);



        MovimentacaoEstoque movimentacao =
                mapper.toEntity(dto);



        movimentacao.setProduto(produto);



        MovimentacaoEstoque salva =
                movimentacaoRepository.save(
                        movimentacao
                );



        log.info(
                "Movimentação {} registrada para produto {}",
                dto.getTipo(),
                produto.getDescricao()
        );


        return mapper.toResponseDTO(
                salva
        );

    }
    
    // ==========================================================
// APLICAR MOVIMENTAÇÃO NO ESTOQUE
// ==========================================================

private void aplicarMovimentacao(
        Produto produto,
        TipoMovimentacaoEstoque tipo,
        Integer quantidade) {


    Integer estoqueAtual =
            produto.getEstoqueAtual() == null
                    ? 0
                    : produto.getEstoqueAtual();



    switch (tipo) {


        case ENTRADA:

            produto.setEstoqueAtual(
                    estoqueAtual + quantidade
            );

            break;



        case SAIDA:


            if (estoqueAtual < quantidade) {

                throw new BusinessException(
                        "Estoque insuficiente para realizar a saída."
                );

            }


            produto.setEstoqueAtual(
                    estoqueAtual - quantidade
            );

            break;



        case DEVOLUCAO:


            produto.setEstoqueAtual(
                    estoqueAtual + quantidade
            );

            break;



        case AJUSTE:


            produto.setEstoqueAtual(
                    quantidade
            );

            break;



        default:

            throw new BusinessException(
                    "Tipo de movimentação não informado."
            );

    }

}

// ==========================================================
// BUSCAR MOVIMENTAÇÃO POR ID
// ==========================================================

@Override
public MovimentacaoEstoqueResponseDTO buscarPorId(Long id) {


    MovimentacaoEstoque movimentacao =
            movimentacaoRepository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Movimentação de estoque",
                                    id
                            ));


    return mapper.toResponseDTO(
            movimentacao
    );

}



// ==========================================================
// HISTÓRICO DO PRODUTO
// ==========================================================

@Override
public Page<MovimentacaoEstoqueResponseDTO> buscarHistoricoProduto(
        Long produtoId,
        Pageable pageable) {


    return movimentacaoRepository
            .findByProdutoId(
                    produtoId,
                    pageable
            )
            .map(
                    mapper::toResponseDTO
            );

}



// ==========================================================
// BUSCAR POR TIPO
// ==========================================================

@Override
public Page<MovimentacaoEstoqueResponseDTO> buscarPorTipo(
        String tipo,
        Pageable pageable) {


    TipoMovimentacaoEstoque tipoMovimentacao;


    try {

        tipoMovimentacao =
                TipoMovimentacaoEstoque.valueOf(
                        tipo.toUpperCase()
                );


    } catch (Exception e) {

        throw new BusinessException(
                "Tipo de movimentação inválido."
        );

    }



    return movimentacaoRepository
            .findByTipo(
                    tipoMovimentacao
            )
            .stream()
            .map(
                    mapper::toResponseDTO
            )
            .collect(
                    java.util.stream.Collectors
                    .collectingAndThen(
                            java.util.stream.Collectors
                            .toList(),
                            lista -> new org.springframework.data.domain.PageImpl<>(
                                    lista,
                                    pageable,
                                    lista.size()
                            )
                    )
            );

}



// ==========================================================
// BUSCAR PRODUTO
// ==========================================================

private Produto buscarProduto(Long id) {


    return produtoRepository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Produto",
                            id
                    ));

}



// ==========================================================
// VALIDAR QUANTIDADE
// ==========================================================

private void validarQuantidade(Integer quantidade) {


    if (quantidade == null || quantidade <= 0) {


        throw new BusinessException(
                "A quantidade deve ser maior que zero."
        );

    }

}

}