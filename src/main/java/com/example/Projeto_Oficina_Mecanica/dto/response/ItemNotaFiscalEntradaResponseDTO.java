package com.example.Projeto_Oficina_Mecanica.dto.response;


import lombok.Data;

import java.math.BigDecimal;



@Data
public class ItemNotaFiscalEntradaResponseDTO {


    private Long id;


    private Long produtoId;


    private String produtoDescricao;


    private Integer quantidade;


    private BigDecimal valorUnitario;


    private BigDecimal subtotal;


}

