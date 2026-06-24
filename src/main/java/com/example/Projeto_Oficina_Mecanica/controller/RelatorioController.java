package com.example.Projeto_Oficina_Mecanica.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    @GetMapping("/financeiro")
    public Object financeiro() {
        return null;
    }

    @GetMapping("/estoque")
    public Object estoque() {
        return null;
    }

    @GetMapping("/ordens-servico")
    public Object ordensServico() {
        return null;
    }

    @GetMapping("/clientes")
    public Object clientes() {
        return null;
    }
}


