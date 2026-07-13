package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecebimentoRequestDTO {

    /**
     * Ordem de Serviço que será paga.
     */
    @NotNull(message = "O ID da Ordem de Serviço é obrigatório.")
    private Long ordemServicoId;

    /**
     * Forma utilizada para realizar o pagamento.
     */
    @NotNull(message = "A forma de pagamento é obrigatória.")
    private FormaPagamento formaPagamento;

}