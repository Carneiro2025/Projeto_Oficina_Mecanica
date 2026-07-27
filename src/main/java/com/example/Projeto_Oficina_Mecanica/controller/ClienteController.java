package com.example.Projeto_Oficina_Mecanica.controller;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import com.example.Projeto_Oficina_Mecanica.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(
        name = "Clientes",
        description = "Gerenciamento de clientes da oficina."
)
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Cadastro de Cliente
     */
    @PostMapping
    @Operation(summary = "Cadastrar Cliente")
    public ResponseEntity<ClienteResponseDTO> criar(

            @RequestBody
            @Valid
            CriarClienteRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(dto));
    }

    /**
     * Listagem paginada
     */
    @GetMapping
    @Operation(summary = "Listar Clientes")
    public ResponseEntity<Page<ClienteResponseDTO>> listar(

            @RequestParam(required = false)
            String nome,

            @RequestParam(required = false)
            String cpfCnpj,

            @RequestParam(required = false)
            Boolean ativo,

            @PageableDefault(size = 10)
            Pageable pageable) {

        return ResponseEntity.ok(

                clienteService.listar(
                        nome,
                        cpfCnpj,
                        ativo,
                        pageable
                )
        );
    }

    /**
     * Buscar por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Cliente por ID")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(

            @Parameter(description = "ID do Cliente")

            @PathVariable
            Long id) {

        return ResponseEntity.ok(

                clienteService.buscarPorId(id)
        );
    }

    /**
     * Atualizar
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Cliente")
    public ResponseEntity<ClienteResponseDTO> atualizar(

            @PathVariable
            Long id,

            @RequestBody
            @Valid
            AtualizarClienteRequestDTO dto) {

        return ResponseEntity.ok(

                clienteService.atualizar(id, dto)
        );
    }

    /**
     * Desativar
     */
    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar Cliente")
    public ResponseEntity<Void> desativar(

            @PathVariable
            Long id) {

        clienteService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Reativar
     */
    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar Cliente")
    public ResponseEntity<ClienteResponseDTO> reativar(

            @PathVariable
            Long id) {

        return ResponseEntity.ok(

                clienteService.reativar(id)
        );
    }

}
