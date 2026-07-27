package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarProdutoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ProdutoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto;
import com.example.Projeto_Oficina_Mecanica.service.ProdutoService;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(
        name = "Produtos",
        description = "Gerenciamento de produtos e peças da oficina."
)
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {


    private final ProdutoService produtoService;

    // Rótulos amigáveis para exibição no front-end
    private static final Map<CategoriaProduto, String> ROTULOS_CATEGORIA = new LinkedHashMap<>();
    static {
        ROTULOS_CATEGORIA.put(CategoriaProduto.OLEO_LUBRIFICANTE, "Óleo Lubrificante");
        ROTULOS_CATEGORIA.put(CategoriaProduto.FILTRO, "Filtro");
        ROTULOS_CATEGORIA.put(CategoriaProduto.PECA_MOTOR, "Peça de Motor");
        ROTULOS_CATEGORIA.put(CategoriaProduto.PECA_FREIO, "Peça de Freio");
        ROTULOS_CATEGORIA.put(CategoriaProduto.PECA_SUSPENSAO, "Peça de Suspensão");
        ROTULOS_CATEGORIA.put(CategoriaProduto.PECA_ELETRICA, "Peça Elétrica");
        ROTULOS_CATEGORIA.put(CategoriaProduto.PECA_TRANSMISSAO, "Peça de Transmissão");
        ROTULOS_CATEGORIA.put(CategoriaProduto.PNEU, "Pneu");
        ROTULOS_CATEGORIA.put(CategoriaProduto.BATERIA, "Bateria");
        ROTULOS_CATEGORIA.put(CategoriaProduto.FLUIDO, "Fluido");
        ROTULOS_CATEGORIA.put(CategoriaProduto.ACESSORIO, "Acessório");
        ROTULOS_CATEGORIA.put(CategoriaProduto.FERRAMENTA, "Ferramenta");
        ROTULOS_CATEGORIA.put(CategoriaProduto.OUTROS, "Outros");
    }

    /**
     * Lista as categorias de produto disponíveis, usada para popular
     * selects no front-end (tela de produtos e modal de cadastro).
     */
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias de produto")
    public ResponseEntity<List<Map<String, String>>> listarCategorias() {

        List<Map<String, String>> categorias = ROTULOS_CATEGORIA.entrySet()
                .stream()
                .map(entry -> {
                    Map<String, String> item = new LinkedHashMap<>();
                    item.put("id", entry.getKey().name());
                    item.put("nome", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(categorias);
    }



    // ==========================================================
    // CRIAR PRODUTO
    // ==========================================================

    @Operation(
            summary = "Cadastrar produto",
            description = "Realiza o cadastro de um novo produto."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Produto cadastrado com sucesso."
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos."
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Produto já cadastrado."
            )

    })
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(

            @Valid
            @RequestBody
            CriarProdutoRequestDTO dto

    ) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        produtoService.criar(dto)
                );

    }



    // ==========================================================
    // LISTAR PRODUTOS
    // ==========================================================

    @Operation(
            summary = "Listar produtos",
            description = "Lista produtos com filtros e paginação."
    )
    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(

            @RequestParam(required = false)
            String descricao,


            @RequestParam(required = false)
            String codigo,


            @RequestParam(required = false)
            CategoriaProduto categoria,


            @RequestParam(required = false)
            Long fornecedorId,


            @ParameterObject
            Pageable pageable

    ) {


        return ResponseEntity.ok(

                produtoService.listar(
                        descricao,
                        codigo,
                        categoria,
                        fornecedorId,
                        pageable
                )

        );

    }



    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @Operation(
            summary = "Buscar produto por ID"
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado."
            )

    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(

            @Parameter(
                    description = "ID do produto"
            )
            @PathVariable
            Long id

    ) {


        return ResponseEntity.ok(

                produtoService.buscarPorId(id)

        );

    }



    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @Operation(
            summary = "Atualizar produto"
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado."
            )

    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(

            @PathVariable
            Long id,


            @Valid
            @RequestBody
            AtualizarProdutoRequestDTO dto

    ) {


        return ResponseEntity.ok(

                produtoService.atualizar(
                        id,
                        dto
                )

        );

    }

    
    // ==========================================================
    // DESATIVAR PRODUTO
    // ==========================================================

    @Operation(
            summary = "Desativar produto",
            description = "Realiza a desativação lógica do produto."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description = "Produto desativado com sucesso."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado."
            )

    })
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(

            @PathVariable
            Long id

    ) {


        produtoService.desativar(id);


        return ResponseEntity
                .noContent()
                .build();

    }



    // ==========================================================
    // REATIVAR PRODUTO
    // ==========================================================

    @Operation(
            summary = "Reativar produto",
            description = "Reativa um produto previamente desativado."
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Produto reativado com sucesso."
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado."
            )

    })
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ProdutoResponseDTO> reativar(

            @PathVariable
            Long id

    ) {


        return ResponseEntity.ok(

                produtoService.reativar(id)

        );

    }

}