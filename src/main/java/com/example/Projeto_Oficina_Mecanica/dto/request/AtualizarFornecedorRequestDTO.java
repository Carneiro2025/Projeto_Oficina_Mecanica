package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Data Transfer Object (DTO) utilizado para a atualização dos dados cadastrais de Fornecedores.
 * Como este fluxo representa uma atualização parcial (PATCH/PUT), todos os campos são tratados como opcionais.
 */
@Data
@Schema(description = "Dados para atualização de fornecedor (todos os campos opcionais)")
public class AtualizarFornecedorRequestDTO {

    // Nome jurídico/oficial da empresa fornecedora de peças ou insumos
    @Size(min = 3, max = 200, message = "A Razão Social deve conter entre {min} e {max} caracteres.")
    private String razaoSocial;

    // Nome comercial/marca pelo qual o fornecedor é conhecido
    @Size(max = 150, message = "O Nome Fantasia não pode exceder {max} caracteres.")
    private String nomeFantasia;

    // Registro fiscal do fornecedor junto à Receita Estadual (utilizado na validação das Notas Fiscais)
    @Size(max = 20, message = "A Inscrição Estadual não pode exceder {max} caracteres.")
    private String inscricaoEstadual;

    // Telefone fixo comercial do estabelecimento
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone comercial inválido.")
    private String telefone;

    // Telefone móvel/celular para contato direto com o fornecedor
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Telefone celular inválido.")
    private String celular;

    // E-mail corporativo para envio de cotações e Notas Fiscais
    @Email(message = "O e-mail informado é inválido.")
    private String email;

    // Endereço web/portal do parceiro comercial
    @Size(max = 100, message = "O link do site não pode exceder {max} caracteres.")
    private String site;

    // Nome do atendente, vendedor ou contato comercial responsável pelas negociações
    @Size(max = 100, message = "O nome do contato não pode exceder {max} caracteres.")
    private String nomeContato;

    // Objeto aninhado contendo as informações de localização física do fornecedor
    @Valid
    private EnderecoResponseDTO endereco;

    // Anotações gerais, históricos de entrega ou condições específicas acordadas com o fornecedor
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;
}