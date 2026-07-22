package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FluxoCaixaResponseDTO {

    private Long id;

    private TipoMovimentacaoCaixa tipoMovimentacao;

    private OrigemMovimentacaoCaixa origem;

    private String descricao;

    private BigDecimal valor;

    private BigDecimal saldoAnterior;

    private BigDecimal saldoAtual;

    private FormaPagamento formaPagamento;

    private LocalDate dataMovimentacao;

    private Long clienteId;

    private String clienteNome;

    private Long fornecedorId;

    private String fornecedorRazaoSocial;

    private Long ordemServicoId;

    private Long contaReceberId;

    private Long contaPagarId;

    private String observacao;

    private Boolean ativo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

