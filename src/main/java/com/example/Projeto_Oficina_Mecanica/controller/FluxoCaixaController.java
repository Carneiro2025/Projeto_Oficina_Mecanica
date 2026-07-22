package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFluxoCaixaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFluxoCaixaRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FluxoCaixaResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.service.FluxoCaixaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fluxo-caixa")
@RequiredArgsConstructor
@Tag(
        name = "Fluxo de Caixa",
        description = "Gerenciamento das movimentações financeiras do sistema"
)
public class FluxoCaixaController {

    private final FluxoCaixaService service;

    // ==========================================================
    // CADASTRAR MOVIMENTAÇÃO
    // ==========================================================

    @PostMapping
    @Operation(summary = "Cadastrar movimentação financeira")
    public ResponseEntity<FluxoCaixaResponseDTO> criar(

            @Valid
            @RequestBody CriarFluxoCaixaRequestDTO dto

    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(dto));

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação pelo ID")
    public ResponseEntity<FluxoCaixaResponseDTO> buscarPorId(

            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );

    }

    // ==========================================================
    // LISTAR TODAS AS MOVIMENTAÇÕES
    // ==========================================================

    @GetMapping
    @Operation(summary = "Listar movimentações financeiras")
    public ResponseEntity<Page<FluxoCaixaResponseDTO>> listar(

            @PageableDefault(size = 20)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.listar(pageable)
        );

    }

    // ==========================================================
    // BUSCAR POR TIPO
    // ==========================================================

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar movimentações por tipo")
    public ResponseEntity<List<FluxoCaixaResponseDTO>> buscarPorTipo(

            @PathVariable TipoMovimentacaoCaixa tipo

    ) {

        return ResponseEntity.ok(
                service.buscarPorTipo(tipo)
        );

    }

    // ==========================================================
    // BUSCAR POR PERÍODO
    // ==========================================================

    @GetMapping("/periodo")
    @Operation(summary = "Buscar movimentações por período")
    public ResponseEntity<List<FluxoCaixaResponseDTO>> buscarPorPeriodo(

            @RequestParam LocalDate inicio,

            @RequestParam LocalDate fim

    ) {

        return ResponseEntity.ok(
                service.buscarPorPeriodo(inicio, fim)
        );

    }

    // ==========================================================
    // BUSCAR POR CLIENTE
    // ==========================================================

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar movimentações por cliente")
    public ResponseEntity<List<FluxoCaixaResponseDTO>> buscarPorCliente(

            @PathVariable Long clienteId

    ) {

        return ResponseEntity.ok(
                service.buscarPorCliente(clienteId)
        );

    }

    // ==========================================================
    // BUSCAR POR FORNECEDOR
    // ==========================================================

    @GetMapping("/fornecedor/{fornecedorId}")
    @Operation(summary = "Buscar movimentações por fornecedor")
    public ResponseEntity<List<FluxoCaixaResponseDTO>> buscarPorFornecedor(

            @PathVariable Long fornecedorId

    ) {

        return ResponseEntity.ok(
                service.buscarPorFornecedor(fornecedorId)
        );

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar movimentação financeira")
    public ResponseEntity<FluxoCaixaResponseDTO> atualizar(

            @PathVariable Long id,

            @Valid
            @RequestBody AtualizarFluxoCaixaRequestDTO dto

    ) {

        return ResponseEntity.ok(
                service.atualizar(id, dto)
        );

    }

    // ==========================================================
    // EXCLUIR
    // ==========================================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir movimentação financeira")
    public ResponseEntity<Void> excluir(

            @PathVariable Long id

    ) {

        service.excluir(id);

        return ResponseEntity.noContent().build();

    }

}