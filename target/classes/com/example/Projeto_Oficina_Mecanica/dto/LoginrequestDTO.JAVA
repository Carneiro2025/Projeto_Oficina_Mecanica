package com.example.Projeto_Oficina_Mecanica.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciais para autenticação do usuário")
public class LoginrequestDTO {
    
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Schema(example = "admin@oficina.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(example = "senha123")
    private String senha;

}