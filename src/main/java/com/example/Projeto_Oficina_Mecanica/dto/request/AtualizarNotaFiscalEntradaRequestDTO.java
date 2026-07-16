package com.example.Projeto_Oficina_Mecanica.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;



@Data
@Schema(description = "Dados para atualização da Nota Fiscal de Entrada")
public class AtualizarNotaFiscalEntradaRequestDTO {


    @Size(
            max = 30,
            message = "O número deve possuir no máximo 30 caracteres."
    )
    @Schema(
            example = "NF-000123"
    )
    private String numero;



    @Schema(
            example = "2026-07-16"
    )
    private LocalDate dataEmissao;



    @Size(
            max = 500,
            message = "Observação deve possuir no máximo 500 caracteres."
    )
    private String observacoes;


}
