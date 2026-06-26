package com.example.Projeto_Oficina_Mecanica.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdicionarItemOSRequestDTO {

    private Long produtoId;

    private Long servicoId;

    @NotNull
    private Integer quantidade;

}

