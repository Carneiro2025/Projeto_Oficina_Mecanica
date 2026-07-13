package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object (DTO) responsável por receber os dados de criação de novos Clientes.
 * Suporta o cadastro unificado de Pessoas Físicas (PF) e Pessoas Jurídicas (PJ) para a oficina.
 */
@Data
@Schema(description = "Dados para cadastro de cliente PF ou PJ")
public class CriarClienteRequestDTO {

    // Nome completo do cliente (ou nome fantasia caso seja pessoa jurídica)
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome deve conter entre {min} e {max} caracteres.")
    @Schema(example = "João da Silva")
    private String nome;

    // Razão Social corporativa (utilizada caso o TipoPessoa seja PJ para emissão de Notas Fiscais)
    @Size(max = 200, message = "A Razão Social não pode exceder {max} caracteres.")
    @Schema(example = "Silva Automóveis LTDA")
    private String razaoSocial;

    // Cadastro identificador único junto à Receita Federal (Validado por anotação customizada)
    @NotBlank(message = "O CPF/CNPJ é obrigatório.")
    @CpfCnpj
    @Schema(example = "123.456.789-09")
    private String cpfCnpj;

    // Define a classificação fiscal do cliente (PF ou PJ)
    @NotNull(message = "O tipo de pessoa é obrigatório.")
    @Schema(example = "PF")
    private TipoPessoa tipo;

    // Telefone fixo residencial ou comercial para contato
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone fixo informado é inválido.")
    @Schema(example = "(81) 3333-4444")
    private String telefone;

    // Telefone móvel para envio de avisos de conclusão de OS via mensagens
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Telefone celular informado é inválido.")
    @Schema(example = "(81) 99999-8888")
    private String celular;

    // E-mail principal para envio de orçamentos, alertas de revisão e Notas Fiscais
    @Email(message = "O e-mail informado é inválido.")
    @Schema(example = "joao@email.com")
    private String email;

    // Objeto de endereço aninhado (passa por validação interna em cascata)
    @Valid
    private EnderecoResponseDTO endereco;

    // Histórico de crédito, restrições internas ou preferências particulares de atendimento do cliente
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;
}