package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastro de cliente PF ou PJ")
public class CriarClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150)
    @Schema(example = "João da Silva")
    private String nome;

    @Size(max = 200)
    @Schema(example = "Silva Automóveis LTDA")
    private String razaoSocial;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @CpfCnpj
    @Schema(example = "123.456.789-09")
    private String cpfCnpj;

    @NotNull(message = "Tipo é obrigatório")
    @Schema(example = "PF")
    private TipoPessoa tipo;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone inválido")
    @Schema(example = "(81) 3333-4444")
    private String telefone;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Celular inválido")
    @Schema(example = "(81) 99999-8888")
    private String celular;

    @Email(message = "E-mail inválido")
    @Schema(example = "joao@email.com")
    private String email;

    @Valid
    private EnderecoResponseDTO endereco;

    @Size(max = 500)
    private String observacoes;
}

