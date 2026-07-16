package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.TipoItemOrdemServico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Item da Ordem de Serviço")
public class ItemOrdemServicoRequestDTO {

    @NotNull(message = "O tipo do item é obrigatório.")
    private TipoItemOrdemServico tipoItem;

    @Schema(description = "Obrigatório quando o tipo for PEÇA")
    private Long produtoId;

    @Size(max = 250)
    @Schema(description = "Descrição do serviço executado")
    private String descricaoServico;

    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1)
    private Integer quantidade;

    @NotNull(message = "O valor unitário é obrigatório.")
    @DecimalMin(value = "0.00")
    private BigDecimal valorUnitario;

}