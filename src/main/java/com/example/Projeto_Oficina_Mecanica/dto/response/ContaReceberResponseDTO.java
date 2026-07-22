package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContaReceberResponseDTO {

    private Long id;

    private Long clienteId;

    private String clienteNome;

    private Long ordemServicoId;

    private String numeroOrdemServico;

    private BigDecimal valor;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private FormaPagamento formaPagamento;

    private StatusContaReceber status;

    private String observacao;

    private LocalDateTime createdAt;

}
