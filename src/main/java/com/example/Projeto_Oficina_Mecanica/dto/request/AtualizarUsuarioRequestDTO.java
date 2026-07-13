package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) utilizado para a atualização cadastral e de credenciais de Usuários.
 * Contém os dados de identificação, autenticação e o nível de acesso dentro do sistema da oficina.
 */
@Data
@Schema(description = "Dados para atualização de um usuário (todos os campos são obrigatórios)")
public class AtualizarUsuarioRequestDTO {

    // Nome completo ou apelido corporativo do usuário
    @Size(min = 3, max = 100, message = "O nome deve ter no mínimo 3 e no máximo 100 caracteres.")
    @Schema(example = "João Silva")
    private String nome;

    // Endereço de e-mail utilizado como login/username para autenticação via JWT
    @Email(message = "O e-mail informado é inválido.")
    @Schema(example = "joao.silva@oficina.com")
    private String email;

    // Senha de acesso (será criptografada com BCrypt na camada de serviço antes de persistir)
    @Size(min = 6, max = 50, message = "A senha deve ter no mínimo 6 e no máximo 50 caracteres.")
    @Schema(example = "senha123")
    private String senha;

    // Nível de permissão atribuído ao usuário (Ex: ADMIN, GERENTE, MECANICO, ATENDENTE)
    @Schema(example = "GERENTE", description = "Define o nível de acesso e as permissões de rota do usuário.")
    private PerfilUsuario perfil;
}