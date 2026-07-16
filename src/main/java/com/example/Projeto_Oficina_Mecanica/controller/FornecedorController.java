package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
@Tag(
        name = "Fornecedores",
        description = "Operações de gerenciamento de fornecedores."
)
@SecurityRequirement(name = "bearerAuth")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    // ==========================================================
    // CADASTRAR
    // ==========================================================

    @Operation(
            summary = "Cadastrar fornecedor",
            description = "Realiza o cadastro de um novo fornecedor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fornecedor cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "409", description = "Fornecedor já cadastrado.")
    })
    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> criar(
            @Valid @RequestBody CriarFornecedorRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fornecedorService.criar(dto));
    }

    // ==========================================================
    // LISTAR
    // ==========================================================

    @Operation(
            summary = "Listar fornecedores",
            description = "Lista fornecedores utilizando filtros e paginação."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    @GetMapping
    public ResponseEntity<Page<FornecedorResponseDTO>> listar(

            @RequestParam(required = false)
            String razaoSocial,

            @RequestParam(required = false)
            String cnpj,

            @RequestParam(required = false)
            String cidade,

            @ParameterObject
            Pageable pageable) {

        return ResponseEntity.ok(

                fornecedorService.listar(
                        razaoSocial,
                        cnpj,
                        cidade,
                        pageable)

        );

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @Operation(
            summary = "Buscar fornecedor por ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fornecedor encontrado."),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> buscarPorId(

            @Parameter(description = "ID do fornecedor")
            @PathVariable Long id) {

        return ResponseEntity.ok(

                fornecedorService.buscarPorId(id)

        );

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @Operation(
            summary = "Atualizar fornecedor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fornecedor atualizado."),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado."),
            @ApiResponse(responseCode = "409", description = "Conflito de dados.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> atualizar(

            @PathVariable Long id,

            @Valid
            @RequestBody
            AtualizarFornecedorRequestDTO dto) {

        return ResponseEntity.ok(

                fornecedorService.atualizar(id, dto)

        );

    }

    // ==========================================================
    // DESATIVAR
    // ==========================================================

    @Operation(
            summary = "Desativar fornecedor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fornecedor desativado."),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado.")
    })
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(

            @PathVariable Long id) {

        fornecedorService.desativar(id);

        return ResponseEntity.noContent().build();

    }

    // ==========================================================
    // REATIVAR
    // ==========================================================

    @Operation(
            summary = "Reativar fornecedor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fornecedor reativado."),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado.")
    })
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<FornecedorResponseDTO> reativar(

            @PathVariable Long id) {

        return ResponseEntity.ok(

                fornecedorService.reativar(id)

        );

    }

}

