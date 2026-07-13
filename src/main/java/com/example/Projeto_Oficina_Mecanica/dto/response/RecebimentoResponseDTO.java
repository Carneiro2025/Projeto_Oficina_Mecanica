package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusRecebimento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecebimentoResponseDTO {

    private Long id;

    private Long ordemServico;

    private String cliente;

    private BigDecimal valor;

    private FormaPagamento formaPagamento;

    private StatusRecebimento status;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

}
