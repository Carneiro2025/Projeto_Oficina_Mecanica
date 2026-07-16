package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.Combustivel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO responsável pelo cadastro de novos veículos.
 *
 * Regras:
 * - Todo veículo deve pertencer a um cliente.
 * - A placa deve ser única.
 */
@Data
@Schema(description = "Dados para cadastro de um veículo")
public class CriarVeiculoRequestDTO {

    @NotNull(message = "O cliente é obrigatório.")
    @Schema(example = "1")
    private Long clienteId;

    @NotBlank(message = "A placa é obrigatória.")
    @Size(min = 7, max = 10)
    @Schema(example = "RFA2B34")
    private String placa;

    @NotBlank(message = "A marca é obrigatória.")
    @Size(max = 100)
    @Schema(example = "Toyota")
    private String marca;

    @NotBlank(message = "O modelo é obrigatório.")
    @Size(max = 100)
    @Schema(example = "Corolla")
    private String modelo;

    @Size(max = 100)
    @Schema(example = "XEi 2.0")
    private String versao;

    @NotNull(message = "Ano de fabricação obrigatório.")
    @Min(1950)
    @Max(2100)
    @Schema(example = "2022")
    private Integer anoFabricacao;

    @NotNull(message = "Ano do modelo obrigatório.")
    @Min(1950)
    @Max(2100)
    @Schema(example = "2023")
    private Integer anoModelo;

    @Size(max = 50)
    @Schema(example = "Prata")
    private String cor;

    @Size(max = 30)
    @Schema(example = "9BWZZZ377VT004251")
    private String chassi;

    @Size(max = 20)
    @Schema(example = "00123456789")
    private String renavam;

    @PositiveOrZero(message = "A quilometragem não pode ser negativa.")
    @Schema(example = "35000")
    private Integer quilometragem;

    @Schema(example = "FLEX")
    private Combustivel combustivel;

    @Size(max = 500)
    @Schema(example = "Veículo revisado regularmente.")
    private String observacoes;
}