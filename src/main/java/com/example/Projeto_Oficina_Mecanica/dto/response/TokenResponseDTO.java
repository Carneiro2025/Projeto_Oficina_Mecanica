package com.example.Projeto_Oficina_Mecanica.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta com tokes de acesso")
public class TokenResponseDTO {

    @Schema(description = "Access token JWT ( valido por 1 hora)")
    private String accessToken;

    @Schema(description = "Refresh token JWT ( valido por 7 dias)")
    private String refreshToken;

    @Schema(example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Expiração do access token em segundos")
    private Long expiresIn;
}