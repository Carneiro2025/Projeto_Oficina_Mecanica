package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private Long totalClientes;

    private Long totalVeiculos;

    private Long totalProdutos;

    private Long totalOrdensServico;

    private Long ordensAbertas;

    private Long ordensFinalizadas;

    private BigDecimal faturamentoMes;

    private Long ordensCanceladas;

    // ── Financeiro do mês ────────────────────────────────
    private BigDecimal receitaMes;

    private BigDecimal despesaMes;

    private BigDecimal lucroMes;

    // ── Estoque ───────────────────────────────────────────
    private Long totalProdutosEstoqueBaixo;

    private List<ProdutoEstoqueBaixoDTO> produtosEstoqueBaixo;

    // ── Contas vencidas ──────────────────────────────────
    private Long qtdContasPagarVencidas;

    private BigDecimal valorContasPagarVencidas;

    private Long qtdContasReceberVencidas;

    private BigDecimal valorContasReceberVencidas;

    // ── Fluxo diário (últimos 7 dias) ────────────────────
    private List<FluxoDiarioDTO> fluxoDiario;

}
