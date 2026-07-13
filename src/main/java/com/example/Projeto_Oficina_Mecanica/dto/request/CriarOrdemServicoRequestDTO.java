package com.example.Projeto_Oficina_Mecanica.dto.request;

import lombok.Data;

/**
 * Data Transfer Object (DTO) responsável por receber os dados necessários para a abertura de uma nova OS.
 * Concentra os IDs das entidades relacionadas e a descrição inicial do problema trazido pelo cliente.
 */
@Data
public class CriarOrdemServicoRequestDTO {

    // Identificador único do cliente proprietário ou responsável pelo veículo
    private Long clienteId;

    // Identificador único do veículo que passará pela manutenção/reparo
    private Long veiculoId;

    // Identificador único do mecânico inicialmente responsável por avaliar ou assumir a OS
    private Long mecanicoId;

    // Descrição detalhada dos sintomas, defeitos ou revisões solicitadas pelo cliente
    private String problemaRelatado;
}