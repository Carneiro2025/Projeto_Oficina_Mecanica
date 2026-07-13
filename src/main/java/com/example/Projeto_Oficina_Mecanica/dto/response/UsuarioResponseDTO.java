package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do usuário(sem senha)")
public class UsuarioResponseDTO {
    
    @Schema(example = "1") 
    private Long id;

    @Schema(example = "João Silva")
    private String nome;

    @Schema(example = "joao@oficina.com")
    private String email;

    @Schema(example = "ADMIN")
    private PerfilUsuario perfil;

    @Schema(example = "true") 
    private Boolean ativo;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

