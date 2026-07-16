package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarNotaFiscalEntradaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.NotaFiscalEntradaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.NotaFiscalEntradaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notas-fiscais")
@RequiredArgsConstructor
@Tag(
        name = "Nota Fiscal de Entrada",
        description = "Gerenciamento das Notas Fiscais de Entrada"
)
public class NotaFiscalEntradaController {

    private final NotaFiscalEntradaService service;

    // ==========================================================
    // CADASTRAR
    // ==========================================================

    @PostMapping
    @Operation(summary = "Cadastrar Nota Fiscal de Entrada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota Fiscal cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<NotaFiscalEntradaResponseDTO> criar(
            @Valid @RequestBody CriarNotaFiscalEntradaRequestDTO dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.criar(dto));

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Nota Fiscal por ID")
    public ResponseEntity<NotaFiscalEntradaResponseDTO> buscarPorId(

            @Parameter(description = "ID da Nota Fiscal")
            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );

    }

    // ==========================================================
    // LISTAR
    // ==========================================================

    @GetMapping
    @Operation(summary = "Listar Notas Fiscais")
    public ResponseEntity<Page<NotaFiscalEntradaResponseDTO>> listar(

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.listar(pageable)
        );

    }

    // ==========================================================
    // BUSCAR POR FORNECEDOR
    // ==========================================================

    @GetMapping("/fornecedor/{fornecedorId}")
    @Operation(summary = "Buscar Notas por Fornecedor")
    public ResponseEntity<Page<NotaFiscalEntradaResponseDTO>> buscarFornecedor(

            @PathVariable Long fornecedorId,

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.buscarPorFornecedor(
                        fornecedorId,
                        pageable
                )
        );

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Nota Fiscal")
    public ResponseEntity<NotaFiscalEntradaResponseDTO> atualizar(

            @PathVariable Long id,

            @Valid
            @RequestBody AtualizarNotaFiscalEntradaRequestDTO dto

    ) {

        return ResponseEntity.ok(
                service.atualizar(id, dto)
        );

    }

}