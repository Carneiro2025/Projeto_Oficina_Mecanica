package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO responsável por retornar os dados de um cliente.
 */
@Data
@Builder
@Schema(description = "Dados de retorno do cliente")
public class ClienteResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "João da Silva")
    private String nome;

    @Schema(example = "Silva Automóveis LTDA")
    private String razaoSocial;

    @Schema(example = "123.456.789-09")
    private String cpfCnpj;

    @Schema(example = "PF")
    private TipoPessoa tipo;

    @Schema(example = "(81) 3333-4444")
    private String telefone;

    @Schema(example = "(81) 99999-8888")
    private String celular;

    @Schema(example = "joao@email.com")
    private String email;

    private EnderecoResponseDTO endereco;

    @Schema(example = "Cliente preferencial.")
    private String observacoes;

    @Schema(example = "true")
    private Boolean ativo;

    @Schema(example = "2026-07-13T10:30:00")
    private LocalDateTime createdAt;

    @Schema(example = "2026-07-13T15:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Quantidade de veículos cadastrados para o cliente", example = "2")
    private Integer quantidadeVeiculos;
}