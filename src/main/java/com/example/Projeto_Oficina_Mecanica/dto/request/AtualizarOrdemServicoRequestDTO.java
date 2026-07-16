package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class AtualizarOrdemServicoRequestDTO {

    private Long clienteId;

    private Long veiculoId;

    @Size(max = 150)
    private String mecanicoResponsavel;

    private LocalDate previsaoEntrega;

    private LocalDate dataConclusao;

    private StatusOrdemServico status;

    private Integer quilometragem;

    @Size(max = 1000)
    private String observacoes;

    @DecimalMin(value = "0.00")
    private BigDecimal valorDesconto;

    @Valid
    private List<ItemOrdemServicoRequestDTO> itens;

}