package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.VeiculoService;

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
 * Testes de integração da camada web de {@link VeiculoController}.
 */
@WebMvcTest(VeiculoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("VeiculoController")
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VeiculoService veiculoService;

    @Nested
    @DisplayName("POST /api/veiculos")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 quando os dados são válidos")
        void deveRetornar201_quandoDadosValidos() throws Exception {
            CriarVeiculoRequestDTO dto = new CriarVeiculoRequestDTO();
            dto.setClienteId(1L);
            dto.setPlaca("ABC1D23");
            dto.setModelo("Onix");
            dto.setQuilometragem(10000);

            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L)
                    .placa("ABC1D23")
                    .build();

            when(veiculoService.criar(any(CriarVeiculoRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/veiculos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.placa").value("ABC1D23"));
        }

        @Test
        @DisplayName("deve retornar 422 quando a placa já está cadastrada")
        void deveRetornar422_quandoPlacaDuplicada() throws Exception {
            CriarVeiculoRequestDTO dto = new CriarVeiculoRequestDTO();
            dto.setClienteId(1L);
            dto.setPlaca("ABC1D23");

            when(veiculoService.criar(any(CriarVeiculoRequestDTO.class)))
                    .thenThrow(new BusinessException("Já existe um veículo cadastrado com esta placa."));

            mockMvc.perform(post("/api/veiculos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("deve retornar 404 quando o cliente informado não existe")
        void deveRetornar404_quandoClienteNaoExiste() throws Exception {
            CriarVeiculoRequestDTO dto = new CriarVeiculoRequestDTO();
            dto.setClienteId(99L);
            dto.setPlaca("ABC1D23");

            when(veiculoService.criar(any(CriarVeiculoRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Cliente", 99L));

            mockMvc.perform(post("/api/veiculos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/veiculos/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando o veículo existe")
        void deveRetornar200_quandoExiste() throws Exception {
            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder().id(1L).placa("ABC1D23").build();

            when(veiculoService.buscarPorId(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/veiculos/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.placa").value("ABC1D23"));
        }

        @Test
        @DisplayName("deve retornar 404 quando o veículo não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(veiculoService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Veículo", 99L));

            mockMvc.perform(get("/api/veiculos/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/veiculos")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com a página de veículos")
        void deveRetornar200ComPagina() throws Exception {
            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder().id(1L).placa("ABC1D23").build();
            Page<VeiculoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO));

            when(veiculoService.listar(any(), any(), any(), any(), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/veiculos").param("placa", "ABC1D23"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].placa").value("ABC1D23"));
        }
    }

    @Nested
    @DisplayName("PUT /api/veiculos/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 quando os dados são válidos")
        void deveRetornar200_quandoDadosValidos() throws Exception {
            AtualizarVeiculoRequestDTO dto = new AtualizarVeiculoRequestDTO();
            dto.setQuilometragem(15000);

            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder()
                    .id(1L).quilometragem(15000).build();

            when(veiculoService.atualizar(eq(1L), any(AtualizarVeiculoRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(put("/api/veiculos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quilometragem").value(15000));
        }

        @Test
        @DisplayName("deve retornar 422 quando a nova quilometragem é menor que a atual")
        void deveRetornar422_quandoQuilometragemMenor() throws Exception {
            AtualizarVeiculoRequestDTO dto = new AtualizarVeiculoRequestDTO();
            dto.setQuilometragem(5000);

            when(veiculoService.atualizar(eq(1L), any(AtualizarVeiculoRequestDTO.class)))
                    .thenThrow(new BusinessException("A nova quilometragem não pode ser menor que a atual."));

            mockMvc.perform(put("/api/veiculos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PATCH /api/veiculos/{id}/desativar")
    class Desativar {

        @Test
        @DisplayName("deve retornar 204 quando desativado com sucesso")
        void deveRetornar204_quandoDesativadoComSucesso() throws Exception {
            mockMvc.perform(patch("/api/veiculos/{id}/desativar", 1L))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("PATCH /api/veiculos/{id}/reativar")
    class Reativar {

        @Test
        @DisplayName("deve retornar 200 quando reativado com sucesso")
        void deveRetornar200_quandoReativadoComSucesso() throws Exception {
            VeiculoResponseDTO responseDTO = VeiculoResponseDTO.builder().id(1L).ativo(true).build();

            when(veiculoService.reativar(1L)).thenReturn(responseDTO);

            mockMvc.perform(patch("/api/veiculos/{id}/reativar", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ativo").value(true));
        }
    }
}
