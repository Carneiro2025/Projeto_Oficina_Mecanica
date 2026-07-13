package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.EspecialidadeMecanico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) utilizado para a atualização dos dados cadastrais de Mecânicos.
 * Representa uma atualização parcial ou total (PUT/PATCH), onde todos os atributos são opcionais.
 */
@Data
@Schema(description = "Dados para atualização de mecânico (todos os campos opcionais)")
public class AtualizarMecanicoRequestDTO {

    // Nome completo do profissional técnico
    @Size(min = 3, max = 150, message = "O nome do mecânico deve conter entre {min} e {max} caracteres.")
    private String nome;

    // Telefone fixo residencial ou de recado
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone informado é inválido.")
    private String telefone;

    // Telefone móvel/celular para contato direto ou corporativo
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Telefone celular informado é inválido.")
    private String celular;

    // E-mail do profissional para comunicações e acessos do sistema
    @Email(message = "O e-mail informado é inválido.")
    private String email;

    // Registro profissional (CREA), útil caso a oficina exija assinatura técnica para laudos ou modificações estruturais
    @Size(max = 20, message = "O CREA não pode exceder {max} caracteres.")
    private String crea;

    // Lista de especialidades do mecânico (Ex: Alinhamento, Injeção Eletrônica, Câmbio)
    @Schema(description = "Substitui integralmente a lista de especialidades atualmente vinculada ao mecânico.")
    private List<EspecialidadeMecanico> mapEspecialidades;

    // Histórico profissional, observações sobre equipamentos específicos ou restrições de horários
    @Size(max = 500, message = "O campo observações não pode exceder {max} caracteres.")
    private String observacoes;

    // Vínculo opcional com a credencial de acesso do sistema (Tabela de Usuários/Autenticação)
    private Long usuarioId;
}