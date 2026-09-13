package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.RecebimentoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.RecebimentoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.RecebimentoService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada web de {@link RecebimentoController}.
 */
@WebMvcTest(RecebimentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RecebimentoController")
class RecebimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecebimentoService recebimentoService;

    private RecebimentoResponseDTO responseDTO(Long id, String cliente) {
        return RecebimentoResponseDTO.builder()
                .id(id)
                .cliente(cliente)
                .ordemServico(10L)
                .build();
    }

    @Nested
    @DisplayName("POST /api/recebimentos/pagar")
    class RegistrarPagamento {

        @Test
        @DisplayName("deve retornar 200 quando o pagamento é registrado com sucesso")
        void deveRetornar200_quandoPagamentoRegistrado() throws Exception {
            RecebimentoRequestDTO dto = new RecebimentoRequestDTO();
            dto.setOrdemServicoId(10L);
            dto.setFormaPagamento(FormaPagamento.PIX);

            when(recebimentoService.registrarPagamento(any(RecebimentoRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "João da Silva"));

            mockMvc.perform(post("/api/recebimentos/pagar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cliente").value("João da Silva"));
        }

        @Test
        @DisplayName("deve retornar 404 quando não existe recebimento para a OS informada")
        void deveRetornar404_quandoRecebimentoNaoExiste() throws Exception {
            RecebimentoRequestDTO dto = new RecebimentoRequestDTO();
            dto.setOrdemServicoId(999L);

            when(recebimentoService.registrarPagamento(any(RecebimentoRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Recebimento", 999L));

            mockMvc.perform(post("/api/recebimentos/pagar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/recebimentos")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com todos os recebimentos")
        void deveRetornar200ComTodos() throws Exception {
            when(recebimentoService.listar()).thenReturn(List.of(responseDTO(1L, "João da Silva")));

            mockMvc.perform(get("/api/recebimentos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].cliente").value("João da Silva"));
        }
    }

    @Nested
    @DisplayName("GET /api/recebimentos/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando o recebimento existe")
        void deveRetornar200_quandoExiste() throws Exception {
            when(recebimentoService.buscarPorId(1L)).thenReturn(responseDTO(1L, "João da Silva"));

            mockMvc.perform(get("/api/recebimentos/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cliente").value("João da Silva"));
        }

        @Test
        @DisplayName("deve retornar 404 quando o recebimento não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(recebimentoService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Recebimento", 99L));

            mockMvc.perform(get("/api/recebimentos/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/recebimentos/pendentes")
    class ListarPendentes {

        @Test
        @DisplayName("deve retornar 200 com os recebimentos pendentes")
        void deveRetornar200ComPendentes() throws Exception {
            when(recebimentoService.listarPendentes()).thenReturn(List.of(responseDTO(1L, "João da Silva")));

            mockMvc.perform(get("/api/recebimentos/pendentes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].cliente").value("João da Silva"));
        }
    }

    @Nested
    @DisplayName("GET /api/recebimentos/cliente/{clienteId}")
    class ListarPorCliente {

        @Test
        @DisplayName("deve retornar 200 com os recebimentos do cliente")
        void deveRetornar200ComRecebimentosDoCliente() throws Exception {
            when(recebimentoService.listarPorCliente(1L))
                    .thenReturn(List.of(responseDTO(1L, "João da Silva")));

            mockMvc.perform(get("/api/recebimentos/cliente/{clienteId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].cliente").value("João da Silva"));
        }
    }
}
