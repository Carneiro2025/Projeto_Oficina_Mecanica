package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.dto.response.EnderecoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastro de fornecedor")
public class CriarFornecedorRequestDTO {

    @NotBlank(message = "Razão social é obrigatória")
    @Size(min = 3, max = 200)
    @Schema(example = "Auto Peças Brasil LTDA")
    private String razaoSocial;

    @Size(max = 150)
    @Schema(example = "Auto Peças Brasil")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @CpfCnpj
    @Schema(example = "11.222.333/0001-81")
    private String cnpj;

    @Size(max = 20)
    @Schema(example = "IE 123456789")
    private String inscricaoEstadual;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone inválido")
    @Schema(example = "(81) 3210-5000")
    private String telefone;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Celular inválido")
    private String celular;

    @Email(message = "E-mail inválido")
    @Schema(example = "contato@autopecas.com.br")
    private String email;

    @Size(max = 100)
    @Schema(example = "www.autopecasbrasil.com.br")
    private String site;

    @Size(max = 100)
    @Schema(example = "Marcos Vendas")
    private String nomeContato;

    @Valid
    private EnderecoResponseDTO endereco;

    @Size(max = 500)
    private String observacoes;
}