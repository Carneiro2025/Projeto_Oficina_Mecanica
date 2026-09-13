package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaReceberRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaReceberResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ContaReceberMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.ContaReceberService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO nesta sessão. O ContaReceberServiceImpl.java
 * original não estava disponível (e, diferente dos outros services desta
 * sprint, não havia nem um ContaReceberServiceImplTest.java para pinar o
 * contrato) — esta reconstrução segue o mesmo padrão já confirmado em
 * ContaPagarServiceImpl (que É testado), já que os dois foram vistos como
 * espelhados um do outro em sessão anterior. Criei também um teste novo
 * (ContaReceberServiceImplTest.java) para travar esse comportamento daqui
 * pra frente. Confira com atenção redobrada contra o arquivo real.
 */
@Service
@RequiredArgsConstructor
public class ContaReceberServiceImpl implements ContaReceberService {

    private final ContaReceberRepository repository;

    private final ClienteRepository clienteRepository;

    private final OrdemServicoRepository ordemServicoRepository;

    private final FluxoCaixaRepository fluxoCaixaRepository;

    private final ContaReceberMapper mapper;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public ContaReceberResponseDTO criar(CriarContaReceberRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));

        ContaReceber.ContaReceberBuilder builder = ContaReceber.builder()
                .cliente(cliente)
                .valor(dto.getValor())
                .dataVencimento(dto.getDataVencimento())
                .status(StatusContaReceber.PENDENTE)
                .observacao(dto.getObservacao());

        if (dto.getOrdemServicoId() != null) {
            OrdemServico ordem = ordemServicoRepository.findById(dto.getOrdemServicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço", dto.getOrdemServicoId()));
            builder.ordemServico(ordem);
        }

        ContaReceber salva = repository.save(builder.build());

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public ContaReceberResponseDTO buscarPorId(Long id) {
        return mapper.toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public ContaReceberResponseDTO atualizar(Long id, AtualizarContaReceberRequestDTO dto) {

        ContaReceber conta = buscarEntidadePorId(id);

        if (dto.getValor() != null) {
            conta.setValor(dto.getValor());
        }
        if (dto.getDataVencimento() != null) {
            conta.setDataVencimento(dto.getDataVencimento());
        }
        if (dto.getObservacao() != null) {
            conta.setObservacao(dto.getObservacao());
        }

        ContaReceber salva = repository.save(conta);

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public ContaReceberResponseDTO registrarPagamento(Long id, AtualizarContaReceberRequestDTO dto) {

        ContaReceber conta = buscarEntidadePorId(id);

        conta.setStatus(StatusContaReceber.PAGO);
        conta.setDataPagamento(dto.getDataPagamento());
        conta.setFormaPagamento(dto.getFormaPagamento());

        ContaReceber salva = repository.save(conta);

        BigDecimal saldoAnterior = fluxoCaixaRepository.findTopByOrderByDataMovimentacaoDescIdDesc()
                .map(FluxoCaixa::getSaldoAtual)
                .orElse(BigDecimal.ZERO);

        FluxoCaixa fluxo = FluxoCaixa.builder()
                .descricao("Recebimento: cliente " + salva.getCliente().getNome())
                .tipoMovimentacao(com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa.ENTRADA)
                .origem(com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa.CONTA_RECEBER)
                .valor(salva.getValor())
                .saldoAtual(saldoAnterior.add(salva.getValor()))
                .dataMovimentacao(salva.getDataPagamento())
                .contaReceber(salva)
                .build();

        fluxoCaixaRepository.save(fluxo);

        auditoriaService.registrar(
                usuarioLogado(),
                "RECEBIMENTO",
                "ContaReceber",
                salva.getId(),
                "Recebimento registrado: cliente " + salva.getCliente().getNome() + " - R$ " + salva.getValor(),
                obterIp()
        );

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        ContaReceber conta = buscarEntidadePorId(id);
        repository.delete(conta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaReceberResponseDTO> listar() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaReceberResponseDTO> buscarPorCliente(Long clienteId) {
        return mapper.toResponseDTOList(repository.findByClienteId(clienteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaReceberResponseDTO> buscarPorStatus(StatusContaReceber status) {
        return mapper.toResponseDTOList(repository.findByStatus(status));
    }

    private ContaReceber buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a Receber", id));
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
