package com.example.Projeto_Oficina_Mecanica.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private Long totalClientes;

    private Long totalVeiculos;

    private Long totalProdutos;

    private Long totalMecanicos;

    private Long totalOrdensServico;

    private Long ordensAbertas;

    private Long ordensFinalizadas;

    private BigDecimal faturamentoMes;

    private BigDecimal contasReceberPendentes;

    private BigDecimal contasPagarPendentes;
}
