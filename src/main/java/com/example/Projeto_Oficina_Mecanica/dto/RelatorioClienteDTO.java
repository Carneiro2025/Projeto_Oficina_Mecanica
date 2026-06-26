package com.example.Projeto_Oficina_Mecanica.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioClienteDTO {

    private Long clienteId;

    private String nome;

    private String telefone;

    private String email;

    private Integer quantidadeVeiculos;

    private Integer quantidadeOrdensServico;
}
