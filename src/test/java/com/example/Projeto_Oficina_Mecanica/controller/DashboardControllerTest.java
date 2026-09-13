package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.DashboardDTO;
import com.example.Projeto_Oficina_Mecanica.service.DashboardService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração da camada web de {@link DashboardController}.
 */
@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DashboardController")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    @DisplayName("GET /api/dashboard deve retornar 200 com os indicadores consolidados")
    void deveRetornar200ComIndicadores() throws Exception {
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalClientes(10L);
        dto.setTotalVeiculos(15L);
        dto.setTotalProdutos(50L);
        dto.setOrdensAbertas(4L);
        dto.setOrdensFinalizadas(20L);
        dto.setOrdensCanceladas(2L);
        dto.setReceitaMes(new java.math.BigDecimal("500.00"));
        dto.setLucroMes(new java.math.BigDecimal("300.00"));
        dto.setTotalProdutosEstoqueBaixo(3L);

        when(dashboardService.obterDashboard()).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClientes").value(10))
                .andExpect(jsonPath("$.ordensAbertas").value(4))
                .andExpect(jsonPath("$.receitaMes").value(500.00))
                .andExpect(jsonPath("$.lucroMes").value(300.00))
                .andExpect(jsonPath("$.totalProdutosEstoqueBaixo").value(3));
    }
}
