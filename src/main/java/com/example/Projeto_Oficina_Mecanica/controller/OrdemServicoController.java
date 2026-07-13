package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AdicionarItemOSRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.OrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelo gerenciamento do ciclo de vida das Ordens de Serviço (OS).
 * Concentra o fluxo operacional da oficina, integrando clientes, veículos, serviços e peças.
 */
@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    /**
     * Registra a abertura de uma nova Ordem de Serviço.
     *
     * @param dto Dados iniciais da OS (Cliente, Veículo, Relato do cliente).
     * @return Retorna a OS criada com status HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> abrir(@RequestBody OrdemServicoRequestDTO dto) {
        OrdemServicoResponseDTO novaOs = ordemServicoService.abrir(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaOs);
    }

    /**
     * Realiza a busca detalhada de uma Ordem de Serviço específica.
     *
     * @param id Identificador único da OS.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        OrdemServicoResponseDTO os = ordemServicoService.buscarPorId(id);
        return ResponseEntity.ok(os);
    }

    /**
     * Lista todas as Ordens de Serviço cadastradas na oficina.
     */
    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> listar() {
        List<OrdemServicoResponseDTO> listaOs = ordemServicoService.listar();
        return ResponseEntity.ok(listaOs);
    }

    /**
     * Finaliza a Ordem de Serviço, indicando a conclusão dos reparos.
     * Este gatilho normalmente encerra o fluxo operacional e gera os Títulos de recebimento.
     *
     * @param id Identificador único da OS.
     */
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizar(@PathVariable Long id) {
        OrdemServicoResponseDTO osFinalizada = ordemServicoService.finalizar(id);
        return ResponseEntity.ok(osFinalizada);
    }

    /**
     * Cancela uma Ordem de Serviço antes de sua conclusão.
     *
     * @param id Identificador único da OS.
     * @return Retorna o status HTTP 24 (No Content).
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        ordemServicoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vincula itens (peças, componentes ou serviços manuais) à Ordem de Serviço.
     *
     * @param id  Identificador único da OS.
     * @param dto Informações do item (código do produto/serviço, quantidade, valor unitário).
     */
    @PostMapping("/{id}/itens")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarItem(
            @PathVariable Long id,
            @RequestBody AdicionarItemOSRequestDTO dto
    ) {
        OrdemServicoResponseDTO osAtualizada = ordemServicoService.addItem(id, dto);
        return ResponseEntity.ok(osAtualizada);
    }
}