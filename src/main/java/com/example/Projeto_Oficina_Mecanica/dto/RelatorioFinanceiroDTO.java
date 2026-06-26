package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioFinanceiroDTO {

    private BigDecimal totalReceitas;

    private BigDecimal totalDespesas;

    private BigDecimal lucro;

    private Integer quantidadeRecebimentos;

    private Integer quantidadePagamentos;
}
