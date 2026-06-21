package com.example.Projeto_Oficina_Mecanica.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitário para geração, validação e extração de dados de tokens JWT.
 *
 * Access Token  → expira em 1 hora (configurável em jwt.expiration)
 * Refresh Token → expira em 7 dias  (configurável em jwt.refresh-expiration)
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // ── Geração ──────────────────────────────────────────────────────────────

    public String gerarAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("perfil", extrairPerfil(userDetails));
        return gerarToken(claims, userDetails.getUsername(), expiration);
    }

    public String gerarRefreshToken(UserDetails userDetails) {
        return gerarToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
    }

    private String gerarToken(Map<String, Object> claims, String subject, Long expMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getChave())
                .compact();
    }

    // ── Validação ─────────────────────────────────────────────────────────────

    public boolean isTokenValido(String token, UserDetails userDetails) {
        try {
            String email = extrairEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    // ── Extração ──────────────────────────────────────────────────────────────

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    public Long getExpirationEmSegundos() {
        return expiration / 1000;
    }

    public <T> T extrairClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extrairTodosClaims(token);
        return resolver.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getChave() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String extrairPerfil(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");
    }
}