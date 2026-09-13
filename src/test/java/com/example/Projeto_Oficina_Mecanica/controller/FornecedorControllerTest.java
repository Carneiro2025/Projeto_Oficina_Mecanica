package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.FornecedorService;

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
 * Testes de integração da camada web de {@link FornecedorController}.
 */
@WebMvcTest(FornecedorController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FornecedorController")
class FornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FornecedorService fornecedorService;

    private FornecedorResponseDTO responseDTO(Long id, String razaoSocial) {
        FornecedorResponseDTO dto = new FornecedorResponseDTO();
        dto.setId(id);
        dto.setRazaoSocial(razaoSocial);
        return dto;
    }

    @Nested
    @DisplayName("POST /api/fornecedores")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 quando os dados são válidos")
        void deveRetornar201_quandoDadosValidos() throws Exception {
            CriarFornecedorRequestDTO dto = new CriarFornecedorRequestDTO();
            dto.setRazaoSocial("Auto Peças LTDA");
            dto.setCnpj("12.345.678/0001-90");
            dto.setEmail("contato@autopecas.com");

            when(fornecedorService.criar(any(CriarFornecedorRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "Auto Peças LTDA"));

            mockMvc.perform(post("/api/fornecedores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.razaoSocial").value("Auto Peças LTDA"));
        }

        @Test
        @DisplayName("deve retornar 422 quando o CNPJ já está cadastrado")
        void deveRetornar422_quandoCnpjDuplicado() throws Exception {
            CriarFornecedorRequestDTO dto = new CriarFornecedorRequestDTO();
            dto.setRazaoSocial("Auto Peças LTDA");
            dto.setCnpj("12.345.678/0001-90");

            when(fornecedorService.criar(any(CriarFornecedorRequestDTO.class)))
                    .thenThrow(new BusinessException("CNPJ já cadastrado."));

            mockMvc.perform(post("/api/fornecedores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("GET /api/fornecedores/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando o fornecedor existe")
        void deveRetornar200_quandoExiste() throws Exception {
            when(fornecedorService.buscarPorId(1L)).thenReturn(responseDTO(1L, "Auto Peças LTDA"));

            mockMvc.perform(get("/api/fornecedores/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.razaoSocial").value("Auto Peças LTDA"));
        }

        @Test
        @DisplayName("deve retornar 404 quando o fornecedor não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(fornecedorService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Fornecedor", 99L));

            mockMvc.perform(get("/api/fornecedores/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/fornecedores")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com a página de fornecedores")
        void deveRetornar200ComPagina() throws Exception {
            Page<FornecedorResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "Auto Peças LTDA")));

            when(fornecedorService.listar(any(), any(), any(), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/fornecedores").param("razaoSocial", "Auto"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].razaoSocial").value("Auto Peças LTDA"));
        }
    }

    @Nested
    @DisplayName("PUT /api/fornecedores/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 quando os dados são válidos")
        void deveRetornar200_quandoDadosValidos() throws Exception {
            AtualizarFornecedorRequestDTO dto = new AtualizarFornecedorRequestDTO();
            dto.setRazaoSocial("Auto Peças Silva LTDA");

            when(fornecedorService.atualizar(eq(1L), any(AtualizarFornecedorRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "Auto Peças Silva LTDA"));

            mockMvc.perform(put("/api/fornecedores/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.razaoSocial").value("Auto Peças Silva LTDA"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/fornecedores/{id}/desativar")
    class Desativar {

        @Test
        @DisplayName("deve retornar 204 quando desativado com sucesso")
        void deveRetornar204_quandoDesativadoComSucesso() throws Exception {
            mockMvc.perform(patch("/api/fornecedores/{id}/desativar", 1L))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("PATCH /api/fornecedores/{id}/reativar")
    class Reativar {

        @Test
        @DisplayName("deve retornar 200 quando reativado com sucesso")
        void deveRetornar200_quandoReativadoComSucesso() throws Exception {
            when(fornecedorService.reativar(1L)).thenReturn(responseDTO(1L, "Auto Peças LTDA"));

            mockMvc.perform(patch("/api/fornecedores/{id}/reativar", 1L))
                    .andExpect(status().isOk());
        }
    }
}
