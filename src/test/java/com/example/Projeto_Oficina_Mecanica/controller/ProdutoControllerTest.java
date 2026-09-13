package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.service.ProdutoService;

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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada web de {@link ProdutoController}.
 */
@WebMvcTest(ProdutoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProdutoController")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    private ProdutoResponseDTO responseDTO(Long id, String codigo) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(id);
        dto.setCodigo(codigo);
        return dto;
    }

    @Nested
    @DisplayName("GET /api/produtos/categorias")
    class ListarCategorias {

        @Test
        @DisplayName("deve retornar 200 com as 13 categorias fixas do enum")
        void deveRetornar200ComCategorias() throws Exception {
            mockMvc.perform(get("/api/produtos/categorias"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(13))
                    .andExpect(jsonPath("$[0].id").value("OLEO_LUBRIFICANTE"))
                    .andExpect(jsonPath("$[0].nome").value("Óleo Lubrificante"));
        }
    }

    @Nested
    @DisplayName("POST /api/produtos")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 quando os dados são válidos")
        void deveRetornar201_quandoDadosValidos() throws Exception {
            CriarProdutoRequestDTO dto = new CriarProdutoRequestDTO();
            dto.setCodigo("PRD001");
            dto.setDescricao("Filtro de óleo");
            dto.setPrecoCusto(new BigDecimal("10.00"));
            dto.setPrecoVenda(new BigDecimal("20.00"));

            when(produtoService.criar(any(CriarProdutoRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "PRD001"));

            mockMvc.perform(post("/api/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.codigo").value("PRD001"));
        }

        @Test
        @DisplayName("deve retornar 422 quando o código já está cadastrado")
        void deveRetornar422_quandoCodigoDuplicado() throws Exception {
            CriarProdutoRequestDTO dto = new CriarProdutoRequestDTO();
            dto.setCodigo("PRD001");

            when(produtoService.criar(any(CriarProdutoRequestDTO.class)))
                    .thenThrow(new BusinessException("Já existe um produto cadastrado com este código."));

            mockMvc.perform(post("/api/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("GET /api/produtos/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 quando o produto existe")
        void deveRetornar200_quandoExiste() throws Exception {
            when(produtoService.buscarPorId(1L)).thenReturn(responseDTO(1L, "PRD001"));

            mockMvc.perform(get("/api/produtos/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.codigo").value("PRD001"));
        }

        @Test
        @DisplayName("deve retornar 404 quando o produto não existe")
        void deveRetornar404_quandoNaoExiste() throws Exception {
            when(produtoService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Produto", 99L));

            mockMvc.perform(get("/api/produtos/{id}", 99L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/produtos")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com a página de produtos")
        void deveRetornar200ComPagina() throws Exception {
            Page<ProdutoResponseDTO> pagina = new PageImpl<>(List.of(responseDTO(1L, "PRD001")));

            when(produtoService.listar(any(), any(), any(), any(), any())).thenReturn(pagina);

            mockMvc.perform(get("/api/produtos").param("descricao", "Filtro"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].codigo").value("PRD001"));
        }
    }

    @Nested
    @DisplayName("PUT /api/produtos/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 quando os dados são válidos")
        void deveRetornar200_quandoDadosValidos() throws Exception {
            AtualizarProdutoRequestDTO dto = new AtualizarProdutoRequestDTO();
            dto.setDescricao("Filtro de óleo premium");

            when(produtoService.atualizar(eq(1L), any(AtualizarProdutoRequestDTO.class)))
                    .thenReturn(responseDTO(1L, "PRD001"));

            mockMvc.perform(put("/api/produtos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.codigo").value("PRD001"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/produtos/{id}/desativar")
    class Desativar {

        @Test
        @DisplayName("deve retornar 204 quando desativado com sucesso")
        void deveRetornar204_quandoDesativadoComSucesso() throws Exception {
            mockMvc.perform(patch("/api/produtos/{id}/desativar", 1L))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("PATCH /api/produtos/{id}/reativar")
    class Reativar {

        @Test
        @DisplayName("deve retornar 200 quando reativado com sucesso")
        void deveRetornar200_quandoReativadoComSucesso() throws Exception {
            when(produtoService.reativar(1L)).thenReturn(responseDTO(1L, "PRD001"));

            mockMvc.perform(patch("/api/produtos/{id}/reativar", 1L))
                    .andExpect(status().isOk());
        }
    }
}
