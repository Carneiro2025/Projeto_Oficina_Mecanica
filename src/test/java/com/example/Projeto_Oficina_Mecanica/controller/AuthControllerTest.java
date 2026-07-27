package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.controller.AuthController;
import com.example.Projeto_Oficina_Mecanica.dto.request.LoginRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.RefreshTokenRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.TokenResponseDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.service.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração da camada web de {@link AuthController}.
 *
 * {@code addFilters = false} desabilita o filtro JWT (não há token para
 * validar nestas rotas — login/refresh são públicas); o {@code AuthService}
 * é mockado para isolar o comportamento HTTP do controller.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("deve retornar 200 e os tokens quando as credenciais são válidas")
        void deveRetornar200_quandoCredenciaisValidas() throws Exception {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("admin@oficina.com");
            dto.setSenha("senha123");

            TokenResponseDTO resposta = TokenResponseDTO.builder()
                    .accessToken("access-token-fake")
                    .refreshToken("refresh-token-fake")
                    .tokenType("Bearer")
                    .expiresIn(3600L)
                    .usuario(UsuarioResponseDTO.builder()
                            .id(1L)
                            .nome("Admin")
                            .email("admin@oficina.com")
                            .perfil(PerfilUsuario.ADMIN)
                            .build())
                    .build();

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(resposta);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-token-fake"))
                    .andExpect(jsonPath("$.usuario.email").value("admin@oficina.com"));
        }

        @Test
        @DisplayName("deve retornar 400 quando o e-mail é inválido")
        void deveRetornar400_quandoEmailInvalido() throws Exception {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("nao-e-um-email");
            dto.setSenha("senha123");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 401 quando as credenciais são inválidas")
        void deveRetornar401_quandoCredenciaisInvalidas() throws Exception {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("admin@oficina.com");
            dto.setSenha("senhaErrada");

            when(authService.login(any(LoginRequestDTO.class)))
                    .thenThrow(new BadCredentialsException("Credenciais inválidas"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class Refresh {

        @Test
        @DisplayName("deve retornar 200 e um novo access token quando o refresh token é válido")
        void deveRetornar200_quandoRefreshTokenValido() throws Exception {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken("refresh-token-valido");

            TokenResponseDTO resposta = TokenResponseDTO.builder()
                    .accessToken("novo-access-token")
                    .refreshToken("novo-refresh-token")
                    .tokenType("Bearer")
                    .expiresIn(3600L)
                    .build();

            when(authService.refresh(any(RefreshTokenRequestDTO.class))).thenReturn(resposta);

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("novo-access-token"));
        }

        @Test
        @DisplayName("deve retornar 422 quando o refresh token é inválido")
        void deveRetornar422_quandoRefreshTokenInvalido() throws Exception {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken("refresh-token-invalido");

            when(authService.refresh(any(RefreshTokenRequestDTO.class)))
                    .thenThrow(new BusinessException("Refresh Token inválido ou expirado."));

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("deve retornar 400 quando o refresh token está em branco")
        void deveRetornar400_quandoRefreshTokenEmBranco() throws Exception {
            RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
            dto.setRefreshToken("");

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {

        @Test
        @DisplayName("deve retornar 204")
        void deveRetornar204() throws Exception {
            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isNoContent());
        }
    }
}
