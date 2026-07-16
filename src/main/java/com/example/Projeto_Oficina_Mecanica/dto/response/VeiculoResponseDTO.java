package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.enums.Combustivel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Dados do veículo")
public class VeiculoResponseDTO {

    private Long id;

    private Long clienteId;

    private String clienteNome;

    private String placa;

    private String marca;

    private String modelo;

    private String versao;

    private Integer anoFabricacao;

    private Integer anoModelo;

    private String cor;

    private String chassi;

    private String renavam;

    private Integer quilometragem;

    private Combustivel combustivel;

    private String observacoes;

    private Boolean ativo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
