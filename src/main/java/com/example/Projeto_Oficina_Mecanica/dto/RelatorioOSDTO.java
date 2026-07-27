package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioOSDTO {

    private String numero;

    private String cliente;

    private String veiculo;

    private String mecanico;

    private String status;

    private LocalDate dataAbertura;

    private LocalDate dataConclusao;

    private BigDecimal valorTotal;

}