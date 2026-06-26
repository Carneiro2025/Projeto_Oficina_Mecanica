package com.example.Projeto_Oficina_Mecanica.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdemServicoRequestDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long veiculoId;

    private Long mecanicoId;

    private String problemaRelatado;

    private String observacoes;
}

