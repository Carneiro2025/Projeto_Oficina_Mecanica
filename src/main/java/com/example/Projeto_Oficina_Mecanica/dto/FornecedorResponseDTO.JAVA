package com.example.Projeto_Oficina_Mecanica.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FornecedorResponseDTO {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String telefone;
    private String celular;
    private String email;
    private String site;
    private String nomeContato;
    private EnderecoResponseDTO endereco;
    private String observacoes;
    private Boolean ativo;
    private Integer totalProdutos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}