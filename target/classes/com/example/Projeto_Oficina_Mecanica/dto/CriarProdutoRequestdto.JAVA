package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.entity.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Dados para cadastro de produto no catálogo")
public class CriarProdutoRequestDTO {

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 30)
    @Schema(example = "FLT-001", description = "Código interno único do produto")
    private String codigo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 3, max = 200)
    @Schema(example = "Filtro de Óleo Motor Universal")
    private String descricao;

    @NotNull(message = "Categoria é obrigatória")
    @Schema(example = "FILTRO")
    private CategoriaProduto categoria;

    @Size(max = 50)
    @Schema(example = "Mann Filter")
    private String marca;

    @Size(max = 50)
    @Schema(example = "UN", description = "Unidade: UN, L, KG, CX, PAR, M...")
    private String unidade;

    @NotNull(message = "Preço de custo é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de custo deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    @Schema(example = "18.50")
    private BigDecimal precoCusto;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    @Schema(example = "35.90")
    private BigDecimal precoVenda;

    @Min(value = 0, message = "Estoque atual não pode ser negativo")
    @Schema(example = "50", description = "Quantidade inicial em estoque")
    private Integer estoqueAtual = 0;

    @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
    @Schema(example = "10", description = "Quantidade mínima que dispara alerta")
    private Integer estoqueMinimo = 5;

    @Min(0)
    @Schema(example = "200")
    private Integer estoqueMaximo;

    @Size(max = 100)
    @Schema(example = "Prateleira A3-01")
    private String localizacao;

    @Size(max = 500)
    private String observacoes;

    @Schema(description = "ID do fornecedor principal deste produto")
    private Long fornecedorId;
}