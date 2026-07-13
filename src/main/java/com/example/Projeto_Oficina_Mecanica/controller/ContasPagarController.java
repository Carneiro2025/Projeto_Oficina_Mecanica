package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.ContasPagar;
import com.example.Projeto_Oficina_Mecanica.service.ContasPagarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsável pela gestão financeira de Contas a Pagar.
 * Centraliza as operações de controle de Títulos emitidos e pagamento de fornecedores.
 */
@RestController
@RequestMapping("/api/contas-pagar")
@RequiredArgsConstructor
public class ContasPagarController {

    private final ContasPagarService service;

    /**
     * Retorna a listagem completa de todos os Títulos de contas a pagar cadastrados.
     */
    @GetMapping
    public ResponseEntity<List<ContasPagar>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    /**
     * Filtra e retorna apenas os Títulos em aberto que aguardam pagamento de fornecedores.
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<ContasPagar>> pendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }

    /**
     * Realiza a baixa de um Título de despesa, efetuando o pagamento do fornecedor.
     *
     * @param id Identificador único do Título a pagar.
     * @param dataPagamento Data em que o pagamento foi efetivamente realizado (Formato ISO: AAAA-MM-DD).
     */
    @PutMapping("/{id}/pagar")
    public ResponseEntity<ContasPagar> pagar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento
    ) {
        // Executa a regra de negócio para a baixa do Título e retorna o registro atualizado
        ContasPagar contaPaga = service.registrarPagamento(id, dataPagamento);
        return ResponseEntity.ok(contaPaga);
    }
}