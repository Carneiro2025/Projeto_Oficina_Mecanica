package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object (DTO) responsável por receber os dados de criação de novos Fornecedores.
 * Utilizado no fluxo de homologação e cadastro de parceiros comerciais de autopeças e insumos.
 */
@Data
@Schema(description = "Dados para cadastro de fornecedor")
public class CriarFornecedorRequestDTO {

    // Razão Social jurídica e oficial da empresa fornecedora
    @NotBlank(message = "A razão social é obrigatória.")
    @Size(min = 3, max = 200, message = "A razão social deve conter entre {min} e {max} caracteres.")
    @Schema(example = "Auto Peças Brasil LTDA")
    private String razaoSocial;

    // Nome fantasia ou marca comercial do parceiro
    @Size(max = 150, message = "O nome fantasia não pode exceder {max} caracteres.")
    @Schema(example = "Auto Peças Brasil")
    private String nomeFantasia;

    // Cadastro identificador fiscal do fornecedor (Validado por anotação customizada)
    @NotBlank(message = "O CNPJ é obrigatório.")
    @CpfCnpj
    @Schema(example = "11.222.333/0001-81")
    private String cnpj;

    // Inscrição Estadual para fins de validação fiscal nas Notas Fiscais de Entrada
    @Size(max = 20, message = "A inscrição estadual não pode exceder {max} caracteres.")
    @Schema(example = "IE 123456789")
    private String inscricaoEstadual;

    // Telefone fixo do estabelecimento comercial
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone informado é inválido.")
    @Schema(example = "(81) 3210-5000")
    private String telefone;

    // Celular para contato rápido ou WhatsApp do vendedor
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Telefone celular informado é inválido.")
    private String celular;

    // E-mail corporativo para envio de Notas Fiscais e ordens de compra
    @Email(message = "O e-mail informado é inválido.")
    @Schema(example = "contato@autopecas.com.br")
    private String email;

    // Portal web oficial do fornecedor
    @Size(max = 100, message = "O link do site não pode exceder {max} caracteres.")
    @Schema(example = "www.autopecasbrasil.com.br")
    private String site;

    // Nome do vendedor ou contato comercial responsável pelo atendimento
    @Size(max = 100, message = "O nome do contato não pode exceder {max} caracteres.")
    @Schema(example = "Marcos Vendas")
    private String nomeContato;

    // Estrutura de endereço do estabelecimento (validação em cascata)
    @Valid
    private EnderecoResponseDTO endereco;

    // Condições de pagamento acordadas, prazos de entrega ou notas de faturamento
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;
}