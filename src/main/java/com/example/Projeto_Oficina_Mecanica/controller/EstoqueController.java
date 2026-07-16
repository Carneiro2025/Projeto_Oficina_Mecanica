package com.example.Projeto_Oficina_Mecanica.controller;


import com.example.Projeto_Oficina_Mecanica.dto.request.CriarMovimentacaoEstoqueRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.MovimentacaoEstoqueResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.EstoqueService;

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
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@Tag(
        name = "Estoque",
        description = "Controle de movimentações e saldo de estoque."
)
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {



    private final EstoqueService estoqueService;



    // ==========================================================
    // REGISTRAR MOVIMENTAÇÃO
    // ==========================================================


    @Operation(
            summary = "Registrar movimentação de estoque",
            description =
                    "Registra entrada, saída, devolução ou ajuste de estoque."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Movimentação registrada."
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado."
            )

    })
    @PostMapping("/movimentar")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> movimentar(

            @Valid
            @RequestBody
            CriarMovimentacaoEstoqueRequestDTO dto

    ) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(

                        estoqueService.movimentar(dto)

                );

    }



    // ==========================================================
    // BUSCAR MOVIMENTAÇÃO POR ID
    // ==========================================================


    @Operation(
            summary = "Buscar movimentação por ID"
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Movimentação encontrada."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Movimentação não encontrada."
            )

    })
    @GetMapping("/movimentacoes/{id}")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> buscarPorId(

            @Parameter(
                    description = "ID da movimentação"
            )
            @PathVariable
            Long id

    ) {


        return ResponseEntity.ok(

                estoqueService.buscarPorId(id)

        );

    }



    // ==========================================================
    // HISTÓRICO POR PRODUTO
    // ==========================================================


    @Operation(
            summary = "Consultar histórico do produto",
            description =
                    "Lista todas as movimentações realizadas para um produto."
    )
    @GetMapping("/produto/{produtoId}/historico")
    public ResponseEntity<Page<MovimentacaoEstoqueResponseDTO>> historicoProduto(

            @PathVariable
            Long produtoId,


            @ParameterObject
            Pageable pageable

    ) {


        return ResponseEntity.ok(

                estoqueService.buscarHistoricoProduto(
                        produtoId,
                        pageable
                )

        );

    }



    // ==========================================================
    // BUSCAR POR TIPO
    // ==========================================================


    @Operation(
            summary = "Buscar movimentações por tipo",
            description =
                    "Consulta movimentações por ENTRADA, SAIDA, DEVOLUCAO ou AJUSTE."
    )
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<MovimentacaoEstoqueResponseDTO>> buscarPorTipo(

            @Parameter(
                    example = "ENTRADA"
            )
            @PathVariable
            String tipo,


            @ParameterObject
            Pageable pageable

    ) {


        return ResponseEntity.ok(

                estoqueService.buscarPorTipo(
                        tipo,
                        pageable
                )

        );

    }


}
