package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

import java.math.BigDecimal;

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


}
