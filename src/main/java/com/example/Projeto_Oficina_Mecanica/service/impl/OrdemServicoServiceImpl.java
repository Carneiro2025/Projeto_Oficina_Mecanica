package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.ItemOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.entity.ItemOrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.TipoItemOrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.OrdemServicoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.EstoqueService;
import com.example.Projeto_Oficina_Mecanica.service.OrdemServicoService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de OrdemServicoServiceImplTest
 * (que já incluía a integração OS → ContaReceber implementada em sessão anterior),
 * já que o OrdemServicoServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA nesta rodada é a auditoria em finalizar().
 */
@Service
@RequiredArgsConstructor
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository repository;

    private final ClienteRepository clienteRepository;

    private final VeiculoRepository veiculoRepository;

    private final ProdutoRepository produtoRepository;

    private final OrdemServicoMapper mapper;

    private final EstoqueService estoqueService;

    private final ContaReceberRepository contaReceberRepository;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public OrdemServicoResponseDTO criar(CriarOrdemServicoRequestDTO dto) {

        if (repository.existsByNumero(dto.getNumero())) {
            throw new BusinessException("Número de OS já cadastrado.");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", dto.getVeiculoId()));

        OrdemServico ordem = OrdemServico.builder()
                .numero(dto.getNumero())
                .cliente(cliente)
                .veiculo(veiculo)
                .status(StatusOrdemServico.ABERTA)
                .ativo(true)
                .itens(new ArrayList<>())
                .build();

        for (ItemOrdemServicoRequestDTO itemDto : dto.getItens()) {

            ItemOrdemServico item = ItemOrdemServico.builder()
                    .ordemServico(ordem)
                    .tipoItem(itemDto.getTipoItem())
                    .descricaoServico(itemDto.getDescricaoServico())
                    .quantidade(itemDto.getQuantidade())
                    .valorUnitario(itemDto.getValorUnitario())
                    .build();

            if (itemDto.getTipoItem() == TipoItemOrdemServico.PECA) {

                Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto", itemDto.getProdutoId()));

                item.setProduto(produto);

                CriarMovimentacaoEstoqueRequestDTO movimentacaoDto = new CriarMovimentacaoEstoqueRequestDTO();
                movimentacaoDto.setProdutoId(produto.getId());
                movimentacaoDto.setTipo(TipoMovimentacaoEstoque.SAIDA);
                movimentacaoDto.setQuantidade(itemDto.getQuantidade());
                movimentacaoDto.setObservacao("Baixa automática - OS " + dto.getNumero());

                estoqueService.movimentar(movimentacaoDto);
            }

            ordem.getItens().add(item);
        }

        ordem.setValorDesconto(dto.getValorDesconto() != null ? dto.getValorDesconto() : BigDecimal.ZERO);
        recalcularTotal(ordem);

        OrdemServico salva = repository.save(ordem);

        auditoriaService.registrar(
                usuarioLogado(),
                "CRIAR",
                "OrdemServico",
                salva.getId(),
                "OS criada: " + salva.getNumero(),
                obterIp()
        );

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(Long id) {
        return mapper.toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public OrdemServicoResponseDTO atualizar(Long id, AtualizarOrdemServicoRequestDTO dto) {

        OrdemServico ordem = buscarEntidadePorId(id);

        if (dto.getObservacoes() != null) {
            ordem.setObservacoes(dto.getObservacoes());
        }
        if (dto.getStatus() != null) {
            ordem.setStatus(dto.getStatus());
        }

        recalcularTotal(ordem);

        OrdemServico salva = repository.save(ordem);

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public OrdemServicoResponseDTO finalizar(Long id) {

        OrdemServico ordem = buscarEntidadePorId(id);

        if (ordem.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new BusinessException("Ordem de Serviço " + ordem.getNumero() + " já está finalizada.");
        }

        ordem.setStatus(StatusOrdemServico.FINALIZADA);
        ordem.setDataConclusao(LocalDate.now());

        OrdemServico salva = repository.save(ordem);

        gerarContaReceber(salva);

        auditoriaService.registrar(
                usuarioLogado(),
                "OS_FINALIZADA",
                "OrdemServico",
                salva.getId(),
                "OS finalizada: " + salva.getNumero(),
                obterIp()
        );

        return mapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {

        OrdemServico ordem = buscarEntidadePorId(id);

        ordem.setStatus(StatusOrdemServico.CANCELADA);
        ordem.setAtivo(false);

        repository.save(ordem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> buscarPorCliente(Long clienteId, Pageable pageable) {
        return repository.findByClienteId(clienteId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> buscarPorVeiculo(Long veiculoId, Pageable pageable) {
        return repository.findByVeiculoId(veiculoId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> buscarPorStatus(StatusOrdemServico status, Pageable pageable) {
        return repository.findByStatus(status, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> buscarComFiltros(String numero, StatusOrdemServico status,
            Long clienteId, Long veiculoId, LocalDate dataInicial, LocalDate dataFinal, Pageable pageable) {
        return repository.buscarComFiltros(numero, status, clienteId, veiculoId, dataInicial, dataFinal, pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    // ==========================================================
    // GERAR CONTA A RECEBER (automático ao finalizar a OS)
    // ==========================================================

    private void gerarContaReceber(OrdemServico ordem) {

        ContaReceber conta = ContaReceber.builder()
                .cliente(ordem.getCliente())
                .ordemServico(ordem)
                .valor(ordem.getValorTotal())
                .dataVencimento(LocalDate.now().plusDays(30))
                .status(StatusContaReceber.PENDENTE)
                .observacao("Gerada automaticamente ao finalizar a OS " + ordem.getNumero())
                .build();

        contaReceberRepository.save(conta);
    }

    private void recalcularTotal(OrdemServico ordem) {

        BigDecimal totalItens = ordem.getItens().stream()
                .map(item -> item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal desconto = ordem.getValorDesconto() != null ? ordem.getValorDesconto() : BigDecimal.ZERO;

        ordem.setValorTotal(totalItens.subtract(desconto));
    }

    private OrdemServico buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de Serviço", id));
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
