package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ProdutoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * ==========================================================
 * SERVICE - PRODUTO
 * ==========================================================
 *
 * Regras de negócio:
 *
 * RN01 - Código do produto deve ser único.
 *
 * RN02 - Produto deve possuir fornecedor válido quando informado.
 *
 * RN03 - Produto utiliza exclusão lógica.
 *
 * RN04 - Estoque nunca pode ser negativo.
 *
 * RN05 - Margem de lucro calculada automaticamente.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {


    private final ProdutoRepository produtoRepository;

    private final FornecedorRepository fornecedorRepository;

    private final ProdutoMapper produtoMapper;


    // ==========================================================
    // CRIAR PRODUTO
    // ==========================================================

    @Override
    @Transactional
    public ProdutoResponseDTO criar(
            CriarProdutoRequestDTO dto) {


        validarCodigo(dto.getCodigo());


        Produto produto =
                produtoMapper.toEntity(dto);



        // ------------------------------------------------------
        // Vincular fornecedor
        // ------------------------------------------------------

        if (dto.getFornecedorId() != null) {

            Fornecedor fornecedor =
                    fornecedorRepository.findById(
                            dto.getFornecedorId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fornecedor",
                                    dto.getFornecedorId()
                            ));

            produto.setFornecedor(fornecedor);

        }


        // ------------------------------------------------------
        // Valores iniciais
        // ------------------------------------------------------

        if (produto.getEstoqueAtual() == null) {

            produto.setEstoqueAtual(0);

        }


        produto.setAtivo(true);



        Produto salvo =
                produtoRepository.save(produto);



        log.info(
                "Produto {} cadastrado com sucesso.",
                salvo.getDescricao()
        );


        return montarResponse(salvo);

    }
    // ==========================================================
// LISTAR PRODUTOS
// ==========================================================

@Override
public Page<ProdutoResponseDTO> listar(
        String descricao,
        String codigo,
        CategoriaProduto categoria,
        Long fornecedorId,
        Pageable pageable) {


    return produtoRepository
            .buscarComFiltros(
                    descricao,
                    codigo,
                    categoria,
                    fornecedorId,
                    pageable
            )
            .map(this::montarResponse);

}


// ==========================================================
// BUSCAR POR ID
// ==========================================================

@Override
public ProdutoResponseDTO buscarPorId(Long id) {


    Produto produto =
            buscarEntidade(id);


    return montarResponse(produto);

}


// ==========================================================
// ATUALIZAR PRODUTO
// ==========================================================

@Override
@Transactional
public ProdutoResponseDTO atualizar(
        Long id,
        AtualizarProdutoRequestDTO dto) {


    Produto produto =
            buscarEntidade(id);



    // ------------------------------------------------------
    // Atualização dos campos simples
    // ------------------------------------------------------

    produtoMapper.updateEntity(
            dto,
            produto
    );



    // ------------------------------------------------------
    // Atualizar fornecedor
    // ------------------------------------------------------

    if(dto.getFornecedorId() != null) {


        Fornecedor fornecedor =
                fornecedorRepository.findById(
                        dto.getFornecedorId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fornecedor",
                                dto.getFornecedorId()
                        ));



        produto.setFornecedor(
                fornecedor
        );

    }



    Produto atualizado =
            produtoRepository.save(produto);



    log.info(
            "Produto {} atualizado com sucesso.",
            atualizado.getDescricao()
    );


    return montarResponse(atualizado);

}

// ==========================================================
// DESATIVAR PRODUTO
// ==========================================================

@Override
@Transactional
public void desativar(Long id) {

    Produto produto = buscarEntidade(id);


    if (!produto.getAtivo()) {

        throw new BusinessException(
                "O produto já está desativado."
        );

    }


    produto.setAtivo(false);


    produtoRepository.save(produto);


    log.info(
            "Produto {} desativado.",
            produto.getDescricao()
    );

}


// ==========================================================
// REATIVAR PRODUTO
// ==========================================================

@Override
@Transactional
public ProdutoResponseDTO reativar(Long id) {


    Produto produto =
            buscarEntidade(id);



    if (produto.getAtivo()) {

        throw new BusinessException(
                "O produto já está ativo."
        );

    }


    produto.setAtivo(true);


    Produto atualizado =
            produtoRepository.save(produto);



    log.info(
            "Produto {} reativado.",
            atualizado.getDescricao()
    );


    return montarResponse(atualizado);

}


// ==========================================================
// BUSCAR ENTIDADE
// ==========================================================

private Produto buscarEntidade(Long id) {


    return produtoRepository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Produto",
                            id
                    ));

}



// ==========================================================
// VALIDAR CÓDIGO
// ==========================================================

private void validarCodigo(String codigo) {


    if (produtoRepository.existsByCodigo(codigo)) {


        throw new BusinessException(
                "Já existe um produto cadastrado com este código."
        );

    }

}



// ==========================================================
// MONTAR RESPONSE DTO
// ==========================================================

private ProdutoResponseDTO montarResponse(
        Produto produto) {


    ProdutoResponseDTO response =
            produtoMapper.toResponseDTO(produto);



    // ------------------------------------------------------
    // Calcula margem de lucro
    // ------------------------------------------------------

    response.setMargemLucroPercent(
            calcularMargem(produto)
    );



    // ------------------------------------------------------
    // Verifica estoque mínimo
    // ------------------------------------------------------

    response.setEstoqueAbaixoMinimo(
            verificarEstoqueMinimo(produto)
    );


    return response;

}



// ==========================================================
// CALCULAR MARGEM DE LUCRO
// ==========================================================

private BigDecimal calcularMargem(
        Produto produto) {


    if(produto.getPrecoCusto() == null ||
       produto.getPrecoVenda() == null ||
       produto.getPrecoCusto().compareTo(BigDecimal.ZERO) == 0) {


        return BigDecimal.ZERO;

    }



    BigDecimal lucro =
            produto.getPrecoVenda()
                    .subtract(produto.getPrecoCusto());



    return lucro
            .divide(
                    produto.getPrecoCusto(),
                    4,
                    RoundingMode.HALF_UP
            )
            .multiply(
                    BigDecimal.valueOf(100)
            )
            .setScale(
                    2,
                    RoundingMode.HALF_UP
            );

}



// ==========================================================
// VERIFICAR ESTOQUE MÍNIMO
// ==========================================================

private Boolean verificarEstoqueMinimo(
        Produto produto) {


    if(produto.getEstoqueAtual() == null ||
       produto.getEstoqueMinimo() == null) {

        return false;

    }


    return produto.getEstoqueAtual()
            <= produto.getEstoqueMinimo();

}

}
