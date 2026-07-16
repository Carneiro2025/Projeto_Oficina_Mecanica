package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;
import com.example.Projeto_Oficina_Mecanica.enums.UnidadeMedida;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) responsável por receber os dados de criação de novos Produtos.
 * Utilizado para catalogar autopeças e insumos, estruturando a base para movimentações de estoque.
 */
@Data
@Schema(description = "Dados para cadastro de produto no catálogo")
public class CriarProdutoRequestDTO {

    // Identificador alfanumérico interno ou SKU da peça para rastreabilidade rápida
    @NotBlank(message = "O código é obrigatório.")
    @Size(max = 30, message = "O código não pode exceder {max} caracteres.")
    @Schema(example = "FLT-001", description = "Código interno único do produto")
    private String codigo;

    // Nome comercial claro ou especificação técnica do componente automotivo
    @NotBlank(message = "A descrição é obrigatória.")
    @Size(min = 3, max = 200, message = "A descrição do produto deve conter entre {min} e {max} caracteres.")
    @Schema(example = "Filtro de Óleo Motor Universal")
    private String descricao;

    // Agrupamento lógico da peça para segmentação de buscas (Ex: FILTRO, FREIO, SUSPENSAO)
    @NotNull(message = "A categoria é obrigatória.")
    @Schema(example = "FILTRO")
    private CategoriaProduto categoria;

    // Fabricante ou marca responsável pela fabricação do componente
    @Size(max = 50, message = "A marca não pode exceder {max} caracteres.")
    @Schema(example = "Mann Filter")
    private String marca;

    // Unidade de medida comercializada (Ex: UN, L, KG, CX, PAR, M)
    @Size(max = 50, message = "A unidade de medida não pode exceder {max} caracteres.")
    @Schema(example = "UN", description = "Unidade: UN, L, KG, CX, PAR, M...")
    private UnidadeMedida unidade;

    // Preço de custo pago ao fornecedor na nota fiscal de entrada
    @NotNull(message = "O preço de custo é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço de custo deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "Formato do preço de custo inválido (máximo de 8 dígitos inteiros e 2 decimais).")
    @Schema(example = "18.50")
    private BigDecimal precoCusto;

    // Preço base sugerido de venda a ser inserido nas Ordens de Serviço
    @NotNull(message = "O preço de venda é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço de venda deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "Formato do preço de venda inválido (máximo de 8 dígitos inteiros e 2 decimais).")
    @Schema(example = "35.90")
    private BigDecimal precoVenda;

    // Saldo físico inicial inserido no momento da abertura do cadastro
    @Min(value = 0, message = "O estoque atual não pode ser negativo.")
    @Schema(example = "50", description = "Quantidade inicial em estoque")
    private Integer estoqueAtual = 0;

    // Margem mínima de segurança para disparar alertas automáticos de novas cotações e compras
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo.")
    @Schema(example = "10", description = "Quantidade mínima que dispara alerta")
    private Integer estoqueMinimo = 5;

    // Teto recomendado para evitar excesso de capital de giro imobilizado em mercadoria
    @Min(value = 0, message = "O estoque máximo não pode ser negativo.")
    @Schema(example = "200")
    private Integer estoqueMaximo;

    // Localização física exata da peça dentro do almoxarifado (Ex: Corredor B, Prateleira 3)
    @Size(max = 100, message = "A localização física não pode exceder {max} caracteres.")
    @Schema(example = "Prateleira A3-01")
    private String localizacao;

    // Notas de compatibilidade, manuais de aplicação técnica ou equivalências de montadoras
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;

    // Associação com o fornecedor homologado que realiza o fornecimento primário da peça
    @Schema(description = "ID do fornecedor principal deste produto")
    private Long fornecedorId;
}