package com.example.Projeto_Oficina_Mecanica.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrdemServicoResponseDTO {

    private Long id;

    private Long numero;

    private String cliente;

    private String veiculo;

    private String mecanico;

    private String status;

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    private BigDecimal valorTotal;
}
