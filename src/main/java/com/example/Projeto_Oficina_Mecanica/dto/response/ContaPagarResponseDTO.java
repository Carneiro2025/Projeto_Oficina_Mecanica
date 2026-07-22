package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContaPagarResponseDTO {

    private Long id;

    private Long fornecedorId;

    private String fornecedorRazaoSocial;

    private Long notaFiscalEntradaId;

    private String numeroNotaFiscal;

    private String descricao;

    private BigDecimal valor;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private FormaPagamento formaPagamento;

    private StatusContaPagar status;

    private String observacao;

    private Boolean ativo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
