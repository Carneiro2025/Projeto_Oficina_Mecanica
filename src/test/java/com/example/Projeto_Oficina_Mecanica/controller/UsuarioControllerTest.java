package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.AlterarSenhaDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarUsuarioRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.UsuarioResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.enums.PerfilUsuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.UsuarioMapper;
import com.example.Projeto_Oficina_Mecanica.service.UsuarioService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada web de {@link UsuarioController}.
 *
 * Diferente de ClienteController, os endpoints aqui usam @PreAuthorize —
 * mesmo com addFilters = false (que só desliga o filtro JWT), o
 * @EnableMethodSecurity do projeto continua avaliando os papéis, então
 * cada teste precisa simular um usuário autenticado com @WithMockUser
 * (ou, no caso de /me, com um Usuario real via .with(user(...)), já que
 * o controller faz cast direto do principal para Usuario).
 *
 * Pré-requisito: dependência spring-security-test no pom.xml (escopo test).
 */
@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @Nested
    @DisplayName("GET /api/usuarios/me")
    class Me {

        @Test
        @DisplayName("deve retornar 200 com os dados do usuário autenticado")
        void deveRetornar200_comUsuarioAutenticado() throws Exception {
            Usuario usuario = Usuario.builder()
                    .id(1L)
                    .nome("Rafael")
                    .email("rafael@oficina.com")
                    .perfil(PerfilUsuario.ADMIN)
                    .ativo(true)
                    .build();

            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder()
                    .id(1L)
                    .nome("Rafael")
                    .email("rafael@oficina.com")
                    .build();

            when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/usuarios/me").with(user(usuario)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("rafael@oficina.com"));
        }

        @Test
        @DisplayName("deve retornar 403 quando não há usuário autenticado")
        void deveRetornar403_quandoNaoAutenticado() throws Exception {
            mockMvc.perform(get("/api/usuarios/me"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/usuarios")
    class Criar {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("deve retornar 201 quando ADMIN envia dados válidos")
        void deveRetornar201_quandoAdminEDadosValidos() throws Exception {
            CriarUsuarioRequestDTO dto = new CriarUsuarioRequestDTO();
            dto.setNome("Rafael");
            dto.setEmail("rafael@oficina.com");
            dto.setSenha("123456");
            dto.setPerfil(PerfilUsuario.ADMIN);

            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder()
                    .id(1L)
                    .email("rafael@oficina.com")
                    .build();

            when(usuarioService.criar(any(CriarUsuarioRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("rafael@oficina.com"));
        }

        @Test
        @WithMockUser(roles = "MECANICO")
        @DisplayName("deve retornar 403 quando quem chama não é ADMIN")
        void deveRetornar403_quandoNaoEAdmin() throws Exception {
            CriarUsuarioRequestDTO dto = new CriarUsuarioRequestDTO();
            dto.setNome("Rafael");
            dto.setEmail("rafael@oficina.com");
            dto.setSenha("123456");
            dto.setPerfil(PerfilUsuario.ADMIN);

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("deve retornar 422 quando o e-mail já está cadastrado")
        void deveRetornar422_quandoEmailDuplicado() throws Exception {
            CriarUsuarioRequestDTO dto = new CriarUsuarioRequestDTO();
            dto.setNome("Rafael");
            dto.setEmail("rafael@oficina.com");
            dto.setSenha("123456");
            dto.setPerfil(PerfilUsuario.ADMIN);

            when(usuarioService.criar(any(CriarUsuarioRequestDTO.class)))
                    .thenThrow(new BusinessException("E-mail já cadastrado."));

            mockMvc.perform(post("/api/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("GET /api/usuarios/{id}")
    class BuscarPorId {

        @Test
        @WithMockUser(roles = "GERENTE")
        @DisplayName("deve retornar 200 quando o usuário existe")
        void deveRetornar200_quandoExiste() throws Exception {
            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder().id(1L).nome("Rafael").build();

            when(usuarioService.buscarPorId(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/usuarios/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Rafael"));
        }

        @Test
        @WithMockUser(roles = "GERENTE")
        @DisplayName("deve retornar 404 quando o usuário não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(usuarioService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Usuário", 99L));

            mockMvc.perform(get("/api/usuarios/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/usuarios")
    class Listar {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("deve retornar 200 com a página de usuários")
        void deveRetornar200ComPagina() throws Exception {
            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder().id(1L).nome("Rafael").build();
            Page<UsuarioResponseDTO> pagina = new PageImpl<>(List.of(responseDTO));

            when(usuarioService.listar(any(), any(), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("Rafael"));
        }
    }

    @Nested
    @DisplayName("PUT /api/usuarios/{id}")
    class Atualizar {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("deve retornar 200 quando os dados são válidos")
        void deveRetornar200_quandoDadosValidos() throws Exception {
            AtualizarUsuarioRequestDTO dto = new AtualizarUsuarioRequestDTO();
            dto.setNome("Rafael Carneiro");

            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder()
                    .id(1L).nome("Rafael Carneiro").build();

            when(usuarioService.atualizar(eq(1L), any(AtualizarUsuarioRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(put("/api/usuarios/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Rafael Carneiro"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/usuarios/{id}/desativar")
    class Desativar {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("deve retornar 204 quando desativado com sucesso")
        void deveRetornar204_quandoDesativadoComSucesso() throws Exception {
            mockMvc.perform(patch("/api/usuarios/{id}/desativar", 1L))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("PATCH /api/usuarios/{id}/reativar")
    class Reativar {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("deve retornar 200 quando reativado com sucesso")
        void deveRetornar200_quandoReativadoComSucesso() throws Exception {
            UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder().id(1L).ativo(true).build();

            when(usuarioService.reativar(1L)).thenReturn(responseDTO);

            mockMvc.perform(patch("/api/usuarios/{id}/reativar", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ativo").value(true));
        }
    }

    @Nested
    @DisplayName("PATCH /api/usuarios/{id}/alterar-senha")
    class AlterarSenha {

        @Test
        @WithMockUser
        @DisplayName("deve retornar 204 quando a senha é alterada com sucesso")
        void deveRetornar204_quandoAlteradaComSucesso() throws Exception {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("123456");
            dto.setNovaSenha("novaSenha123");
            dto.setConfirmarSenha("novaSenha123");

            mockMvc.perform(patch("/api/usuarios/{id}/alterar-senha", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser
        @DisplayName("deve retornar 422 quando a senha atual está incorreta")
        void deveRetornar422_quandoSenhaAtualIncorreta() throws Exception {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("errada");
            dto.setNovaSenha("novaSenha123");
            dto.setConfirmarSenha("novaSenha123");

            org.mockito.Mockito.doThrow(new BusinessException("Senha atual inválida."))
                    .when(usuarioService).alterarSenha(eq(1L), any(AlterarSenhaDTO.class));

            mockMvc.perform(patch("/api/usuarios/{id}/alterar-senha", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}
