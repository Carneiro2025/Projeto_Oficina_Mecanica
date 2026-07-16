package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdemServicoResponseDTO {

    private Long id;

    private String numero;

    private Long clienteId;

    private String clienteNome;

    private Long veiculoId;

    private String placaVeiculo;

    private String modeloVeiculo;

    private String mecanicoResponsavel;

    private LocalDate dataAbertura;

    private LocalDate previsaoEntrega;

    private LocalDate dataConclusao;

    private StatusOrdemServico status;

    private Integer quilometragem;

    private String observacoes;

    private BigDecimal valorPecas;

    private BigDecimal valorServicos;

    private BigDecimal valorDesconto;

    private BigDecimal valorTotal;

    private Boolean ativo;

    private List<ItemOrdemServicoResponseDTO> itens;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}