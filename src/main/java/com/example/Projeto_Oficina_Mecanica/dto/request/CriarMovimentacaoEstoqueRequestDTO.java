package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;


@Data
@Schema(description = "Dados para registrar uma movimentação de estoque")
public class CriarMovimentacaoEstoqueRequestDTO {


    @NotNull(message = "O produto é obrigatório.")
    @Schema(
            example = "1",
            description = "ID do produto movimentado"
    )
    private Long produtoId;



    @NotNull(message = "O tipo de movimentação é obrigatório.")
    @Schema(
            example = "ENTRADA",
            description = "Tipo da movimentação"
    )
    private TipoMovimentacaoEstoque tipo;



    @NotNull(message = "A quantidade é obrigatória.")
    @Min(
            value = 1,
            message = "A quantidade deve ser maior que zero."
    )
    @Schema(
            example = "10",
            description = "Quantidade movimentada"
    )
    private Integer quantidade;



    @Size(
            max = 500,
            message = "A observação deve possuir no máximo 500 caracteres."
    )
    @Schema(
            example = "Entrada referente a inventário inicial."
    )
    private String observacao;

}
