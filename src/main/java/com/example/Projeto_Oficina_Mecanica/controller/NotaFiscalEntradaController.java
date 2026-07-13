package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import com.example.Projeto_Oficina_Mecanica.service.NotaFiscalEntradaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo recebimento e registro de Notas Fiscais de Entrada.
 * Gerencia a entrada oficial de mercadorias, peças e componentes no estoque da oficina.
 */
@RestController
@RequestMapping("/api/notas-entrada")
@RequiredArgsConstructor
public class NotaFiscalEntradaController {

    private final NotaFiscalEntradaService service;

    /**
     * Registra uma nova Nota Fiscal de Entrada no sistema.
     * Além de salvar o documento, este fluxo costuma alimentar o estoque e gerar os Títulos no contas a pagar.
     *
     * @param nota Objeto contendo os dados estruturados da nota fiscal de compra.
     * @return Retorna a nota fiscal cadastrada com o status HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<NotaFiscalEntrada> salvar(@RequestBody NotaFiscalEntrada nota) {
        // Executa o processamento do documento fiscal e gera o registro no banco de dados
        NotaFiscalEntrada novaNota = service.salvar(nota);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(novaNota);
    }
}