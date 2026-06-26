package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.ContasPagar;
import com.example.Projeto_Oficina_Mecanica.service.ContasPagarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contas-pagar")
@RequiredArgsConstructor
public class ContasPagarController {

    private final ContasPagarService service;

    @GetMapping
    public ResponseEntity<List<ContasPagar>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ContasPagar>> pendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<ContasPagar> pagar(
            @PathVariable Long id,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataPagamento) {

        return ResponseEntity.ok(
                service.registrarPagamento(id, dataPagamento)
        );
    }
}