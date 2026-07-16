package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@Tag(
        name = "Veículos",
        description = "Gerenciamento de veículos da oficina."
)
@SecurityRequirement(name = "bearerAuth")
public class VeiculoController {

    private final VeiculoService veiculoService;

    // ==========================================================
    // CADASTRAR
    // ==========================================================

    @Operation(
            summary = "Cadastrar veículo",
            description = "Realiza o cadastro de um novo veículo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada.")
    })
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> criar(

            @RequestBody
            @Valid
            CriarVeiculoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(veiculoService.criar(dto));

    }

    // ==========================================================
    // LISTAR
    // ==========================================================

    @Operation(
            summary = "Listar veículos",
            description = "Lista veículos utilizando filtros e paginação."
    )
    @GetMapping
    public ResponseEntity<Page<VeiculoResponseDTO>> listar(

            @RequestParam(required = false)
            String placa,

            @RequestParam(required = false)
            String modelo,

            @RequestParam(required = false)
            Long clienteId,

            @RequestParam(required = false)
            Boolean ativo,

            @ParameterObject
            Pageable pageable) {

        return ResponseEntity.ok(

                veiculoService.listar(
                        placa,
                        modelo,
                        clienteId,
                        ativo,
                        pageable)

        );

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @Operation(
            summary = "Buscar veículo por ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículo encontrado."),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarPorId(

            @Parameter(description = "ID do veículo")

            @PathVariable
            Long id) {

        return ResponseEntity.ok(

                veiculoService.buscarPorId(id)

        );

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @Operation(
            summary = "Atualizar veículo",
            description = "Atualiza os dados cadastrais de um veículo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado."),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizar(

            @Parameter(description = "ID do veículo")
            @PathVariable
            Long id,

            @RequestBody
            @Valid
            AtualizarVeiculoRequestDTO dto) {

        return ResponseEntity.ok(

                veiculoService.atualizar(id, dto)

        );

    }

    // ==========================================================
    // DESATIVAR
    // ==========================================================

    @Operation(
            summary = "Desativar veículo",
            description = "Realiza a exclusão lógica (soft delete) do veículo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Veículo desativado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado."),
            @ApiResponse(responseCode = "422", description = "Veículo já está inativo.")
    })
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(

            @Parameter(description = "ID do veículo")
            @PathVariable
            Long id) {

        veiculoService.desativar(id);

        return ResponseEntity.noContent().build();

    }

    // ==========================================================
    // REATIVAR
    // ==========================================================

    @Operation(
            summary = "Reativar veículo",
            description = "Reativa um veículo anteriormente desativado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículo reativado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado."),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada.")
    })
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<VeiculoResponseDTO> reativar(

            @Parameter(description = "ID do veículo")
            @PathVariable
            Long id) {

        return ResponseEntity.ok(

                veiculoService.reativar(id)

        );

    }

}