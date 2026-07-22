package com.example.Projeto_Oficina_Mecanica.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Dados para criação de uma Conta a Receber")
public class CriarContaReceberRequestDTO {

    @NotNull(message = "O cliente é obrigatório.")
    private Long clienteId;

    @Schema(description = "Ordem de Serviço vinculada")
    private Long ordemServicoId;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal valor;

    @NotNull(message = "A data de vencimento é obrigatória.")
    private LocalDate dataVencimento;

    @Size(max = 500)
    private String observacao;

}
