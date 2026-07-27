package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaReceberResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ContaReceberMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.service.ContaReceberService;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContaReceberServiceImpl
        implements ContaReceberService {

    private final ContaReceberRepository repository;

    private final ClienteRepository clienteRepository;

    private final OrdemServicoRepository ordemServicoRepository;

    private final ContaReceberMapper mapper;

    private final FluxoCaixaRepository fluxoCaixaRepository;

    private BigDecimal obterSaldoAtual() {

    return fluxoCaixaRepository

            .findTopByOrderByDataMovimentacaoDescIdDesc()

            .map(FluxoCaixa::getSaldoAtual)

            .orElse(BigDecimal.ZERO);

}

        @Override
    @Transactional
    public ContaReceberResponseDTO criar(
            CriarContaReceberRequestDTO dto
    ) {

        Cliente cliente =
                buscarCliente(dto.getClienteId());

        OrdemServico ordemServico = null;

        if (dto.getOrdemServicoId() != null) {

            ordemServico =
                    buscarOrdemServico(
                            dto.getOrdemServicoId()
                    );

        }

        ContaReceber conta =
                ContaReceber.builder()

                        .cliente(cliente)

                        .ordemServico(ordemServico)

                        .valor(dto.getValor())

                        .dataVencimento(dto.getDataVencimento())

                        .observacao(dto.getObservacao())

                        .status(StatusContaReceber.PENDENTE)

                        .build();

        ContaReceber salva =
                repository.save(conta);

        log.info(
                "Conta a receber {} criada.",
                salva.getId()
        );

        return mapper.toResponseDTO(salva);

    }

        private Cliente buscarCliente(
            Long id
    ) {

        return clienteRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente",
                                id
                        )
                );

    }

        private OrdemServico buscarOrdemServico(
            Long id
    ) {

        return ordemServicoRepository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ordem de Serviço",
                                id
                        )
                );

    }

        @Override
    public ContaReceberResponseDTO buscarPorId(
            Long id
    ) {

        ContaReceber conta =
                repository.findById(id)

                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Conta a Receber",
                                        id
                                )
                        );

        return mapper.toResponseDTO(conta);

    }

        @Override
    public List<ContaReceberResponseDTO> listar() {

        return mapper.toResponseDTOList(

                repository.findAll()

        );

    }

        @Override
    public List<ContaReceberResponseDTO> buscarPorCliente(
            Long clienteId
    ) {

        return mapper.toResponseDTOList(

                repository.findByClienteId(clienteId)

        );

    }

        @Override
    public List<ContaReceberResponseDTO> buscarPorStatus(
            StatusContaReceber status
    ) {

        return mapper.toResponseDTOList(

                repository.findByStatus(status)

        );

    }

    // ==========================================================
// ATUALIZAR
// ==========================================================

@Override
@Transactional
public ContaReceberResponseDTO atualizar(
        Long id,
        AtualizarContaReceberRequestDTO dto
) {

    ContaReceber conta = repository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Conta a Receber",
                            id
                    )
            );

    if (dto.getClienteId() != null) {
        conta.setCliente(
                buscarCliente(dto.getClienteId())
        );
    }

    if (dto.getOrdemServicoId() != null) {
        conta.setOrdemServico(
                buscarOrdemServico(dto.getOrdemServicoId())
        );
    }

    if (dto.getValor() != null) {
        conta.setValor(dto.getValor());
    }

    if (dto.getDataVencimento() != null) {
        conta.setDataVencimento(dto.getDataVencimento());
    }

    if (dto.getObservacao() != null) {
        conta.setObservacao(dto.getObservacao());
    }

    if (dto.getStatus() != null) {
        conta.setStatus(dto.getStatus());
    }

    ContaReceber atualizada =
            repository.save(conta);

    log.info(
            "Conta a Receber {} atualizada.",
            atualizada.getId()
    );

    return mapper.toResponseDTO(atualizada);

}

// ==========================================================
// EXCLUIR
// ==========================================================

@Override
@Transactional
public void excluir(
        Long id
) {

    ContaReceber conta =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Conta a Receber",
                                    id
                            )
                    );

    repository.delete(conta);

    log.info(
            "Conta {} removida.",
            conta.getId()
    );

}

// ==========================================================
// REGISTRAR PAGAMENTO
// ==========================================================

@Override
@Transactional
public ContaReceberResponseDTO registrarPagamento(
        Long id,
        AtualizarContaReceberRequestDTO dto
) {

    ContaReceber conta =
            repository.findById(id)

                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Conta a Receber",
                                    id
                            )
                    );

    conta.setDataPagamento(
            dto.getDataPagamento()
    );

    conta.setFormaPagamento(
            dto.getFormaPagamento()
    );

    conta.setStatus(
            StatusContaReceber.PAGO
    );

    ContaReceber salva =
            repository.save(conta);

    // =====================================================
    // GERA ENTRADA NO FLUXO DE CAIXA
    // =====================================================

    BigDecimal saldoAnterior =
            obterSaldoAtual();

    BigDecimal saldoAtual =
            saldoAnterior.add(
                    salva.getValor()
            );

    FluxoCaixa fluxo =
            FluxoCaixa.builder()

                    .tipoMovimentacao(
                            TipoMovimentacaoCaixa.ENTRADA
                    )

                    .origem(
                            OrigemMovimentacaoCaixa.CONTA_RECEBER
                    )

                    .descricao(
                            "Recebimento Conta Nº " + salva.getId()
                    )

                    .valor(
                            salva.getValor()
                    )

                    .saldoAnterior(
                            saldoAnterior
                    )

                    .saldoAtual(
                            saldoAtual
                    )

                    .formaPagamento(
                            salva.getFormaPagamento()
                    )

                    .dataMovimentacao(
                            salva.getDataPagamento()
                    )

                    .cliente(
                            salva.getCliente()
                    )

                    .contaReceber(
                            salva
                    )

                    .observacao(
                            salva.getObservacao()
                    )

                    .build();

    fluxoCaixaRepository.save(fluxo);

    log.info(
            "Conta {} recebida e lançada no Fluxo de Caixa.",
            salva.getId()
    );

    return mapper.toResponseDTO(salva);

}

}