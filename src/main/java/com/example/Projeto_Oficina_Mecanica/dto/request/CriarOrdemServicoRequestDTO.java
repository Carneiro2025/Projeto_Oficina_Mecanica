package com.example.Projeto_Oficina_Mecanica.dto.request;

import lombok.Data;

@Data
public class CriarOrdemServicoRequestDTO {

    private Long clienteId;

    private Long veiculoId;

    private Long mecanicoId;

    private String problemaRelatado;
}