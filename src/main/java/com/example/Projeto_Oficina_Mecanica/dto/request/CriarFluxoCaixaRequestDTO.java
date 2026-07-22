package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Cadastro de movimentação financeira")
public class CriarFluxoCaixaRequestDTO {

    @NotNull
    private TipoMovimentacaoCaixa tipoMovimentacao;

    @NotNull
    private OrigemMovimentacaoCaixa origem;

    @NotBlank
    @Size(max = 200)
    private String descricao;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;

    @NotNull
    private FormaPagamento formaPagamento;

    @NotNull
    private LocalDate dataMovimentacao;

    private Long clienteId;

    private Long fornecedorId;

    private Long ordemServicoId;

    private Long contaReceberId;

    private Long contaPagarId;

    @Size(max = 500)
    private String observacao;

}

