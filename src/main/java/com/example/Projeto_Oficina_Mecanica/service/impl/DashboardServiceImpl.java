package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.DashboardDTO;
import com.example.Projeto_Oficina_Mecanica.dto.FluxoDiarioDTO;
import com.example.Projeto_Oficina_Mecanica.dto.ProdutoEstoqueBaixoDTO;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ATENÇÃO: expandido nesta sessão (Fase 3 da sprint) a partir do
 * DashboardServiceImpl real do projeto, que já tinha os totais básicos.
 * As novas dependências (ContaPagarRepository, ContaReceberRepository,
 * FluxoCaixaRepository) foram adicionadas ao construtor via
 * @RequiredArgsConstructor.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ProdutoRepository produtoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final FluxoCaixaRepository fluxoCaixaRepository;

    private static final int DIAS_FLUXO_DIARIO = 7;

    @Override
    public DashboardDTO obterDashboard() {

        DashboardDTO dto = new DashboardDTO();

        // ── Totais gerais (já existia) ───────────────────────
        dto.setTotalClientes(clienteRepository.count());
        dto.setTotalVeiculos(veiculoRepository.count());
        dto.setTotalProdutos(produtoRepository.count());

        dto.setOrdensAbertas(ordemServicoRepository.countByStatus(StatusOrdemServico.ABERTA));
        dto.setOrdensFinalizadas(ordemServicoRepository.countByStatus(StatusOrdemServico.FINALIZADA));
        dto.setOrdensCanceladas(ordemServicoRepository.countByStatus(StatusOrdemServico.CANCELADA));

        // ── Financeiro do mês ─────────────────────────────────
        preencherFinanceiroDoMes(dto);

        // ── Estoque baixo ─────────────────────────────────────
        preencherEstoqueBaixo(dto);

        // ── Contas vencidas ───────────────────────────────────
        preencherContasVencidas(dto);

        // ── Fluxo diário (últimos 7 dias) ─────────────────────
        dto.setFluxoDiario(montarFluxoDiario());

        return dto;
    }

    private void preencherFinanceiroDoMes(DashboardDTO dto) {

        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        List<FluxoCaixa> movimentacoesDoMes =
                fluxoCaixaRepository.findByDataMovimentacaoBetween(inicioMes, fimMes);

        BigDecimal receita = movimentacoesDoMes.stream()
                .filter(f -> f.getTipoMovimentacao() == TipoMovimentacaoCaixa.ENTRADA)
                .map(FluxoCaixa::getValor)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesa = movimentacoesDoMes.stream()
                .filter(f -> f.getTipoMovimentacao() == TipoMovimentacaoCaixa.SAIDA)
                .map(FluxoCaixa::getValor)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setReceitaMes(receita);
        dto.setDespesaMes(despesa);
        dto.setLucroMes(receita.subtract(despesa));
        dto.setFaturamentoMes(receita);
    }

    private void preencherEstoqueBaixo(DashboardDTO dto) {

        List<Produto> produtosAbaixoDoMinimo = produtoRepository.findProdutosAbaixoDoMinimo();

        dto.setTotalProdutosEstoqueBaixo((long) produtosAbaixoDoMinimo.size());

        dto.setProdutosEstoqueBaixo(
                produtosAbaixoDoMinimo.stream()
                        .map(p -> ProdutoEstoqueBaixoDTO.builder()
                                .id(p.getId())
                                .codigo(p.getCodigo())
                                .descricao(p.getDescricao())
                                .estoqueAtual(p.getEstoqueAtual())
                                .estoqueMinimo(p.getEstoqueMinimo())
                                .build())
                        .toList()
        );
    }

    private void preencherContasVencidas(DashboardDTO dto) {

        LocalDate hoje = LocalDate.now();

        var contasPagarVencidas = contaPagarRepository
                .findByDataVencimentoBeforeAndStatusNot(hoje, StatusContaPagar.PAGO);

        var contasReceberVencidas = contaReceberRepository
                .findByDataVencimentoBeforeAndStatusNot(hoje, StatusContaReceber.PAGO);

        dto.setQtdContasPagarVencidas((long) contasPagarVencidas.size());
        dto.setValorContasPagarVencidas(
                contasPagarVencidas.stream()
                        .map(c -> c.getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        dto.setQtdContasReceberVencidas((long) contasReceberVencidas.size());
        dto.setValorContasReceberVencidas(
                contasReceberVencidas.stream()
                        .map(c -> c.getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    private List<FluxoDiarioDTO> montarFluxoDiario() {

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusDays(DIAS_FLUXO_DIARIO - 1L);

        List<FluxoCaixa> movimentacoes =
                fluxoCaixaRepository.findByDataMovimentacaoBetween(inicio, hoje);

        return inicio.datesUntil(hoje.plusDays(1))
                .map(dia -> {

                    List<FluxoCaixa> doDia = movimentacoes.stream()
                            .filter(f -> dia.equals(f.getDataMovimentacao()))
                            .toList();

                    BigDecimal entradas = doDia.stream()
                            .filter(f -> f.getTipoMovimentacao() == TipoMovimentacaoCaixa.ENTRADA)
                            .map(FluxoCaixa::getValor)
                            .map(BigDecimal::abs)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal saidas = doDia.stream()
                            .filter(f -> f.getTipoMovimentacao() == TipoMovimentacaoCaixa.SAIDA)
                            .map(FluxoCaixa::getValor)
                            .map(BigDecimal::abs)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return FluxoDiarioDTO.builder()
                            .data(dia)
                            .entradas(entradas)
                            .saidas(saidas)
                            .saldoDoDia(entradas.subtract(saidas))
                            .build();
                })
                .toList();
    }
}
