package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.service.ContaReceberService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contas-receber")
@RequiredArgsConstructor
public class ContaReceberController {

    private final ContaReceberService service;

    @GetMapping
    public ResponseEntity<List<ContaReceber>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaReceber>> pendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<ContaReceber> pagar(
            @PathVariable Long id,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataPagamento) {

        return ResponseEntity.ok(
                service.registrarPagamento(
                        id,
                        dataPagamento
                )
        );
    }
}

