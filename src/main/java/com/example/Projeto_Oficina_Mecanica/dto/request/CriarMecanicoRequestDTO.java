package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.EspecialidadeMecanico;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) responsável por receber os dados de criação de novos Mecânicos.
 * Utilizado no fluxo de contratação e cadastro do time técnico da oficina.
 */
@Data
@Schema(description = "Dados para cadastro de mecânico")
public class CriarMecanicoRequestDTO {

    // Nome completo do profissional técnico
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome deve conter entre {min} e {max} caracteres.")
    @Schema(example = "Carlos Eduardo Santos")
    private String nome;

    // Cadastro identificador único junto à Receita Federal (Validado por anotação customizada)
    @NotBlank(message = "O CPF é obrigatório.")
    @CpfCnpj
    @Schema(example = "529.982.247-25")
    private String cpf;

    // Telefone fixo residencial ou para recados
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone fixo informado é inválido.")
    @Schema(example = "(81) 3333-4444")
    private String telefone;

    // Telefone móvel para contato direto ou acionamento de equipes
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Telefone celular informado é inválido.")
    @Schema(example = "(81) 99999-1234")
    private String celular;

    // E-mail profissional para comunicações e alertas internos do sistema
    @Email(message = "O e-mail informado é inválido.")
    @Schema(example = "carlos@oficina.com")
    private String email;

    // Registro profissional técnico (CREA), útil para validação de laudos ou revisões estruturais
    @Size(max = 20, message = "O CREA não pode exceder {max} caracteres.")
    @Schema(example = "CREA-PE 12345")
    private String crea;

    // Matriz de capacitações técnicas do mecânico (Exige ao menos uma especialidade ativa)
    @NotEmpty(message = "Informe ao menos uma especialidade.")
    @Schema(example = "[\"MOTOR\", \"ELETRICA\"]")
    private List<EspecialidadeMecanico> especialidades;

    // Histórico profissional, anotações de ferramentas próprias ou restrições de operação
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;

    // Vínculo opcional com o login de acesso do sistema (Tabela de Usuários/Autenticação JWT)
    @Schema(description = "ID do usuário do sistema vinculado ao mecânico (opcional)")
    private Long usuarioId;
}