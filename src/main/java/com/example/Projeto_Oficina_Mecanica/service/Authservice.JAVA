package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.LoginRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.RefreshTokenRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.TokenResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.mapper.UsuarioMapper;
import com.example.Projeto_Oficina_Mecanica.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService — login()/refresh() seguem o contrato validado por
 * AuthServiceTest.java. A auditoria (registrar()) agora usa a assinatura
 * real de AuditoriaService (String usuario + ip), corrigida nesta sessão
 * após detectar que a versão anterior (Usuario + 5 args) não batia com o
 * AuditoriaServiceImpl real do projeto e quebrava a compilação.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final UsuarioMapper usuarioMapper;

    private final AuditoriaService auditoriaService;

    private String obterIp() {
        try {
            var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            return (attrs != null) ? attrs.getRequest().getRemoteAddr() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO dto) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );

        } catch (BadCredentialsException ex) {

            auditoriaService.registrar(
                    dto.getEmail(),
                    "LOGIN_FALHA",
                    "Usuario",
                    null,
                    "Tentativa de login falhou para o e-mail: " + dto.getEmail(),
                    obterIp()
            );

            throw ex;
        }

        Usuario usuario = (Usuario) userDetailsService.loadUserByUsername(dto.getEmail());

        String accessToken = jwtUtil.gerarAccessToken(usuario);
        String refreshToken = jwtUtil.gerarRefreshToken(usuario);

        auditoriaService.registrar(
                usuario.getEmail(),
                "LOGIN",
                "Usuario",
                usuario.getId(),
                "Login realizado com sucesso",
                obterIp()
        );

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getExpirationEmSegundos())
                .usuario(usuarioMapper.toResponseDTO(usuario))
                .build();
    }

    @Transactional(readOnly = true)
    public TokenResponseDTO refresh(RefreshTokenRequestDTO dto) {

        String email = jwtUtil.extrairEmail(dto.getRefreshToken());

        Usuario usuario = (Usuario) userDetailsService.loadUserByUsername(email);

        if (!jwtUtil.isTokenValido(dto.getRefreshToken(), usuario)) {
            throw new BusinessException("Refresh Token inválido ou expirado.");
        }

        String novoAccessToken = jwtUtil.gerarAccessToken(usuario);
        String novoRefreshToken = jwtUtil.gerarRefreshToken(usuario);

        return TokenResponseDTO.builder()
                .accessToken(novoAccessToken)
                .refreshToken(novoRefreshToken)
                .expiresIn(jwtUtil.getExpirationEmSegundos())
                .usuario(usuarioMapper.toResponseDTO(usuario))
                .build();
    }
}
