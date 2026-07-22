package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AtualizarFluxoCaixaRequestDTO {

    @Size(max = 200)
    private String descricao;

    @DecimalMin("0.01")
    private BigDecimal valor;

    private FormaPagamento formaPagamento;

    private LocalDate dataMovimentacao;

    private String observacao;

}

