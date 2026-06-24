package com.example.Projeto_Oficina_Mecanica.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Dados para atualização de fornecedor (todos os campos opcionais)")
public class AtualizarFornecedorRequestDTO {

    @Size(min = 3, max = 200)
    private String razaoSocial;

    @Size(max = 150)
    private String nomeFantasia;

    @Size(max = 20)
    private String inscricaoEstadual;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Celular inválido")
    private String celular;

    @Email(message = "E-mail inválido")
    private String email;

    @Size(max = 100)
    private String site;

    @Size(max = 100)
    private String nomeContato;

    @Valid
    private EnderecoDTO endereco;

    @Size(max = 500)
    private String observacoes;
}