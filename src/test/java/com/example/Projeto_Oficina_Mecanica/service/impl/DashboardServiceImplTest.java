package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.DashboardDTO;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes unitários de {@link DashboardServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardServiceImpl")
class DashboardServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ContaPagarRepository contaPagarRepository;

    @Mock
    private ContaReceberRepository contaReceberRepository;

    @Mock
    private FluxoCaixaRepository fluxoCaixaRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        when(clienteRepository.count()).thenReturn(10L);
        when(veiculoRepository.count()).thenReturn(15L);
        when(produtoRepository.count()).thenReturn(50L);
        when(ordemServicoRepository.countByStatus(StatusOrdemServico.ABERTA)).thenReturn(4L);
        when(ordemServicoRepository.countByStatus(StatusOrdemServico.FINALIZADA)).thenReturn(20L);
        when(ordemServicoRepository.countByStatus(StatusOrdemServico.CANCELADA)).thenReturn(2L);

        // por padrão, sem movimentações/pendências (cada teste sobrescreve o que precisa)
        when(fluxoCaixaRepository.findByDataMovimentacaoBetween(any(), any())).thenReturn(List.of());
        when(contaPagarRepository.findByDataVencimentoBeforeAndStatusNot(any(), any())).thenReturn(List.of());
        when(contaReceberRepository.findByDataVencimentoBeforeAndStatusNot(any(), any())).thenReturn(List.of());
        when(produtoRepository.findProdutosAbaixoDoMinimo()).thenReturn(List.of());
    }

    @Test
    @DisplayName("deve consolidar todas as contagens básicas dos repositórios no DTO do dashboard")
    void deveConsolidarContagens() {
        DashboardDTO resultado = dashboardService.obterDashboard();

        assertThat(resultado.getTotalClientes()).isEqualTo(10L);
        assertThat(resultado.getTotalVeiculos()).isEqualTo(15L);
        assertThat(resultado.getTotalProdutos()).isEqualTo(50L);
        assertThat(resultado.getOrdensAbertas()).isEqualTo(4L);
        assertThat(resultado.getOrdensFinalizadas()).isEqualTo(20L);
        assertThat(resultado.getOrdensCanceladas()).isEqualTo(2L);
    }

    @Test
    @DisplayName("deve calcular receita, despesa e lucro do mês a partir do fluxo de caixa")
    void deveCalcularFinanceiroDoMes() {
        FluxoCaixa entrada = FluxoCaixa.builder()
                .tipoMovimentacao(TipoMovimentacaoCaixa.ENTRADA)
                .valor(new BigDecimal("500.00"))
                .dataMovimentacao(LocalDate.now())
                .build();

        FluxoCaixa saida = FluxoCaixa.builder()
                .tipoMovimentacao(TipoMovimentacaoCaixa.SAIDA)
                .valor(new BigDecimal("-200.00"))
                .dataMovimentacao(LocalDate.now())
                .build();

        when(fluxoCaixaRepository.findByDataMovimentacaoBetween(any(), any()))
                .thenReturn(List.of(entrada, saida));

        DashboardDTO resultado = dashboardService.obterDashboard();

        assertThat(resultado.getReceitaMes()).isEqualByComparingTo("500.00");
        assertThat(resultado.getDespesaMes()).isEqualByComparingTo("200.00");
        assertThat(resultado.getLucroMes()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("deve listar os produtos com estoque abaixo do mínimo")
    void deveListarProdutosComEstoqueBaixo() {
        Produto produto = Produto.builder()
                .id(1L)
                .codigo("PRD001")
                .descricao("Filtro de óleo")
                .estoqueAtual(2)
                .estoqueMinimo(5)
                .build();

        when(produtoRepository.findProdutosAbaixoDoMinimo()).thenReturn(List.of(produto));

        DashboardDTO resultado = dashboardService.obterDashboard();

        assertThat(resultado.getTotalProdutosEstoqueBaixo()).isEqualTo(1L);
        assertThat(resultado.getProdutosEstoqueBaixo()).hasSize(1);
        assertThat(resultado.getProdutosEstoqueBaixo().get(0).getCodigo()).isEqualTo("PRD001");
    }

    @Test
    @DisplayName("deve somar o valor das contas a pagar e a receber vencidas")
    void deveSomarContasVencidas() {
        ContaPagar contaPagar = ContaPagar.builder()
                .id(1L)
                .valor(new BigDecimal("300.00"))
                .status(StatusContaPagar.PENDENTE)
                .build();

        ContaReceber contaReceber = ContaReceber.builder()
                .id(1L)
                .valor(new BigDecimal("450.00"))
                .status(StatusContaReceber.PENDENTE)
                .build();

        when(contaPagarRepository.findByDataVencimentoBeforeAndStatusNot(any(), any()))
                .thenReturn(List.of(contaPagar));
        when(contaReceberRepository.findByDataVencimentoBeforeAndStatusNot(any(), any()))
                .thenReturn(List.of(contaReceber));

        DashboardDTO resultado = dashboardService.obterDashboard();

        assertThat(resultado.getQtdContasPagarVencidas()).isEqualTo(1L);
        assertThat(resultado.getValorContasPagarVencidas()).isEqualByComparingTo("300.00");
        assertThat(resultado.getQtdContasReceberVencidas()).isEqualTo(1L);
        assertThat(resultado.getValorContasReceberVencidas()).isEqualByComparingTo("450.00");
    }

    @Test
    @DisplayName("deve montar o fluxo diário com 7 dias, mesmo sem movimentações")
    void deveMontarFluxoDiarioComSeteDias() {
        DashboardDTO resultado = dashboardService.obterDashboard();

        assertThat(resultado.getFluxoDiario()).hasSize(7);
        assertThat(resultado.getFluxoDiario().get(6).getData()).isEqualTo(LocalDate.now());
    }
}
