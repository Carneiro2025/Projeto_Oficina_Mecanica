package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Item leve para a lista de "produtos com estoque baixo" do dashboard.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProdutoEstoqueBaixoDTO {

    private Long id;

    private String codigo;

    private String descricao;

    private Integer estoqueAtual;

    private Integer estoqueMinimo;
}
