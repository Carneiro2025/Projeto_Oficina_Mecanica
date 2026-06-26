package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
public class OrdemServicoResponseDTO {

    private Long id;

    private Long numero;

    private String cliente;

    private String veiculo;

    private String mecanico;

    private StatusOrdemServico status;

    private BigDecimal valorTotal;

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;
}


