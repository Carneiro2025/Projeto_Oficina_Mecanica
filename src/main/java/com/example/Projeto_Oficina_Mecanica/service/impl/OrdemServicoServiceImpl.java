package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.*;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.*;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.TipoItemOrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.OrdemServicoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.*;
import com.example.Projeto_Oficina_Mecanica.service.EstoqueService;
import com.example.Projeto_Oficina_Mecanica.service.OrdemServicoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdemServicoServiceImpl
        implements OrdemServicoService {

    private final OrdemServicoRepository repository;

    private final ClienteRepository clienteRepository;

    private final VeiculoRepository veiculoRepository;

    private final ProdutoRepository produtoRepository;

    private final OrdemServicoMapper mapper;

    private final EstoqueService estoqueService;

        @Override
    @Transactional
    public OrdemServicoResponseDTO criar(
            CriarOrdemServicoRequestDTO dto
    ) {

        validarNumero(dto.getNumero());

        Cliente cliente =
                clienteRepository.findById(dto.getClienteId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cliente",
                                        dto.getClienteId()
                                ));

        Veiculo veiculo =
                veiculoRepository.findById(dto.getVeiculoId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veículo",
                                        dto.getVeiculoId()
                                ));

        OrdemServico ordem =
                OrdemServico.builder()

                        .numero(dto.getNumero())

                        .cliente(cliente)

                        .veiculo(veiculo)

                        .mecanicoResponsavel(dto.getMecanicoResponsavel())

                        .previsaoEntrega(dto.getPrevisaoEntrega())

                        .quilometragem(dto.getQuilometragem())

                        .observacoes(dto.getObservacoes())

                        .valorDesconto(dto.getValorDesconto())

                        .status(StatusOrdemServico.ABERTA)

                        .itens(new ArrayList<>())

                        .build();

        criarItens(
                ordem,
                dto
        );

        ordem.calcularTotal();

        OrdemServico salva =
                repository.save(ordem);

        return mapper.toResponseDTO(salva);

    }

        private void criarItens(

            OrdemServico ordem,

            CriarOrdemServicoRequestDTO dto

    ) {

        dto.getItens()

                .forEach(itemDTO -> {

                    Produto produto = null;

                    if(itemDTO.getProdutoId() != null){

                        produto =
                                buscarProduto(
                                        itemDTO.getProdutoId()
                                );

                    }

                    ItemOrdemServico item =
                            ItemOrdemServico.builder()

                                    .ordemServico(ordem)

                                    .produto(produto)

                                    .tipoItem(itemDTO.getTipoItem())

                                    .descricaoServico(
                                            itemDTO.getDescricaoServico()
                                    )

                                    .quantidade(
                                            itemDTO.getQuantidade()
                                    )

                                    .valorUnitario(
                                            itemDTO.getValorUnitario()
                                    )

                                    .build();

                    item.calcularSubtotal();

                    ordem.getItens().add(item);

                    if(itemDTO.getTipoItem()
                            == TipoItemOrdemServico.PECA){

                        movimentarEstoque(itemDTO);

                    }

                });

    }

        private void movimentarEstoque(

            ItemOrdemServicoRequestDTO itemDTO

    ){

        CriarMovimentacaoEstoqueRequestDTO dto =
                new CriarMovimentacaoEstoqueRequestDTO();

        dto.setProdutoId(
                itemDTO.getProdutoId()
        );

        dto.setQuantidade(
                itemDTO.getQuantidade()
        );

        dto.setTipo(
                TipoMovimentacaoEstoque.SAIDA
        );

        dto.setObservacao(
                "Consumo na Ordem de Serviço"
        );

        estoqueService.movimentar(dto);

    }

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

        private void validarNumero(
            String numero
    ){

        if(repository.existsByNumero(numero)){

            throw new BusinessException(
                    "Número da Ordem de Serviço já cadastrado."
            );

        }

    }

    // ==========================================================
// BUSCAR POR ID
// ==========================================================

@Override
public OrdemServicoResponseDTO buscarPorId(Long id) {

    OrdemServico ordem =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Ordem de Serviço",
                                    id
                            )
                    );

    return mapper.toResponseDTO(ordem);

}

