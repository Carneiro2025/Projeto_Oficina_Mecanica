package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.LoginRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.RefreshTokenRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.TokenResponseDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.mapper.UsuarioMapper;
import com.example.Projeto_Oficina_Mecanica.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários de {@link AuthService}.
 *
 * AuthenticationManager, UserDetailsService, JwtUtil e UsuarioMapper são
 * mockados: o objetivo aqui é validar a orquestração do fluxo de login/refresh,
 * não a implementação real de segurança do Spring nem a geração de JWT.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@oficina.com");
        usuario.setSenha("$2a$10$hashSimulado");
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setAtivo(true);
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("deve retornar tokens e dados do usuário quando as credenciais são válidas")
        void deveRetornarTokens_quandoCredenciaisValidas() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("joao@oficina.com");
            dto.setSenha("senha123");

            UsuarioResponseDTO usuarioResponseDTO = UsuarioResponseDTO.builder()
                    .id(1L)
                    .nome("João Silva")
                    .email("joao@oficina.com")
                    .perfil(PerfilUsuario.ADMIN)
                    .build();

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(null); // autenticação bem-sucedida não lança exceção
            when(userDetailsService.loadUserByUsername("joao@oficina.com")).thenReturn(usuario);
            when(jwtUtil.gerarAccessToken(usuario)).thenReturn("access-token-fake");
            when(jwtUtil.gerarRefreshToken(usuario)).thenReturn("refresh-token-fake");
            when(jwtUtil.getExpirationEmSegundos()).thenReturn(3600L);
            when(usuarioMapper.toResponseDTO(usuario)).thenReturn(usuarioResponseDTO);

            TokenResponseDTO resposta = authService.login(dto);

            assertThat(resposta.getAccessToken()).isEqualTo("access-token-fake");
            assertThat(resposta.getRefreshToken()).isEqualTo("refresh-token-fake");
            assertThat(resposta.getExpiresIn()).isEqualTo(3600L);
            assertThat(resposta.getUsuario()).isNotNull();
            assertThat(resposta.getUsuario().getEmail()).isEqualTo("joao@oficina.com");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("deve propagar BadCredentialsException quando as credenciais são inválidas")
        void deveLancarBadCredentialsException_quandoCredenciaisInvalidas() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("joao@oficina.com");
            dto.setSenha("senhaErrada");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciais inválidas"));

            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(BadCredentialsException.class);

            verifyNoInteractions(jwtUtil);
        }
    }

    @Nested
    @DisplayName("refresh()")
    class Refresh {

        @Test
        @DisplayName("deve gerar novo par de tokens quando o refresh token é válido")
        void deveGerarNovoToken_quandoRefreshTokenValido() {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken("refresh-token-valido");

            UsuarioResponseDTO usuarioResponseDTO = UsuarioResponseDTO.builder()
                    .id(1L)
                    .email("joao@oficina.com")
                    .build();

            when(jwtUtil.extrairEmail("refresh-token-valido")).thenReturn("joao@oficina.com");
            when(userDetailsService.loadUserByUsername("joao@oficina.com")).thenReturn(usuario);
            when(jwtUtil.isTokenValido("refresh-token-valido", usuario)).thenReturn(true);
            when(jwtUtil.gerarAccessToken(usuario)).thenReturn("novo-access-token");
            when(jwtUtil.gerarRefreshToken(usuario)).thenReturn("novo-refresh-token");
            when(jwtUtil.getExpirationEmSegundos()).thenReturn(3600L);
            when(usuarioMapper.toResponseDTO(usuario)).thenReturn(usuarioResponseDTO);

            TokenResponseDTO resposta = authService.refresh(dto);

            assertThat(resposta.getAccessToken()).isEqualTo("novo-access-token");
            assertThat(resposta.getRefreshToken()).isEqualTo("novo-refresh-token");
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o refresh token é inválido ou expirado")
        void deveLancarBusinessException_quandoRefreshTokenInvalido() {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken("refresh-token-expirado");

            when(jwtUtil.extrairEmail("refresh-token-expirado")).thenReturn("joao@oficina.com");
            when(userDetailsService.loadUserByUsername("joao@oficina.com")).thenReturn(usuario);
            when(jwtUtil.isTokenValido("refresh-token-expirado", usuario)).thenReturn(false);

            assertThatThrownBy(() -> authService.refresh(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Refresh Token inválido ou expirado");

            verify(jwtUtil, never()).gerarAccessToken(any());
        }
    }
}
