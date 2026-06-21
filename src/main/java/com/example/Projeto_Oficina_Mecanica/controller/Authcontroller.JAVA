package com.example.Projeto_Oficina_Mecanica.controller;

import com.oficina.dto.request.LoginRequestDTO;
import com.oficina.dto.request.RefreshTokenRequestDTO;
import com.oficina.dto.response.TokenResponseDTO;
import com.oficina.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação.
 *
 * POST /api/auth/login   → autentica e retorna JWT
 * POST /api/auth/refresh → renova o access token
 * POST /api/auth/logout  → instrução ao cliente para descartar o token
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, refresh e logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Retorna access token (1h) e refresh token (7d)")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Gera novo access token usando o refresh token")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody @Valid RefreshTokenRequestDTO dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "O cliente deve descartar os tokens localmente")
    public ResponseEntity<Void> logout() {
        // JWT é stateless: o logout é feito no cliente descartando o token.
        // Em produção, implemente uma blacklist com Redis para revogar tokens.
        return ResponseEntity.noContent().build();
    }
}