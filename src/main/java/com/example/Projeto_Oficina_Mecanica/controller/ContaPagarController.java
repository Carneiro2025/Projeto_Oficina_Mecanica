package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarContaPagarRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ContaPagarResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.service.ContaPagarService;

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

import java.util.List;

@RestController
@RequestMapping("/api/contas-pagar")
@RequiredArgsConstructor
@Tag(
        name = "Contas a Pagar",
        description = "Gerenciamento das obrigações financeiras da empresa"
)
public class ContaPagarController {

    private final ContaPagarService service;

    // ==========================================================
    // CADASTRAR
    // ==========================================================

    @PostMapping
    @Operation(summary = "Cadastrar conta a pagar")
    public ResponseEntity<ContaPagarResponseDTO> criar(

            @Valid
            @RequestBody CriarContaPagarRequestDTO dto

    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(dto));

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID")
    public ResponseEntity<ContaPagarResponseDTO> buscarPorId(

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
    @Operation(summary = "Listar contas a pagar")
    public ResponseEntity<Page<ContaPagarResponseDTO>> listar(

            @PageableDefault(size = 20)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.listar(pageable)
        );

    }

    // ==========================================================
    // BUSCAR POR STATUS
    // ==========================================================

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar contas por status")
    public ResponseEntity<List<ContaPagarResponseDTO>> buscarPorStatus(

            @PathVariable StatusContaPagar status

    ) {

        return ResponseEntity.ok(
                service.buscarPorStatus(status)
        );

    }

    // ==========================================================
    // LISTAR PENDENTES
    // ==========================================================

    @GetMapping("/pendentes")
    @Operation(summary = "Listar contas pendentes")
    public ResponseEntity<List<ContaPagarResponseDTO>> listarPendentes() {

        return ResponseEntity.ok(
                service.listarPendentes()
        );

    }

    // ==========================================================
    // BUSCAR POR FORNECEDOR
    // ==========================================================

    @GetMapping("/fornecedor/{fornecedorId}")
    @Operation(summary = "Buscar contas por fornecedor")
    public ResponseEntity<List<ContaPagarResponseDTO>> buscarPorFornecedor(

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
    @Operation(summary = "Atualizar conta a pagar")
    public ResponseEntity<ContaPagarResponseDTO> atualizar(

            @PathVariable Long id,

            @Valid
            @RequestBody AtualizarContaPagarRequestDTO dto

    ) {

        return ResponseEntity.ok(
                service.atualizar(id, dto)
        );

    }

    // ==========================================================
    // REGISTRAR PAGAMENTO
    // ==========================================================

    @PatchMapping("/{id}/pagamento")
    @Operation(summary = "Registrar pagamento da conta")
    public ResponseEntity<ContaPagarResponseDTO> registrarPagamento(

            @PathVariable Long id,

            @Valid
            @RequestBody AtualizarContaPagarRequestDTO dto

    ) {

        return ResponseEntity.ok(
                service.registrarPagamento(id, dto)
        );

    }

    // ==========================================================
    // EXCLUIR
    // ==========================================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta a pagar")
    public ResponseEntity<Void> excluir(

            @PathVariable Long id

    ) {

        service.excluir(id);

        return ResponseEntity.noContent().build();

    }

}