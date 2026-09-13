package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resumo do fluxo de caixa de um único dia, usado no gráfico do dashboard.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class FluxoDiarioDTO {

    private LocalDate data;

    private BigDecimal entradas;

    private BigDecimal saidas;

    private BigDecimal saldoDoDia;
}
