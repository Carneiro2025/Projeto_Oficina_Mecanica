package com.example.Projeto_Oficina_Mecanica.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token é Obrigatório")
    private String refreshToken;
}