package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.controller.ClienteController;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.ClienteService;

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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada web de {@link ClienteController}.
 *
 * Usa {@code @WebMvcTest}, que sobe apenas o contexto MVC (controller,
 * conversores de JSON, {@code @ControllerAdvice}), sem banco de dados real.
 * O {@code ClienteService} é substituído por um mock, e o filtro de
 * segurança (JWT) é desabilitado com {@code addFilters = false} — o foco
 * aqui é a camada HTTP (rotas, validação de payload, status codes),
 * não a autenticação em si.
 */
@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    @Nested
    @DisplayName("POST /api/clientes")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 quando os dados são válidos")
        void deveRetornar201_quandoDadosValidos() throws Exception {
            CriarClienteRequestDTO dto = new CriarClienteRequestDTO();
            dto.setNome("João da Silva");
            dto.setCpfCnpj("123.456.789-09");
            dto.setTipo(TipoPessoa.PF);

            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João da Silva")
                    .cpfCnpj("123.456.789-09")
                    .ativo(true)
                    .build();

            when(clienteService.criar(any(CriarClienteRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("João da Silva"));
        }

        @Test
        @DisplayName("deve retornar 400 quando o nome está em branco")
        void deveRetornar400_quandoNomeEmBranco() throws Exception {
            CriarClienteRequestDTO dto = new CriarClienteRequestDTO();
            dto.setNome(""); // inválido: @NotBlank
            dto.setCpfCnpj("123.456.789-09");
            dto.setTipo(TipoPessoa.PF);

            mockMvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 422 quando o CPF/CNPJ já está cadastrado")
        void deveRetornar422_quandoCpfCnpjDuplicado() throws Exception {
            CriarClienteRequestDTO dto = new CriarClienteRequestDTO();
            dto.setNome("João da Silva");
            dto.setCpfCnpj("123.456.789-09");
            dto.setTipo(TipoPessoa.PF);

            when(clienteService.criar(any(CriarClienteRequestDTO.class)))
                    .thenThrow(new BusinessException("CPF/CNPJ já cadastrado."));

            mockMvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("GET /api/clientes/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando o cliente existe")
        void deveRetornar200_quandoExiste() throws Exception {
            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João da Silva")
                    .build();

            when(clienteService.buscarPorId(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/clientes/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João da Silva"));
        }

        @Test
        @DisplayName("deve retornar 404 quando o cliente não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(clienteService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Cliente", 99L));

            mockMvc.perform(get("/api/clientes/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/clientes")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com a página de clientes")
        void deveRetornar200ComPagina() throws Exception {
            ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                    .id(1L)
                    .nome("João da Silva")
                    .build();

            Page<ClienteResponseDTO> pagina = new PageImpl<>(List.of(responseDTO));

            when(clienteService.listar(any(), any(), any(), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/clientes")
                            .param("nome", "João"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("João da Silva"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/clientes/{id}/desativar")
    class Desativar {

        @Test
        @DisplayName("deve retornar 204 quando desativado com sucesso")
        void deveRetornar204_quandoDesativadoComSucesso() throws Exception {
            mockMvc.perform(patch("/api/clientes/{id}/desativar", 1L))
                    .andExpect(status().isNoContent());
        }
    }
}
