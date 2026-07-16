package com.example.Projeto_Oficina_Mecanica.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Dados para abertura de Ordem de Serviço")
public class CriarOrdemServicoRequestDTO {

    @NotBlank
    private String numero;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long veiculoId;

    @Size(max = 150)
    private String mecanicoResponsavel;

    private LocalDate previsaoEntrega;

    private Integer quilometragem;

    @Size(max = 1000)
    private String observacoes;

    @DecimalMin(value = "0.00")
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Valid
    @NotEmpty(message = "A Ordem de Serviço deve possuir pelo menos um item.")
    private List<ItemOrdemServicoRequestDTO> itens;

}