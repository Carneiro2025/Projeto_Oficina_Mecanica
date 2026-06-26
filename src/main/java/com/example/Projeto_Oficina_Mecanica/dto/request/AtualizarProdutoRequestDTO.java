package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Dados para atualização de produto (todos os campos opcionais)")
public class AtualizarProdutoRequestDTO {

    @Size(min = 3, max = 200)
    private String descricao;

    private CategoriaProduto categoria;

    @Size(max = 50)
    private String marca;

    @Size(max = 50)
    private String unidade;

    @DecimalMin(value = "0.01", message = "Preço de custo deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precoCusto;

    @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precoVenda;

    @Min(0)
    private Integer estoqueMinimo;

    @Min(0)
    private Integer estoqueMaximo;

    @Size(max = 100)
    private String localizacao;

    @Size(max = 500)
    private String observacoes;

    @Schema(description = "Altera o fornecedor principal do produto")
    private Long fornecedorId;
}