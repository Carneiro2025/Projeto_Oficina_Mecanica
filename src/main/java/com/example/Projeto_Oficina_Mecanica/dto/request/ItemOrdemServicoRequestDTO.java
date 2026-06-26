package com.example.Projeto_Oficina_Mecanica.dto.request;

import lombok.Data;

@Data
public class ItemOrdemServicoRequestDTO {

    private Long produtoId;

    private Long servicoId;

    private Integer quantidade;
}

