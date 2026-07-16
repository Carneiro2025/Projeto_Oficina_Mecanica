package com.example.Projeto_Oficina_Mecanica.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;



@Data
@Schema(description = "Dados para cadastro de Nota Fiscal de Entrada")
public class CriarNotaFiscalEntradaRequestDTO {



    @NotBlank(
            message = "O número da nota é obrigatório."
    )
    @Size(
            max = 30,
            message = "O número da nota deve possuir no máximo 30 caracteres."
    )
    @Schema(
            example = "NF-000123"
    )
    private String numero;



    @NotNull(
            message = "O fornecedor é obrigatório."
    )
    @Schema(
            example = "1",
            description = "ID do fornecedor"
    )
    private Long fornecedorId;



    @NotNull(
            message = "A data de emissão é obrigatória."
    )
    @Schema(
            example = "2026-07-16"
    )
    private LocalDate dataEmissao;



    @NotEmpty(
            message = "A nota deve possuir pelo menos um item."
    )
    @Valid
    private List<ItemNotaFiscalEntradaRequestDTO> itens;



    @Size(
            max = 500,
            message = "Observação deve possuir no máximo 500 caracteres."
    )
    private String observacoes;


}
