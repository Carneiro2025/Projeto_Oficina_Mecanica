package com.example.Projeto_Oficina_Mecanica.service.impl;


import com.example.Projeto_Oficina_Mecanica.dto.request.*;
import com.example.Projeto_Oficina_Mecanica.dto.response.NotaFiscalEntradaResponseDTO;

import com.example.Projeto_Oficina_Mecanica.entity.*;

import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;

import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;

import com.example.Projeto_Oficina_Mecanica.mapper.NotaFiscalEntradaMapper;

import com.example.Projeto_Oficina_Mecanica.repository.*;

import com.example.Projeto_Oficina_Mecanica.service.EstoqueService;
import com.example.Projeto_Oficina_Mecanica.service.NotaFiscalEntradaService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.ArrayList;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotaFiscalEntradaServiceImpl 
        implements NotaFiscalEntradaService {



    private final NotaFiscalEntradaRepository repository;

    private final FornecedorRepository fornecedorRepository;

    private final ProdutoRepository produtoRepository;

    private final NotaFiscalEntradaMapper mapper;

    private final EstoqueService estoqueService;



    // ==========================================================
    // CRIAR NOTA FISCAL DE ENTRADA
    // ==========================================================


    @Override
    @Transactional
    public NotaFiscalEntradaResponseDTO criar(
            CriarNotaFiscalEntradaRequestDTO dto
    ) {


        log.info(
                "Iniciando cadastro da NF {}",
                dto.getNumero()
        );


        validarNumeroNota(
                dto.getNumero()
        );



        Fornecedor fornecedor =
                fornecedorRepository.findById(
                        dto.getFornecedorId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fornecedor",
                                dto.getFornecedorId()
                        )
                );



        NotaFiscalEntrada nota =
                NotaFiscalEntrada.builder()

                        .numero(
                                dto.getNumero()
                        )

                        .fornecedor(
                                fornecedor
                        )

                        .dataEmissao(
                                dto.getDataEmissao()
                        )

                        .dataEntrada(
                                LocalDate.now()
                        )

                        .observacoes(
                                dto.getObservacoes()
                        )

                        .itens(
                                new ArrayList<>()
                        )

                        .build();



        criarItens(
                nota,
                dto.getItens()
        );



        nota.calcularTotal();



        NotaFiscalEntrada salva =
                repository.save(
                        nota
                );



        atualizarEstoque(
                salva
        );



        log.info(
                "NF {} cadastrada com sucesso",
                salva.getNumero()
        );



        return mapper.toResponseDTO(
                salva
        );

    }




    // ==========================================================
    // CRIAR ITENS DA NOTA
    // ==========================================================


    private void criarItens(
            NotaFiscalEntrada nota,
            java.util.List<ItemNotaFiscalEntradaRequestDTO> itensDTO
    ) {



        for(
            ItemNotaFiscalEntradaRequestDTO itemDTO 
            : itensDTO
        ){



            Produto produto =
                    buscarProduto(
                            itemDTO.getProdutoId()
                    );



            ItemNotaFiscalEntrada item =
                    ItemNotaFiscalEntrada.builder()

                    .notaFiscalEntrada(
                            nota
                    )

                    .produto(
                            produto
                    )

                    .quantidade(
                            itemDTO.getQuantidade()
                    )

                    .valorUnitario(
                            itemDTO.getValorUnitario()
                    )

                    .build();



            item.calcularSubtotal();



            nota.getItens()
                    .add(item);

        }

    }





    // ==========================================================
    // BUSCAR PRODUTO
    // ==========================================================


    private Produto buscarProduto(
            Long id
    ){


        return produtoRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Produto",
                                id
                        )
                );

    }





    // ==========================================================
    // ATUALIZAR ESTOQUE
    // ==========================================================


    private void atualizarEstoque(
            NotaFiscalEntrada nota
    ){


        for(
            ItemNotaFiscalEntrada item 
            : nota.getItens()
        ){



            CriarMovimentacaoEstoqueRequestDTO movimentacao =
                    new CriarMovimentacaoEstoqueRequestDTO();



            movimentacao.setProdutoId(
                    item.getProduto().getId()
            );



            movimentacao.setQuantidade(
                    item.getQuantidade()
            );



            movimentacao.setTipo(
                    TipoMovimentacaoEstoque.ENTRADA
            );



            movimentacao.setObservacao(
                    "Entrada NF "
                    + nota.getNumero()
            );



            estoqueService.movimentar(
                    movimentacao
            );


        }

    }





    // ==========================================================
    // VALIDAR NUMERO DA NOTA
    // ==========================================================


    private void validarNumeroNota(
            String numero
    ){



        if(repository.existsByNumero(numero)){


            throw new BusinessException(
                    "Já existe uma Nota Fiscal com número "
                    + numero
            );


        }


    }

   // ==========================================================
// BUSCAR POR ID
// ==========================================================

@Override
public NotaFiscalEntradaResponseDTO buscarPorId(Long id) {

    NotaFiscalEntrada nota = repository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Nota Fiscal",
                            id
                    )
            );

    return mapper.toResponseDTO(nota);
}

// ==========================================================
// LISTAR
// ==========================================================

@Override
public Page<NotaFiscalEntradaResponseDTO> listar(Pageable pageable) {

    return repository
            .findAll(pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// ATUALIZAR
// ==========================================================

@Override
@Transactional
public NotaFiscalEntradaResponseDTO atualizar(
        Long id,
        AtualizarNotaFiscalEntradaRequestDTO dto
) {

    NotaFiscalEntrada nota =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Nota Fiscal",
                                    id
                            )
                    );



    if(dto.getNumero() != null &&
            !dto.getNumero().equals(nota.getNumero())){

        validarNumeroNota(dto.getNumero());

        nota.setNumero(dto.getNumero());

    }



    if(dto.getDataEmissao() != null){

        nota.setDataEmissao(
                dto.getDataEmissao()
        );

    }



    if(dto.getObservacoes() != null){

        nota.setObservacoes(
                dto.getObservacoes()
        );

    }



    NotaFiscalEntrada atualizada =
            repository.save(nota);



    log.info(
            "Nota Fiscal {} atualizada.",
            atualizada.getNumero()
    );



    return mapper.toResponseDTO(
            atualizada
    );

}

// ==========================================================
// BUSCAR POR FORNECEDOR
// ==========================================================

@Override
public Page<NotaFiscalEntradaResponseDTO> buscarPorFornecedor(
        Long fornecedorId,
        Pageable pageable
) {

    return repository
            .findByFornecedorId(
                    fornecedorId,
                    pageable
            )
            .map(mapper::toResponseDTO);

}

}