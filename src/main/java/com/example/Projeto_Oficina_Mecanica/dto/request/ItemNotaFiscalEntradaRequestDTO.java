package com.example.Projeto_Oficina_Mecanica.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import lombok.Data;

import java.math.BigDecimal;



@Data
@Schema(description = "Item de produto da Nota Fiscal de Entrada")
public class ItemNotaFiscalEntradaRequestDTO {



    @NotNull(message = "O produto é obrigatório.")
    @Schema(
            example = "10",
            description = "ID do produto"
    )
    private Long produtoId;



    @NotNull(message = "A quantidade é obrigatória.")
    @Min(
            value = 1,
            message = "A quantidade deve ser maior que zero."
    )
    @Schema(
            example = "50",
            description = "Quantidade recebida"
    )
    private Integer quantidade;



    @NotNull(message = "O valor unitário é obrigatório.")
    @DecimalMin(
            value = "0.01",
            message = "O valor deve ser maior que zero."
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "Formato de valor inválido."
    )
    @Schema(
            example = "25.90",
            description = "Valor unitário do produto"
    )
    private BigDecimal valorUnitario;


}
