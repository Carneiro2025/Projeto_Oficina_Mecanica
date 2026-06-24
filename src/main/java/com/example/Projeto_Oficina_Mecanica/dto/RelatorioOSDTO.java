package com.example.Projeto_Oficina_Mecanica.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioOSDTO {

    private Long numeroOS;

    private String cliente;

    private String veiculo;

    private String mecanico;

    private LocalDate dataAbertura;

    private String status;

    private BigDecimal valorTotal;
}

