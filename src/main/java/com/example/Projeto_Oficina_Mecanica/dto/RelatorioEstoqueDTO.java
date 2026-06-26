package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioEstoqueDTO {

    private String codigoProduto;

    private String descricao;

    private Integer estoqueAtual;

    private Integer estoqueMinimo;

    private Boolean abaixoMinimo;
}

