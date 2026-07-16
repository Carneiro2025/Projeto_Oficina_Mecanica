package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.Combustivel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO responsável pela atualização de veículos.
 *
 * Todos os campos são opcionais.
 */
@Data
@Schema(description = "Dados para atualização de veículo")
public class AtualizarVeiculoRequestDTO {

    @Schema(example = "1")
    private Long clienteId;

    @Size(min = 7, max = 10)
    @Schema(example = "RFA2B34")
    private String placa;

    @Size(max = 100)
    private String marca;

    @Size(max = 100)
    private String modelo;

    @Size(max = 100)
    private String versao;

    @Min(1950)
    @Max(2100)
    private Integer anoFabricacao;

    @Min(1950)
    @Max(2100)
    private Integer anoModelo;

    @Size(max = 50)
    private String cor;

    @Size(max = 30)
    private String chassi;

    @Size(max = 20)
    private String renavam;

    @PositiveOrZero
    private Integer quilometragem;

    private Combustivel combustivel;

    @Size(max = 500)
    private String observacoes;
}
