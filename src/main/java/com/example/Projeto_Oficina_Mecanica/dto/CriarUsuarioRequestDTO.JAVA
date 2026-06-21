package com.example.Projeto_Oficina_Mecanica.dto;

import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para criação de um novo usuário")
public class CriarUsuarioRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(example = "João Silva")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Schema(example = "joao.silva@oficina.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 50, message = "A senha deve ter no mínimo 6 e no máximo 50 caracteres")
    @Schema(example = "senha123")
    private String senha;

    @NotNull(message = "Perfil do usuário é obrigatório")
    @Schema(example = "ATENDENTE")
    private PerfilUsuario perfil;

}





