package com.example.Projeto_Oficina_Mecanica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger / OpenAPI 3.0.
 *
 * Acesse: http://localhost:8080/swagger-ui.html
 *
 * Para testar endpoints protegidos:
 *   1. POST /api/auth/login → copie o accessToken
 *   2. Clique em "Authorize" → Bearer {token}
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API - Sistema de Gestão de Oficina Mecânica",
                version = "1.0.0",
                description = "API REST desenvolvida com Spring Boot 3 + JWT + MySQL. " +
                              "Portfólio Profissional.",
                contact = @Contact(
                        name = "Desenvolvedor",
                        email = "dev@oficina.com"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe o token JWT no formato: Bearer {token}"
)
public class SwaggerConfig {
    // Configurações via anotações
}