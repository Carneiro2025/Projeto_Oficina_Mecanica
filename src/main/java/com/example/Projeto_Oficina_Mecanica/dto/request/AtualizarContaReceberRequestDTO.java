package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AtualizarContaReceberRequestDTO {

    private Long clienteId;

    private Long ordemServicoId;

    @DecimalMin(value = "0.01")
    private BigDecimal valor;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private FormaPagamento formaPagamento;

    private StatusContaReceber status;

    @Size(max = 500)
    private String observacao;

}
