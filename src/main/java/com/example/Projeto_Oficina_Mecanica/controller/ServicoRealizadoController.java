package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.entity.ServicoRealizado;
import com.example.Projeto_Oficina_Mecanica.service.ServicoRealizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelo registro e controle de Serviços Realizados.
 * Gerencia o histórico de mão de obra e as operações técnicas executadas nos veículos pela oficina.
 */
@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoRealizadoController {

    private final ServicoRealizadoService service;

    /**
     * Lista todos os serviços registrados no sistema.
     */
    @GetMapping
    public ResponseEntity<List<ServicoRealizado>> listar() {
        List<ServicoRealizado> servicos = service.listarTodos();
        return ResponseEntity.ok(servicos);
    }

    /**
     * Busca um serviço realizado específico utilizando o identificador único.
     *
     * @param id Identificador único do serviço.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicoRealizado> buscar(@PathVariable Long id) {
        ServicoRealizado servico = service.buscarPorId(id);
        return ResponseEntity.ok(servico);
    }

    /**
     * Registra um novo serviço realizado na base de dados.
     *
     * @param servico Objeto contendo os dados do serviço executado (descrição, tempo, valor da mão de obra).
     */
    @PostMapping
    public ResponseEntity<ServicoRealizado> criar(@RequestBody ServicoRealizado servico) {
        ServicoRealizado novoServico = service.salvar(servico);
        return ResponseEntity.ok(novoServico);
    }

    /**
     * Remove o registro de um serviço realizado do sistema.
     *
     * @param id Identificador único do serviço a ser removido.
     * @return Retorna o status HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}