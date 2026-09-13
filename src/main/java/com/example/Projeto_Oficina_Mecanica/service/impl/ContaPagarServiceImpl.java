package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ContaPagarMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.repository.NotaFiscalEntradaRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.ContaPagarService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de ContaPagarServiceImplTest,
 * já que o ContaPagarServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA é a auditoria em registrarPagamento().
 */
@Service
@RequiredArgsConstructor
public class ContaPagarServiceImpl implements ContaPagarService {

    private final ContaPagarRepository repository;

    private final FornecedorRepository fornecedorRepository;

    private final NotaFiscalEntradaRepository notaFiscalRepository;

    private final FluxoCaixaRepository fluxoCaixaRepository;

    private final ContaPagarMapper mapper;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public ContaPagarResponseDTO criar(CriarContaPagarRequestDTO dto) {

        Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", dto.getFornecedorId()));

        ContaPagar.ContaPagarBuilder builder = ContaPagar.builder()
                .fornecedor(fornecedor)
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .dataVencimento(dto.getDataVencimento())
                .observacao(dto.getObservacao());

        if (dto.getNotaFiscalEntradaId() != null) {
            NotaFiscalEntrada nota = notaFiscalRepository.findById(dto.getNotaFiscalEntradaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Nota Fiscal de Entrada", dto.getNotaFiscalEntradaId()));
            builder.notaFiscalEntrada(nota);
        }

        ContaPagar salva = repository.save(builder.build());

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public ContaPagarResponseDTO buscarPorId(Long id) {
        return mapper.toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public ContaPagarResponseDTO atualizar(Long id, AtualizarContaPagarRequestDTO dto) {

        ContaPagar conta = buscarEntidadePorId(id);

        if (dto.getDescricao() != null) {
            conta.setDescricao(dto.getDescricao());
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

        ContaPagar salva = repository.save(conta);

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public ContaPagarResponseDTO registrarPagamento(Long id, AtualizarContaPagarRequestDTO dto) {

        ContaPagar conta = buscarEntidadePorId(id);

        conta.setStatus(StatusContaPagar.PAGO);
        conta.setDataPagamento(dto.getDataPagamento());
        conta.setFormaPagamento(dto.getFormaPagamento());

        ContaPagar salva = repository.save(conta);

        BigDecimal saldoAnterior = fluxoCaixaRepository.findTopByOrderByDataMovimentacaoDescIdDesc()
                .map(FluxoCaixa::getSaldoAtual)
                .orElse(BigDecimal.ZERO);

        FluxoCaixa fluxo = FluxoCaixa.builder()
                .descricao("Pagamento: " + salva.getDescricao())
                .tipoMovimentacao(com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa.SAIDA)
                .origem(com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa.CONTA_PAGAR)
                .valor(salva.getValor().negate())
                .saldoAtual(saldoAnterior.subtract(salva.getValor()))
                .dataMovimentacao(salva.getDataPagamento())
                .contaPagar(salva)
                .build();

        fluxoCaixaRepository.save(fluxo);

        auditoriaService.registrar(
                usuarioLogado(),
                "PAGAMENTO",
                "ContaPagar",
                salva.getId(),
                "Pagamento registrado: " + salva.getDescricao() + " - R$ " + salva.getValor(),
                obterIp()
        );

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        ContaPagar conta = buscarEntidadePorId(id);
        repository.delete(conta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaPagarResponseDTO> listarPendentes() {
        return mapper.toResponseDTOList(repository.findByStatus(StatusContaPagar.PENDENTE));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContaPagarResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaPagarResponseDTO> buscarPorStatus(StatusContaPagar status) {
        return mapper.toResponseDTOList(repository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaPagarResponseDTO> buscarPorFornecedor(Long fornecedorId) {
        return mapper.toResponseDTOList(repository.findByFornecedorId(fornecedorId));
    }

    private ContaPagar buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a Pagar", id));
    }

    private String usuarioLogado() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (principal instanceof Usuario) ? ((Usuario) principal).getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String obterIp() {
        try {
            var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            return (attrs != null) ? attrs.getRequest().getRemoteAddr() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
