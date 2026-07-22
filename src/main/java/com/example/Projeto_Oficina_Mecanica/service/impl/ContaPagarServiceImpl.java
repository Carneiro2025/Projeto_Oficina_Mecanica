package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ContaPagarMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.NotaFiscalEntradaRepository;
import com.example.Projeto_Oficina_Mecanica.service.ContaPagarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContaPagarServiceImpl implements ContaPagarService {

    private final ContaPagarRepository repository;

    private final FornecedorRepository fornecedorRepository;

    private final NotaFiscalEntradaRepository notaFiscalRepository;

    private final FluxoCaixaRepository fluxoCaixaRepository;

    private final ContaPagarMapper mapper;

        private Fornecedor buscarFornecedor(Long id) {

        return fornecedorRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fornecedor",
                                id
                        )
                );

    }

    private NotaFiscalEntrada buscarNotaFiscal(Long id) {

        return notaFiscalRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Nota Fiscal",
                                id
                        )
                );

    }

    private BigDecimal obterSaldoAtual() {

        return fluxoCaixaRepository

                .findTopByOrderByDataMovimentacaoDescIdDesc()

                .map(FluxoCaixa::getSaldoAtual)

                .orElse(BigDecimal.ZERO);

    }

        @Override
    @Transactional
    public ContaPagarResponseDTO criar(
            CriarContaPagarRequestDTO dto
    ) {

        Fornecedor fornecedor =
                buscarFornecedor(dto.getFornecedorId());

        NotaFiscalEntrada notaFiscal = null;

        if (dto.getNotaFiscalEntradaId() != null) {

            notaFiscal =
                    buscarNotaFiscal(
                            dto.getNotaFiscalEntradaId()
                    );

        }

        ContaPagar conta = ContaPagar.builder()

                .fornecedor(fornecedor)

                .notaFiscalEntrada(notaFiscal)

                .descricao(dto.getDescricao())

                .valor(dto.getValor())

                .dataVencimento(dto.getDataVencimento())

                .formaPagamento(dto.getFormaPagamento())

                .observacao(dto.getObservacao())

                .build();

        ContaPagar salva =
                repository.save(conta);

        log.info(
                "Conta a pagar {} criada.",
                salva.getId()
        );

        return mapper.toResponseDTO(salva);

    }

    // ==========================================================
// BUSCAR POR ID
// ==========================================================

@Override
public ContaPagarResponseDTO buscarPorId(Long id) {

    ContaPagar conta = repository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Conta a Pagar",
                            id
                    )
            );

    return mapper.toResponseDTO(conta);

}

// ==========================================================
// LISTAR
// ==========================================================

@Override
public Page<ContaPagarResponseDTO> listar(
        Pageable pageable
) {

    return repository.findAll(pageable)
            .map(mapper::toResponseDTO);

}

// ==========================================================
// BUSCAR POR STATUS
// ==========================================================

@Override
public List<ContaPagarResponseDTO> buscarPorStatus(
        StatusContaPagar status
) {

    return mapper.toResponseDTOList(

            repository.findByStatus(status)

    );

}

// ==========================================================
// BUSCAR POR FORNECEDOR
// ==========================================================

@Override
public List<ContaPagarResponseDTO> buscarPorFornecedor(
        Long fornecedorId
) {

    return mapper.toResponseDTOList(

            repository.findByFornecedorId(fornecedorId)

    );

}

// ==========================================================
// LISTAR PENDENTES
// ==========================================================

@Override
public List<ContaPagarResponseDTO> listarPendentes() {

    return mapper.toResponseDTOList(

            repository.findByStatus(
                    StatusContaPagar.PENDENTE
            )

    );

}

// ==========================================================
// ATUALIZAR
// ==========================================================

@Override
@Transactional
public ContaPagarResponseDTO atualizar(
        Long id,
        AtualizarContaPagarRequestDTO dto
) {

    ContaPagar conta = repository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Conta a Pagar",
                            id
                    )
            );

    if (dto.getDescricao() != null)
        conta.setDescricao(dto.getDescricao());

    if (dto.getValor() != null)
        conta.setValor(dto.getValor());

    if (dto.getDataVencimento() != null)
        conta.setDataVencimento(dto.getDataVencimento());

    if (dto.getFormaPagamento() != null)
        conta.setFormaPagamento(dto.getFormaPagamento());

    if (dto.getStatus() != null)
        conta.setStatus(dto.getStatus());

    if (dto.getObservacao() != null)
        conta.setObservacao(dto.getObservacao());

    ContaPagar atualizado = repository.save(conta);

    log.info(
            "Conta a pagar {} atualizada.",
            atualizado.getId()
    );

    return mapper.toResponseDTO(atualizado);

}

// ==========================================================
// REGISTRAR PAGAMENTO
// ==========================================================

@Override
@Transactional
public ContaPagarResponseDTO registrarPagamento(
        Long id,
        AtualizarContaPagarRequestDTO dto
) {

    ContaPagar conta = repository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Conta a Pagar",
                            id
                    )
            );

    conta.setStatus(StatusContaPagar.PAGO);

    conta.setDataPagamento(dto.getDataPagamento());

    if (dto.getFormaPagamento() != null) {
        conta.setFormaPagamento(dto.getFormaPagamento());
    }

    ContaPagar salva = repository.save(conta);

    // =====================================================
    // GERA MOVIMENTAÇÃO NO FLUXO DE CAIXA
    // =====================================================

    BigDecimal saldoAnterior = obterSaldoAtual();

    BigDecimal saldoAtual =
            saldoAnterior.subtract(salva.getValor());

    FluxoCaixa fluxo = FluxoCaixa.builder()

            .tipoMovimentacao(TipoMovimentacaoCaixa.SAIDA)

            .origem(OrigemMovimentacaoCaixa.CONTA_PAGAR)

            .descricao("Pagamento Conta Nº " + salva.getId())

            .valor(salva.getValor())

            .saldoAnterior(saldoAnterior)

            .saldoAtual(saldoAtual)

            .formaPagamento(salva.getFormaPagamento())

            .dataMovimentacao(salva.getDataPagamento())

            .fornecedor(salva.getFornecedor())

            .contaPagar(salva)

            .observacao(salva.getObservacao())

            .build();

    fluxoCaixaRepository.save(fluxo);

    log.info(
            "Conta {} paga e lançada no Fluxo de Caixa.",
            salva.getId()
    );

    return mapper.toResponseDTO(salva);

}

// ==========================================================
// EXCLUIR
// ==========================================================

@Override
@Transactional
public void excluir(Long id) {

    ContaPagar conta = repository.findById(id)

            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Conta a Pagar",
                            id
                    )
            );

    repository.delete(conta);

    log.info(
            "Conta {} removida.",
            conta.getId()
    );

}

}
