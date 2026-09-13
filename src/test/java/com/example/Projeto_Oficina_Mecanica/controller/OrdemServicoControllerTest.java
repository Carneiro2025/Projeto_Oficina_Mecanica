package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.OrdemServicoService;

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
 * Testes de integração da camada web de {@link OrdemServicoController}.
 */
@WebMvcTest(OrdemServicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OrdemServicoController")
class OrdemServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdemServicoService service;

    private OrdemServicoResponseDTO responseDTO(Long id, String numero) {
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();
        dto.setId(id);
        dto.setNumero(numero);
        return dto;
    }

    @Nested
    @DisplayName("POST /api/ordens-servico")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 quando os dados são válidos")
        void deveRetornar201_quandoDadosValidos() throws Exception {
            CriarOrdemServicoRequestDTO dto = new CriarOrdemServicoRequestDTO();
            dto.setNumero("OS-001");
            dto.setClienteId(1L);
            dto.setVeiculoId(1L);
            dto.setItens(List.of());

            when(service.criar(any(CriarOrdemServicoRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "OS-001"));

            mockMvc.perform(post("/api/ordens-servico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.numero").value("OS-001"));
        }

        @Test
        @DisplayName("deve retornar 422 quando o número já está cadastrado")
        void deveRetornar422_quandoNumeroDuplicado() throws Exception {
            CriarOrdemServicoRequestDTO dto = new CriarOrdemServicoRequestDTO();
            dto.setNumero("OS-001");
            dto.setClienteId(1L);
            dto.setVeiculoId(1L);
            dto.setItens(List.of());

            when(service.criar(any(CriarOrdemServicoRequestDTO.class)))
                    .thenThrow(new BusinessException("Número de OS já cadastrado."));

            mockMvc.perform(post("/api/ordens-servico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("GET /api/ordens-servico/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando a OS existe")
        void deveRetornar200_quandoExiste() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(responseDTO(1L, "OS-001"));

            mockMvc.perform(get("/api/ordens-servico/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numero").value("OS-001"));
        }

        @Test
        @DisplayName("deve retornar 404 quando a OS não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Ordem de Serviço", 99L));

            mockMvc.perform(get("/api/ordens-servico/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/ordens-servico")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com a página de ordens de serviço")
        void deveRetornar200ComPagina() throws Exception {
            Page<OrdemServicoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "OS-001")));

            when(service.listar(any())).thenReturn(pagina);

            mockMvc.perform(get("/api/ordens-servico"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numero").value("OS-001"));
        }
    }

    @Nested
    @DisplayName("GET /api/ordens-servico/cliente/{clienteId}")
    class BuscarPorCliente {

        @Test
        @DisplayName("deve retornar 200 com as ordens do cliente")
        void deveRetornar200ComOrdensDoCliente() throws Exception {
            Page<OrdemServicoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "OS-001")));

            when(service.buscarPorCliente(eq(1L), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/ordens-servico/cliente/{clienteId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numero").value("OS-001"));
        }
    }

    @Nested
    @DisplayName("GET /api/ordens-servico/veiculo/{veiculoId}")
    class BuscarPorVeiculo {

        @Test
        @DisplayName("deve retornar 200 com as ordens do veículo")
        void deveRetornar200ComOrdensDoVeiculo() throws Exception {
            Page<OrdemServicoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "OS-001")));

            when(service.buscarPorVeiculo(eq(1L), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/ordens-servico/veiculo/{veiculoId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numero").value("OS-001"));
        }
    }

    @Nested
    @DisplayName("GET /api/ordens-servico/status/{status}")
    class BuscarPorStatus {

        @Test
        @DisplayName("deve retornar 200 com as ordens no status informado")
        void deveRetornar200ComOrdensNoStatus() throws Exception {
            Page<OrdemServicoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "OS-001")));

            when(service.buscarPorStatus(eq(StatusOrdemServico.ABERTA), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/ordens-servico/status/{status}", "ABERTA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numero").value("OS-001"));
        }
    }

    @Nested
    @DisplayName("GET /api/ordens-servico/filtros")
    class Filtros {

        @Test
        @DisplayName("deve retornar 200 com as ordens filtradas")
        void deveRetornar200ComOrdensFiltradas() throws Exception {
            Page<OrdemServicoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "OS-001")));

            when(service.buscarComFiltros(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(pagina);

            mockMvc.perform(get("/api/ordens-servico/filtros").param("numero", "OS-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].numero").value("OS-001"));
        }
    }

    @Nested
    @DisplayName("PUT /api/ordens-servico/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 quando os dados são válidos")
        void deveRetornar200_quandoDadosValidos() throws Exception {
            AtualizarOrdemServicoRequestDTO dto = new AtualizarOrdemServicoRequestDTO();
            dto.setObservacoes("Cliente pediu revisão extra");

            when(service.atualizar(eq(1L), any(AtualizarOrdemServicoRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "OS-001"));

            mockMvc.perform(put("/api/ordens-servico/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numero").value("OS-001"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/ordens-servico/{id}/finalizar")
    class Finalizar {

        @Test
        @DisplayName("deve retornar 200 quando finalizada com sucesso")
        void deveRetornar200_quandoFinalizadaComSucesso() throws Exception {
            when(service.finalizar(1L)).thenReturn(responseDTO(1L, "OS-001"));

            mockMvc.perform(patch("/api/ordens-servico/{id}/finalizar", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numero").value("OS-001"));
        }

        @Test
        @DisplayName("deve retornar 422 quando a OS já está finalizada")
        void deveRetornar422_quandoJaFinalizada() throws Exception {
            when(service.finalizar(1L))
                    .thenThrow(new BusinessException("Ordem de Serviço OS-001 já está finalizada."));

            mockMvc.perform(patch("/api/ordens-servico/{id}/finalizar", 1L))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PATCH /api/ordens-servico/{id}/cancelar")
    class Cancelar {

        @Test
        @DisplayName("deve retornar 204 quando cancelada com sucesso")
        void deveRetornar204_quandoCanceladaComSucesso() throws Exception {
            mockMvc.perform(patch("/api/ordens-servico/{id}/cancelar", 1L))
                    .andExpect(status().isNoContent());
        }
    }
}
