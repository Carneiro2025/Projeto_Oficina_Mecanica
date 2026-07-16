package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.TipoItemOrdemServico;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemOrdemServicoResponseDTO {

    private Long id;

    private TipoItemOrdemServico tipoItem;

    private Long produtoId;

    private String codigoProduto;

    private String descricaoProduto;

    private String descricaoServico;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal subtotal;

}