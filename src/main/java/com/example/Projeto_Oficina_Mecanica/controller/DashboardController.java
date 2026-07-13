package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.DashboardDTO;
import com.example.Projeto_Oficina_Mecanica.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por consolidar e fornecer os dados analíticos do sistema.
 * Alimenta a interface gráfica com os principais indicadores e métricas da oficina mecânica.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para consulta de indicadores gerais da oficina mecânica")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Recupera o compilado de métricas do sistema.
     * Consolida dados de Títulos, ordens de serviço e faturamento para exibição gerencial.
     */
    @GetMapping
    @Operation(
            summary = "Obter indicadores do Dashboard",
            description = "Retorna um objeto consolidado (DTO) contendo os principais KPIs e índices financeiros do sistema."
    )
    public ResponseEntity<DashboardDTO> obterDashboard() {
        // Busca os dados consolidados na camada de serviço e envelopa na resposta HTTP 200 (OK)
        DashboardDTO dadosDashboard = dashboardService.obterDashboard();
        return ResponseEntity.ok(dadosDashboard);
    }
}
