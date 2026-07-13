package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) responsável por receber os dados de criação de novos Usuários.
 * Define as credenciais iniciais e o nível de acesso (perfil) para os colaboradores da oficina.
 */
@Data
@Schema(description = "Dados para criação de um novo usuário")
public class CriarUsuarioRequestDTO {

    // Nome completo do colaborador a ser cadastrado
    @NotBlank(message = "O nome é obrigatório.")
    @Schema(example = "João Silva")
    private String nome;

    // Endereço de e-mail que servirá como identificador único (username) para o login do usuário
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado é inválido.")
    @Schema(example = "joao.silva@oficina.com")
    private String email;

    // Senha em texto limpo (será criptografada com BCrypt na camada de Service antes da persistência)
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, max = 50, message = "A senha deve conter entre {min} e {max} caracteres.")
    @Schema(example = "senha123")
    private String senha;

    // Nível de permissão/perfil atribuído no sistema (Ex: ADMIN, GERENTE, MECANICO, ATENDENTE)
    @NotNull(message = "O perfil do usuário é obrigatório.")
    @Schema(example = "ATENDENTE", description = "Determina as regras de controle de acesso (RBAC) às rotas da API.")
    private PerfilUsuario perfil;
}