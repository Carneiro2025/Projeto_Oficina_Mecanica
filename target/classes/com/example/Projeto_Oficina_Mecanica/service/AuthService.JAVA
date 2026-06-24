package com.example.Projeto_Oficina_Mecanica.service;

import com.oficina.dto.request.LoginRequestDTO;
import com.oficina.dto.request.RefreshTokenRequestDTO;
import com.oficina.dto.response.TokenResponseDTO;
import com.oficina.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação.
 *
 * Responsabilidades:
 *  - Autenticar usuário com e-mail e senha
 *  - Gerar access token + refresh token
 *  - Renovar access token via refresh token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Autentica o usuário e retorna os tokens JWT.
     *
     * @throws BadCredentialsException se credenciais inválidas
     * @throws DisabledException       se conta inativa
     */
    public TokenResponseDTO login(LoginRequestDTO dto) {
        // Spring Security valida e-mail + senha + conta ativa
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        UserDetails user = userDetailsService.loadUserByUsername(dto.getEmail());
        log.info("Login realizado: {}", dto.getEmail());

        return buildTokenResponse(user);
    }

    /**
     * Renova o access token usando o refresh token.
     *
     * @throws io.jsonwebtoken.JwtException se token inválido ou expirado
     */
    public TokenResponseDTO refresh(RefreshTokenRequestDTO dto) {
        String email = jwtUtil.extrairEmail(dto.getRefreshToken());
        UserDetails user = userDetailsService.loadUserByUsername(email);

        if (!jwtUtil.isTokenValido(dto.getRefreshToken(), user)) {
            throw new IllegalArgumentException("Refresh token inválido ou expirado");
        }

        log.info("Token renovado para: {}", email);
        return buildTokenResponse(user);
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private TokenResponseDTO buildTokenResponse(UserDetails user) {
        return TokenResponseDTO.builder()
                .accessToken(jwtUtil.gerarAccessToken(user))
                .refreshToken(jwtUtil.gerarRefreshToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationEmSegundos())
                .build();
    }
}