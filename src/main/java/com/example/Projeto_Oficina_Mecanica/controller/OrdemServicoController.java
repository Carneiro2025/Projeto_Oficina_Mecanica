package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarOrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import com.example.Projeto_Oficina_Mecanica.service.OrdemServicoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
@Tag(
        name = "Ordens de Serviço",
        description = "Gerenciamento das Ordens de Serviço"
)
public class OrdemServicoController {

    private final OrdemServicoService service;

    // ==========================================================
    // CADASTRAR
    // ==========================================================

    @PostMapping
    @Operation(summary = "Abrir Ordem de Serviço")
    public ResponseEntity<OrdemServicoResponseDTO> criar(

            @Valid
            @RequestBody CriarOrdemServicoRequestDTO dto

    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(dto));

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Ordem de Serviço por ID")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(

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
    @Operation(summary = "Listar Ordens de Serviço")
    public ResponseEntity<Page<OrdemServicoResponseDTO>> listar(

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.listar(pageable)
        );

    }

    // ==========================================================
    // BUSCAR POR CLIENTE
    // ==========================================================

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar Ordens por Cliente")
    public ResponseEntity<Page<OrdemServicoResponseDTO>> buscarCliente(

            @PathVariable Long clienteId,

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.buscarPorCliente(
                        clienteId,
                        pageable
                )
        );

    }

    // ==========================================================
    // BUSCAR POR VEÍCULO
    // ==========================================================

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Buscar Ordens por Veículo")
    public ResponseEntity<Page<OrdemServicoResponseDTO>> buscarVeiculo(

            @PathVariable Long veiculoId,

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.buscarPorVeiculo(
                        veiculoId,
                        pageable
                )
        );

    }

    // ==========================================================
    // BUSCAR POR STATUS
    // ==========================================================

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar Ordens por Status")
    public ResponseEntity<Page<OrdemServicoResponseDTO>> buscarStatus(

            @PathVariable StatusOrdemServico status,

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.buscarPorStatus(
                        status,
                        pageable
                )
        );

    }

    // ==========================================================
    // FILTROS
    // ==========================================================

    @GetMapping("/filtros")
    @Operation(summary = "Pesquisar Ordens de Serviço")
    public ResponseEntity<Page<OrdemServicoResponseDTO>> filtros(

            @RequestParam(required = false)
            String numero,

            @RequestParam(required = false)
            StatusOrdemServico status,

            @RequestParam(required = false)
            Long clienteId,

            @RequestParam(required = false)
            Long veiculoId,

            @RequestParam(required = false)
            LocalDate dataInicial,

            @RequestParam(required = false)
            LocalDate dataFinal,

            @PageableDefault(size = 10)
            Pageable pageable

    ) {

        return ResponseEntity.ok(

                service.buscarComFiltros(

                        numero,

                        status,

                        clienteId,

                        veiculoId,

                        dataInicial,

                        dataFinal,

                        pageable

                )

        );

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Ordem de Serviço")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(

            @PathVariable Long id,

            @Valid
            @RequestBody AtualizarOrdemServicoRequestDTO dto

    ) {

        return ResponseEntity.ok(
                service.atualizar(id, dto)
        );

    }

    // ==========================================================
    // FINALIZAR
    // ==========================================================

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar Ordem de Serviço")
    public ResponseEntity<OrdemServicoResponseDTO> finalizar(

            @PathVariable Long id

    ) {

        return ResponseEntity.ok(
                service.finalizar(id)
        );

    }

    // ==========================================================
    // CANCELAR
    // ==========================================================

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar Ordem de Serviço")
    public ResponseEntity<Void> cancelar(

            @PathVariable Long id

    ) {

        service.cancelar(id);

        return ResponseEntity.noContent().build();

    }

}
