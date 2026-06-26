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

@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    /**
     * Abrir nova OS
     */
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> abrir(
            @RequestBody OrdemServicoRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordemServicoService.abrir(dto));
    }

    /**
     * Buscar OS por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ordemServicoService.buscarPorId(id)
        );
    }

    /**
     * Listar todas as OS
     */
    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> listar() {

        return ResponseEntity.ok(
                ordemServicoService.listar()
        );
    }

    /**
     * Finalizar OS
     */
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ordemServicoService.finalizar(id)
        );
    }

    /**
     * Cancelar OS
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id) {

        ordemServicoService.cancelar(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Adicionar produto ou serviço na OS
     */
    @PostMapping("/{id}/itens")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarItem(
            @PathVariable Long id,
            @RequestBody AdicionarItemOSRequestDTO dto) {

        return ResponseEntity.ok(
                ordemServicoService.adicionarItem(id, dto)
        );
    }
}

