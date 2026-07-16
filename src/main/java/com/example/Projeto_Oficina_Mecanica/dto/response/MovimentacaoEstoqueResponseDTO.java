package com.example.Projeto_Oficina_Mecanica.dto.response;


import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MovimentacaoEstoqueResponseDTO {


    private Long id;


    private Long produtoId;


    private String produtoDescricao;


    private TipoMovimentacaoEstoque tipo;


    private Integer quantidade;


    private String observacao;


    private LocalDateTime dataMovimentacao;

}
