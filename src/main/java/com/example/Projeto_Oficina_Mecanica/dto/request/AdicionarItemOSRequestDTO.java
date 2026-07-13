package com.example.Projeto_Oficina_Mecanica.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) responsável por receber os dados de inserção de itens em uma OS.
 * Permite vincular produtos (peças) ou serviços (mão de obra) especificando a quantidade desejada.
 */
@Data
public class AdicionarItemOSRequestDTO {

    // Identificador único da peça/componente no estoque (opcional se for apenas serviço)
    private Long produtoId;

    // Identificador único do serviço técnico manual (opcional se for apenas produto)
    private Long servicoId;

    // Quantidade de itens ou execuções do serviço a serem adicionadas na OS
    @NotNull(message = "A quantidade de itens é obrigatória.")
    private Integer quantidade;
}