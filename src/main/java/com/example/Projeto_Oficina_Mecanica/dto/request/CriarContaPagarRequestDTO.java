package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Dados para cadastro de Conta a Pagar")
public class CriarContaPagarRequestDTO {

    @NotNull(message = "Fornecedor é obrigatório")
    private Long fornecedorId;

    private Long notaFiscalEntradaId;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 200)
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal valor;

    @NotNull
    private LocalDate dataVencimento;

    private FormaPagamento formaPagamento;

    @Size(max = 500)
    private String observacao;

}
