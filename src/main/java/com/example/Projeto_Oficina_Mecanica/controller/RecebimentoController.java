package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.RecebimentoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.RecebimentoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.RecebimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelo controle financeiro dos recebimentos.
 */
@RestController
@RequestMapping("/api/recebimentos")
@RequiredArgsConstructor
@Tag(
        name = "💰 Recebimentos",
        description = "Gerenciamento dos recebimentos da oficina."
)
public class RecebimentoController {

    private final RecebimentoService recebimentoService;

    @PostMapping("/pagar")
    @Operation(
            summary = "Registrar pagamento",
            description = "Efetua o pagamento de uma Ordem de Serviço."
    )
    @ApiResponse(responseCode = "200", description = "Pagamento registrado com sucesso.")
    public ResponseEntity<RecebimentoResponseDTO> registrarPagamento(
            @RequestBody @Valid RecebimentoRequestDTO dto) {

        return ResponseEntity.ok(
                recebimentoService.registrarPagamento(dto)
        );
    }

    @GetMapping
    @Operation(summary = "Listar todos os recebimentos")
    public ResponseEntity<List<RecebimentoResponseDTO>> listar() {

        return ResponseEntity.ok(
                recebimentoService.listar()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar recebimento por ID")
    public ResponseEntity<RecebimentoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                recebimentoService.buscarPorId(id)
        );
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Listar recebimentos pendentes")
    public ResponseEntity<List<RecebimentoResponseDTO>> listarPendentes() {

        return ResponseEntity.ok(
                recebimentoService.listarPendentes()
        );
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar recebimentos por cliente")
    public ResponseEntity<List<RecebimentoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                recebimentoService.listarPorCliente(clienteId)
        );
    }

}
