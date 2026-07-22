package com.example.Projeto_Oficina_Mecanica.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Projeto_Oficina_Mecanica.dto.RelatorioClienteDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioEstoqueDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioFinanceiroDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioOSDTO;
import com.example.Projeto_Oficina_Mecanica.service.RelatorioService;

import java.util.List;

/**
 * Controller responsável pela geração de relatórios gerenciais e inteligência de negócio.
 * Centraliza os endpoints que extraem dados consolidados e históricos para auditoria e tomada de decisão.
 */
@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {


    private final RelatorioService relatorioService;

    /**
     * Extrai o relatório de movimentação financeira da oficina.
     * Consolida dados de Títulos pagos, Títulos recebidos e fluxo de caixa geral.
     */
    @GetMapping("/financeiro")
    public ResponseEntity<RelatorioFinanceiroDTO> financeiro() {

    return ResponseEntity.ok(
            relatorioService.relatorioFinanceiro()
    );
    }

    /**
     * Extrai o relatório de posição e curva de estoque.
     * Apresenta o saldo de peças, componentes disponíveis, valores imobilizados e alertas de reposição.
     */
    @GetMapping("/estoque")
    public ResponseEntity<List<RelatorioEstoqueDTO>> estoque() {

    return ResponseEntity.ok(
            relatorioService.relatorioEstoque()
    );
    }

    /**
     * Extrai o relatório analítico de Ordens de Serviço.
     * Consolida métricas de OS abertas, finalizadas, canceladas e o ticket médio dos reparos.
     */
    @GetMapping("/ordens-servico")
    public ResponseEntity<List<RelatorioOSDTO>> ordensServico() {

    return ResponseEntity.ok(
            relatorioService.relatorioOrdensServico()
    );
    }

    /**
     * Extrai o relatório de comportamento e cadastro de clientes.
     * Mapeia a frequência de visitas por veículo, faturamento por cliente e histórico de revisões.
     */
    @GetMapping("/clientes")
    public ResponseEntity<List<RelatorioClienteDTO>> clientes() {

    return ResponseEntity.ok(
            relatorioService.relatorioClientes()
    );
    }
}