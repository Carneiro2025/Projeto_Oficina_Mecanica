package com.example.Projeto_Oficina_Mecanica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração centralizada do Swagger / OpenAPI.
 *
 * Acesso:
 * http://localhost:8080/swagger-ui/index.html
 *
 * Fluxo:
 * 1. Faça login em /api/auth/login
 * 2. Copie o Access Token
 * 3. Clique em "Authorize"
 * 4. Informe:
 *
 * Bearer SEU_TOKEN
 *
 * 5. Teste qualquer endpoint protegido.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de Gestão de Oficina Mecânica",
                version = "1.0.0",
                description = """
                        API REST desenvolvida em Spring Boot 3.

                        Principais módulos:

                        • Autenticação JWT
                        • Usuários
                        • Clientes
                        • Veículos
                        • Mecânicos
                        • Fornecedores
                        • Produtos
                        • Estoque
                        • Ordem de Serviço
                        • Recebimentos
                        • Dashboard
                        • Relatórios Gerenciais
                        """,
                contact = @Contact(
                        name = "Rafael Carneiro",
                        email = "seu-email@exemplo.com"
                ),
                license = @License(
                        name = "MIT License"
                ),
                extensions = {
                        @Extension(name = "Projeto", properties = {})
                }
        ),

        servers = {
                @Server(
                        description = "Servidor Local",
                        url = "http://localhost:8080"
                )
        }
)

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe apenas o JWT gerado no login."
)
public class SwaggerConfig {

}