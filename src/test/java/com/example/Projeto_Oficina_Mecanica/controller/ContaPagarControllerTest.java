package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.ContaPagarService;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada web de {@link ContaPagarController}.
 */
@WebMvcTest(ContaPagarController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ContaPagarController")
class ContaPagarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContaPagarService service;

    private ContaPagarResponseDTO responseDTO(Long id, String descricao) {
        ContaPagarResponseDTO dto = new ContaPagarResponseDTO();
        dto.setId(id);
        dto.setDescricao(descricao);
        return dto;
    }

    @Nested
    @DisplayName("POST /api/contas-pagar")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 quando os dados são válidos")
        void deveRetornar201_quandoDadosValidos() throws Exception {
            CriarContaPagarRequestDTO dto = new CriarContaPagarRequestDTO();
            dto.setFornecedorId(1L);
            dto.setDescricao("Compra de peças");
            dto.setValor(new BigDecimal("500.00"));
            dto.setDataVencimento(LocalDate.now().plusDays(30));

            when(service.criar(any(CriarContaPagarRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "Compra de peças"));

            mockMvc.perform(post("/api/contas-pagar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.descricao").value("Compra de peças"));
        }

        @Test
        @DisplayName("deve retornar 404 quando o fornecedor não existe")
        void deveRetornar404_quandoFornecedorNaoExiste() throws Exception {
            CriarContaPagarRequestDTO dto = new CriarContaPagarRequestDTO();
            dto.setFornecedorId(99L);
            dto.setValor(new BigDecimal("500.00"));

            when(service.criar(any(CriarContaPagarRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Fornecedor", 99L));

            mockMvc.perform(post("/api/contas-pagar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/contas-pagar/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando a conta existe")
        void deveRetornar200_quandoExiste() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(responseDTO(1L, "Compra de peças"));

            mockMvc.perform(get("/api/contas-pagar/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.descricao").value("Compra de peças"));
        }

        @Test
        @DisplayName("deve retornar 404 quando a conta não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Conta a Pagar", 99L));

            mockMvc.perform(get("/api/contas-pagar/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/contas-pagar")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com a página de contas")
        void deveRetornar200ComPagina() throws Exception {
            Page<ContaPagarResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "Compra de peças")));

            when(service.listar(any())).thenReturn(pagina);

            mockMvc.perform(get("/api/contas-pagar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].descricao").value("Compra de peças"));
        }
    }

    @Nested
    @DisplayName("GET /api/contas-pagar/status/{status}")
    class BuscarPorStatus {

        @Test
        @DisplayName("deve retornar 200 com as contas no status informado")
        void deveRetornar200ComContasNoStatus() throws Exception {
            when(service.buscarPorStatus(StatusContaPagar.PENDENTE))
                    .thenReturn(List.of(responseDTO(1L, "Compra de peças")));

            mockMvc.perform(get("/api/contas-pagar/status/{status}", "PENDENTE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].descricao").value("Compra de peças"));
        }
    }

    @Nested
    @DisplayName("GET /api/contas-pagar/pendentes")
    class ListarPendentes {

        @Test
        @DisplayName("deve retornar 200 com as contas pendentes")
        void deveRetornar200ComPendentes() throws Exception {
            when(service.listarPendentes()).thenReturn(List.of(responseDTO(1L, "Compra de peças")));

            mockMvc.perform(get("/api/contas-pagar/pendentes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].descricao").value("Compra de peças"));
        }
    }

    @Nested
    @DisplayName("GET /api/contas-pagar/fornecedor/{fornecedorId}")
    class BuscarPorFornecedor {

        @Test
        @DisplayName("deve retornar 200 com as contas do fornecedor")
        void deveRetornar200ComContasDoFornecedor() throws Exception {
            when(service.buscarPorFornecedor(1L)).thenReturn(List.of(responseDTO(1L, "Compra de peças")));

            mockMvc.perform(get("/api/contas-pagar/fornecedor/{fornecedorId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].descricao").value("Compra de peças"));
        }
    }

    @Nested
    @DisplayName("PUT /api/contas-pagar/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 quando os dados são válidos")
        void deveRetornar200_quandoDadosValidos() throws Exception {
            AtualizarContaPagarRequestDTO dto = new AtualizarContaPagarRequestDTO();
            dto.setDescricao("Compra de peças - revisada");

            when(service.atualizar(eq(1L), any(AtualizarContaPagarRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "Compra de peças - revisada"));

            mockMvc.perform(put("/api/contas-pagar/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.descricao").value("Compra de peças - revisada"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/contas-pagar/{id}/pagamento")
    class RegistrarPagamento {

        @Test
        @DisplayName("deve retornar 200 quando o pagamento é registrado com sucesso")
        void deveRetornar200_quandoPagamentoRegistrado() throws Exception {
            AtualizarContaPagarRequestDTO dto = new AtualizarContaPagarRequestDTO();
            dto.setDataPagamento(LocalDate.now());
            dto.setFormaPagamento(FormaPagamento.PIX);

            when(service.registrarPagamento(eq(1L), any(AtualizarContaPagarRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "Compra de peças"));

            mockMvc.perform(patch("/api/contas-pagar/{id}/pagamento", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.descricao").value("Compra de peças"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/contas-pagar/{id}")
    class Excluir {

        @Test
        @DisplayName("deve retornar 204 quando excluída com sucesso")
        void deveRetornar204_quandoExcluidaComSucesso() throws Exception {
            mockMvc.perform(delete("/api/contas-pagar/{id}", 1L))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("deve retornar 404 quando a conta não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            org.mockito.Mockito.doThrow(new ResourceNotFoundException("Conta a Pagar", 99L))
                    .when(service).excluir(99L);

            mockMvc.perform(delete("/api/contas-pagar/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }
}
