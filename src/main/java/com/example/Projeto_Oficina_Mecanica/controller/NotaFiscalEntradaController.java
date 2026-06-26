package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.service.NotaFiscalEntradaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notas-entrada")
@RequiredArgsConstructor
public class NotaFiscalEntradaController {

    private final NotaFiscalEntradaService service;

    @PostMapping
    public ResponseEntity<NotaFiscalEntrada> salvar(
            @RequestBody NotaFiscalEntrada nota) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.salvar(nota));
    }
}
