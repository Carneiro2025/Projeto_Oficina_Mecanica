package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.entity.Endereco;
import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponseDTO {

    private Long id;

    private String nome;

    private String razaoSocial;

    private String cpfCnpj;

    private TipoPessoa tipo;

    private String telefone;

    private String celular;

    private String email;

    private Endereco endereco;

    private String observacoes;

    private Boolean ativo;

    private Integer quantidadeVeiculos;
}
