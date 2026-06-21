package com.example.Projeto_Oficina_Mecanica.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Objeto embutido de endereço — reutilizado em Fornecedor, Cliente, etc.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Column(length = 200)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 50)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 9)
    private String cep;
}