// ==========================================================
// LISTAR
// ==========================================================

@Override
public Page<OrdemServicoResponseDTO> listar(
        Pageable pageable
) {

    return repository
            .findAll(pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// BUSCAR POR CLIENTE
// ==========================================================

@Override
public Page<OrdemServicoResponseDTO> buscarPorCliente(

        Long clienteId,

        Pageable pageable

) {

    return repository
            .findByClienteId(clienteId, pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// BUSCAR POR VEÍCULO
// ==========================================================

@Override
public Page<OrdemServicoResponseDTO> buscarPorVeiculo(

        Long veiculoId,

        Pageable pageable

) {

    return repository
            .findByVeiculoId(veiculoId, pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// BUSCAR POR STATUS
// ==========================================================

@Override
public Page<OrdemServicoResponseDTO> buscarPorStatus(

        StatusOrdemServico status,

        Pageable pageable

) {

    return repository
            .findByStatus(status, pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// BUSCAR COM FILTROS
// ==========================================================

@Override
public Page<OrdemServicoResponseDTO> buscarComFiltros(

        String numero,

        StatusOrdemServico status,

        Long clienteId,

        Long veiculoId,

        LocalDate dataInicial,

        LocalDate dataFinal,

        Pageable pageable

) {

    return repository

            .buscarComFiltros(

                    numero,

                    status,

                    clienteId,

                    veiculoId,

                    dataInicial,

                    dataFinal,

                    pageable

            )

            .map(mapper::toResponseDTO);

}

// ==========================================================
// ATUALIZAR
// ==========================================================

@Override
@Transactional
public OrdemServicoResponseDTO atualizar(
        Long id,
        AtualizarOrdemServicoRequestDTO dto
) {

    OrdemServico ordem = repository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Ordem de Serviço",
                            id
                    )
            );

    if (dto.getClienteId() != null) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente",
                                dto.getClienteId()
                        )
                );

        ordem.setCliente(cliente);
    }

    if (dto.getVeiculoId() != null) {

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Veículo",
                                dto.getVeiculoId()
                        )
                );

        ordem.setVeiculo(veiculo);

    }

    if (dto.getMecanicoResponsavel() != null) {
        ordem.setMecanicoResponsavel(dto.getMecanicoResponsavel());
    }

    if (dto.getPrevisaoEntrega() != null) {
        ordem.setPrevisaoEntrega(dto.getPrevisaoEntrega());
    }

    if (dto.getDataConclusao() != null) {
        ordem.setDataConclusao(dto.getDataConclusao());
    }

    if (dto.getStatus() != null) {
        ordem.setStatus(dto.getStatus());
    }

    if (dto.getQuilometragem() != null) {
        ordem.setQuilometragem(dto.getQuilometragem());
    }

    if (dto.getObservacoes() != null) {
        ordem.setObservacoes(dto.getObservacoes());
    }

    if (dto.getValorDesconto() != null) {
        ordem.setValorDesconto(dto.getValorDesconto());
    }

    ordem.calcularTotal();

    OrdemServico atualizada =
            repository.save(ordem);

    return mapper.toResponseDTO(atualizada);

}

// ==========================================================
// FINALIZAR
// ==========================================================

@Override
@Transactional
public OrdemServicoResponseDTO finalizar(
        Long id
) {

    OrdemServico ordem =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Ordem de Serviço",
                                    id
                            )
                    );

    ordem.setStatus(
            StatusOrdemServico.FINALIZADA
    );

    ordem.setDataConclusao(
            LocalDate.now()
    );

    OrdemServico salva =
            repository.save(ordem);

    log.info(
            "Ordem de Serviço {} finalizada.",
            salva.getNumero()
    );

    return mapper.toResponseDTO(salva);

}

// ==========================================================
// CANCELAR
// ==========================================================

@Override
@Transactional
public void cancelar(
        Long id
) {

    OrdemServico ordem =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Ordem de Serviço",
                                    id
                            )
                    );

    ordem.setStatus(
            StatusOrdemServico.CANCELADA
    );

    ordem.setAtivo(false);

    repository.save(ordem);

    log.info(
            "Ordem de Serviço {} cancelada.",
            ordem.getNumero()
    );

}

}
