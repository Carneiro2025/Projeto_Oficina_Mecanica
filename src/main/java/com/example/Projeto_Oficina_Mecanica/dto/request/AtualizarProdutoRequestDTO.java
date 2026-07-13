package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) utilizado para a atualização dos dados cadastrais de Produtos/Peças.
 * Como este fluxo representa uma atualização parcial (PUT/PATCH), todos os atributos são opcionais.
 */
@Data
@Schema(description = "Dados para atualização de produto (todos os campos opcionais)")
public class AtualizarProdutoRequestDTO {

    // Descrição detalhada ou nome comercial da peça/componente
    @Size(min = 3, max = 200, message = "A descrição do produto deve conter entre {min} e {max} caracteres.")
    private String descricao;

    // Segmentação do produto (Ex: Amortecedores, Freios, Lubrificantes, Filtros)
    private CategoriaProduto categoria;

    // Fabricante ou marca da peça automotiva
    @Size(max = 50, message = "A marca do produto não pode exceder {max} caracteres.")
    private String marca;

    // Unidade de medida para controle de estoque (Ex: UN, PC, LT, KG)
    @Size(max = 50, message = "A unidade de medida não pode exceder {max} caracteres.")
    private String unidade;

    // Valor de aquisição pago ao fornecedor na entrada da mercadoria
    @DecimalMin(value = "0.01", message = "O preço de custo deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "Formato do preço de custo inválido (máximo de 8 dígitos inteiros e 2 decimais).")
    private BigDecimal precoCusto;

    // Valor de comercialização praticado nas Ordens de Serviço
    @DecimalMin(value = "0.01", message = "O preço de venda deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "Formato do preço de venda inválido (máximo de 8 dígitos inteiros e 2 decimais).")
    private BigDecimal precoVenda;

    // Gatilho de segurança para emitir alertas de reposição de estoque
    @Min(value = 0, message = "O estoque mínimo não pode ser menor que zero.")
    private Integer estoqueMinimo;

    // Limite sugerido para evitar imobilização excessiva de capital em peças
    @Min(value = 0, message = "O estoque máximo não pode ser menor que zero.")
    private Integer estoqueMaximo;

    // Endereço físico interno da peça no almoxarifado (Ex: Prateleira A, Corredor 2)
    @Size(max = 100, message = "A localização física não pode exceder {max} caracteres.")
    private String localizacao;

    // Histórico de compatibilidade de modelos de veículos ou notas técnicas de aplicação
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;

    // Vínculo com o fornecedor principal responsável pelo fornecimento regular desta peça
    @Schema(description = "Altera o fornecedor principal do produto")
    private Long fornecedorId;
}