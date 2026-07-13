package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.service.ContaReceberService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsável pela gestão financeira de Contas a Receber.
 * Controla o ciclo de vida dos Títulos da oficina, desde o mapeamento até a baixa por recebimento.
 */
@RestController
@RequestMapping("/api/contas-receber")
@RequiredArgsConstructor
public class ContaReceberController {

    private final ContaReceberService service;

    /**
     * Retorna a listagem completa de todos os Títulos registrados no sistema.
     */
    @GetMapping
    public ResponseEntity<List<ContaReceber>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    /**
     * Filtra e retorna apenas os Títulos em aberto (pendentes de pagamento).
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaReceber>> pendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }

    /**
     * Realiza a baixa de um Título específico, registrando a liquidação financeira.
     * * @param id Identificador único do Título a receber.
     * @param dataPagamento Data em que o pagamento foi efetivamente recebido (Formato ISO: AAAA-MM-DD).
     */
    @PutMapping("/{id}/pagar")
    public ResponseEntity<ContaReceber> pagar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento
    ) {
        // Registra a liquidação e atualiza o status do Título no banco de dados
        ContaReceber contaAtualizada = service.registrarPagamento(id, dataPagamento);
        return ResponseEntity.ok(contaAtualizada);
    }
}