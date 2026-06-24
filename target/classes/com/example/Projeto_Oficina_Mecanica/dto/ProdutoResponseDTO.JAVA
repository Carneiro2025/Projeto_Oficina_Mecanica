package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.entity.CategoriaProduto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProdutoResponseDTO {
    private Long id;
    private String codigo;
    private String descricao;
    private CategoriaProduto categoria;
    private String marca;
    private String unidade;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private BigDecimal margemLucroPercent;
    private Integer estoqueAtual;
    private Integer estoqueMinimo;
    private Integer estoqueMaximo;
    private Boolean estoqueAbaixoMinimo;
    private String localizacao;
    private String observacoes;
    private Boolean ativo;
    // Dados do fornecedor vinculado
    private Long fornecedorId;
    private String fornecedorRazaoSocial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}