package com.example.Projeto_Oficina_Mecanica.dto;

import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para atualização de um usuário (todos os campos são obrigatórios)")
public class AtualizarUsuarioRequestDTO {

    @Size(min = 3, max = 100, message = "O nome deve ter no mínimo 3 e no máximo 100 caracteres")
    @Schema(example = "João Silva")
    private String nome;

    
    @Email(message = "E-mail inválido")
    @Schema(example = "joao.silva@oficina.com")
    private String email;

    @Size(min = 6, max = 50, message = "Senha deve ter no mínimo 6 e no máximo 50 caracteres")
    @Schema(example = "senha123")
    private String senha;

    @Schema(example = "GERENTE")
    private PerfilUsuario perfil;

}