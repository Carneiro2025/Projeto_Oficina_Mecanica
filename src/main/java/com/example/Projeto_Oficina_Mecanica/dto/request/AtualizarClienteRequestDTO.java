package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO responsável pela atualização dos dados de um cliente.
 *
 * Todos os campos são opcionais, permitindo atualização parcial.
 */
@Data
@Schema(description = "Dados para atualização de cliente")
public class AtualizarClienteRequestDTO {

    @Size(min = 3, max = 150,
            message = "O nome deve conter entre {min} e {max} caracteres.")
    @Schema(example = "João da Silva")
    private String nome;

    @Size(max = 200,
            message = "A Razão Social não pode exceder {max} caracteres.")
    @Schema(example = "Silva Automóveis LTDA")
    private String razaoSocial;

    @CpfCnpj
    @Schema(example = "123.456.789-09")
    private String cpfCnpj;

    @Schema(example = "PF")
    private TipoPessoa tipo;

    @Pattern(
            regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$",
            message = "Telefone informado é inválido."
    )
    @Schema(example = "(81) 3333-4444")
    private String telefone;

    @Pattern(
            regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$",
            message = "Celular informado é inválido."
    )
    @Schema(example = "(81) 99999-8888")
    private String celular;

    @Email(message = "E-mail inválido.")
    @Schema(example = "cliente@email.com")
    private String email;

    @Valid
    private EnderecoRequestDTO endereco;

    @Size(max = 500)
    @Schema(example = "Cliente VIP.")
    private String observacoes;

    @Schema(example = "true")
    private Boolean ativo;
}