package com.example.Projeto_Oficina_Mecanica.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO responsável pela alteração da senha de um usuário.
 *
 * Fluxo:
 * 1. Informar a senha atual.
 * 2. Informar a nova senha.
 * 3. Confirmar a nova senha.
 */
@Data
@Schema(description = "Dados para alteração da senha do usuário")
public class AlterarSenhaDTO {

    @NotBlank(message = "A senha atual é obrigatória.")
    @Schema(
            description = "Senha atualmente utilizada pelo usuário",
            example = "SenhaAtual123"
    )
    private String senhaAtual;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, max = 50,
            message = "A nova senha deve conter entre {min} e {max} caracteres.")
    @Schema(
            description = "Nova senha do usuário",
            example = "NovaSenha123")
    private String novaSenha;

    @NotBlank(message = "A confirmação da senha é obrigatória.")
    @Schema(
            description = "Confirmação da nova senha",
            example = "NovaSenha123"    )
    private String confirmarSenha;
}

