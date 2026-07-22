package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AtualizarContaPagarRequestDTO {

    private Long fornecedorId;

    private Long notaFiscalEntradaId;

    @Size(max = 200)
    private String descricao;

    @DecimalMin("0.01")
    private BigDecimal valor;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private FormaPagamento formaPagamento;

    private StatusContaPagar status;

    @Size(max = 500)
    private String observacao;

}
