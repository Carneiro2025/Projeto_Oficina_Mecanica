package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.LoginRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.RefreshTokenRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.TokenResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;




/**
 * Controller responsável pelos endpoints de autenticação e gerenciamento de sessão.
 *
 * Mapeamento das Rotas Públicas:
 * - POST /api/auth/login   → Valida credenciais e gera o par de tokens.
 * - POST /api/auth/refresh → Emite um novo Access Token sem exigir nova senha.
 * - POST /api/auth/logout  → Orienta a invalidação do token do lado do cliente.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", 
     description = "Endpoints responsáveis pelo Login, Refresh Token e Logout do sistema.")
public class AuthController {

    private final AuthService authService;

    /**
     * Realiza a autenticação do usuário na plataforma.
     * Recebe as credenciais (e-mail/username e senha) e retorna os tokens de acesso.
     */
    @PostMapping("/login")
    @Operation(summary = "Realizar Login", description = "Autentica o usuário e retorna um Access Token JWT e um Refresh Token.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Login realizado com sucesso."), @ApiResponse(responseCode = "400",
    description = "Dados inválidos."), @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos.")})
    public ResponseEntity<TokenResponseDTO> login(
        @RequestBody @Valid LoginRequestDTO dto) {

    return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Renova o tempo de sessão do usuário utilizando o Refresh Token.
     * Evita que o usuário precise digitar a senha novamente a cada 1 hora.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar Access Token", description = "Gera um novo Access Token utilizando um Refresh Token válido.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Novo token gerado."), @ApiResponse(responseCode = "401",
    description = "Refresh Token inválido.")})
    public ResponseEntity<TokenResponseDTO> refresh(
        @RequestBody @Valid RefreshTokenRequestDTO dto) {

    return ResponseEntity.ok(authService.refresh(dto));
    }

    /**
     * Realiza a operação de encerramento de sessão.
     * Por ser uma arquitetura Stateless (JWT), a destruição do token ocorre primariamente no cliente.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Realiza o encerramento da sessão do usuário.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Logout realizado.")})
    public ResponseEntity<Void> logout() {

    return ResponseEntity.noContent().build();
    }
    
}