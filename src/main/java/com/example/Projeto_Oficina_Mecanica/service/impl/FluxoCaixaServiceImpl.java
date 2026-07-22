package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFluxoCaixaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFluxoCaixaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FluxoCaixaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.FluxoCaixaMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.service.FluxoCaixaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FluxoCaixaServiceImpl implements FluxoCaixaService {

    private final FluxoCaixaRepository repository;

    private final ClienteRepository clienteRepository;

    private final FornecedorRepository fornecedorRepository;

    private final OrdemServicoRepository ordemServicoRepository;

    private final ContaReceberRepository contaReceberRepository;

    private final FluxoCaixaMapper mapper;

        @Override
    @Transactional
    public FluxoCaixaResponseDTO criar(
            CriarFluxoCaixaRequestDTO dto
    ) {

        Cliente cliente = null;
        Fornecedor fornecedor = null;
        OrdemServico ordemServico = null;
        ContaReceber contaReceber = null;

        if (dto.getClienteId() != null) {
            cliente = buscarCliente(dto.getClienteId());
        }

        if (dto.getFornecedorId() != null) {
            fornecedor = buscarFornecedor(dto.getFornecedorId());
        }

        if (dto.getOrdemServicoId() != null) {
            ordemServico = buscarOrdemServico(dto.getOrdemServicoId());
        }

        if (dto.getContaReceberId() != null) {
            contaReceber = buscarContaReceber(dto.getContaReceberId());
        }

        BigDecimal saldoAnterior = calcularSaldoAtual();

        BigDecimal saldoAtual;

        if (dto.getTipoMovimentacao() == TipoMovimentacaoCaixa.ENTRADA) {

            saldoAtual = saldoAnterior.add(dto.getValor());

        } else {

            saldoAtual = saldoAnterior.subtract(dto.getValor());

        }

        FluxoCaixa fluxo = FluxoCaixa.builder()

                .tipoMovimentacao(dto.getTipoMovimentacao())

                .origem(dto.getOrigem())

                .descricao(dto.getDescricao())

                .valor(dto.getValor())

                .saldoAnterior(saldoAnterior)

                .saldoAtual(saldoAtual)

                .formaPagamento(dto.getFormaPagamento())

                .dataMovimentacao(dto.getDataMovimentacao())

                .cliente(cliente)

                .fornecedor(fornecedor)

                .ordemServico(ordemServico)

                .contaReceber(contaReceber)

                .observacao(dto.getObservacao())

                .build();

        FluxoCaixa salvo =
                repository.save(fluxo);

        log.info(
                "Movimentação financeira {} criada.",
                salvo.getId()
        );

        return mapper.toResponseDTO(salvo);

    }

        private BigDecimal calcularSaldoAtual() {

        List<FluxoCaixa> movimentacoes =
                repository.findAll();

        if (movimentacoes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return movimentacoes
                .get(movimentacoes.size() - 1)
                .getSaldoAtual();

    }

        private Cliente buscarCliente(Long id) {

        return clienteRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente",
                                id
                        )
                );

    }

        private Fornecedor buscarFornecedor(Long id) {

        return fornecedorRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fornecedor",
                                id
                        )
                );

    }

        private OrdemServico buscarOrdemServico(Long id) {

        return ordemServicoRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ordem de Serviço",
                                id
                        )
                );

    }

        private ContaReceber buscarContaReceber(Long id) {

        return contaReceberRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conta a Receber",
                                id
                        )
                );

    }

    // ==========================================================
// BUSCAR POR ID
// ==========================================================

@Override
public FluxoCaixaResponseDTO buscarPorId(
        Long id
) {

    FluxoCaixa fluxo =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fluxo de Caixa",
                                    id
                            )
                    );

    return mapper.toResponseDTO(fluxo);

}

// ==========================================================
// LISTAR
// ==========================================================

@Override
public Page<FluxoCaixaResponseDTO> listar(
        Pageable pageable
) {

    return repository
            .findAll(pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// BUSCAR POR TIPO
// ==========================================================

@Override
public List<FluxoCaixaResponseDTO> buscarPorTipo(
        TipoMovimentacaoCaixa tipo
) {

    return mapper.toResponseDTOList(

            repository.findByTipoMovimentacao(tipo)

    );

}

// ==========================================================
// BUSCAR POR PERÍODO
// ==========================================================

@Override
public List<FluxoCaixaResponseDTO> buscarPorPeriodo(

        LocalDate inicio,

        LocalDate fim

) {

    return mapper.toResponseDTOList(

            repository.findByDataMovimentacaoBetween(

                    inicio,

                    fim

            )

    );

}

// ==========================================================
// BUSCAR POR CLIENTE
// ==========================================================

@Override
public List<FluxoCaixaResponseDTO> buscarPorCliente(
        Long clienteId
) {

    return mapper.toResponseDTOList(

            repository.findByClienteId(clienteId)

    );

}

// ==========================================================
// BUSCAR POR FORNECEDOR
// ==========================================================

@Override
public List<FluxoCaixaResponseDTO> buscarPorFornecedor(
        Long fornecedorId
) {

    return mapper.toResponseDTOList(

            repository.findByFornecedorId(fornecedorId)

    );

}


// ==========================================================
// ATUALIZAR
// ==========================================================

@Override
@Transactional
public FluxoCaixaResponseDTO atualizar(
        Long id,
        AtualizarFluxoCaixaRequestDTO dto
) {

    FluxoCaixa fluxo = repository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Fluxo de Caixa",
                            id
                    )
            );

    if (dto.getDescricao() != null) {
        fluxo.setDescricao(dto.getDescricao());
    }

    if (dto.getValor() != null) {
        fluxo.setValor(dto.getValor());
    }

    if (dto.getFormaPagamento() != null) {
        fluxo.setFormaPagamento(dto.getFormaPagamento());
    }

    if (dto.getDataMovimentacao() != null) {
        fluxo.setDataMovimentacao(dto.getDataMovimentacao());
    }

    if (dto.getObservacao() != null) {
        fluxo.setObservacao(dto.getObservacao());
    }

    FluxoCaixa atualizado =
            repository.save(fluxo);

    log.info(
            "Movimentação financeira {} atualizada.",
            atualizado.getId()
    );

    return mapper.toResponseDTO(atualizado);

}

// ==========================================================
// EXCLUIR
// ==========================================================

@Override
@Transactional
public void excluir(
        Long id
) {

    FluxoCaixa fluxo = repository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Fluxo de Caixa",
                            id
                    )
            );

    repository.delete(fluxo);

    log.info(
            "Movimentação financeira {} removida.",
            fluxo.getId()
    );

}
}


