package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioOSDTO {

    private Long numero;

    private String cliente;

    private String veiculo;

    private String mecanico;

    private String status;

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    private BigDecimal valorTotal;

}