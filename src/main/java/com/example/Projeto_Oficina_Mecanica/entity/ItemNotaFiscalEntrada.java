package com.example.Projeto_Oficina_Mecanica.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "itens_nf_entrada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemNotaFiscalEntrada {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "nota_fiscal_entrada_id",
            nullable = false
    )
    private NotaFiscalEntrada notaFiscalEntrada;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "produto_id",
            nullable = false
    )
    private Produto produto;



    @Column(nullable = false)
    private Integer quantidade;



    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal valorUnitario;



    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal subtotal;



    @PrePersist
    @PreUpdate
    private void calcularSubtotal(){

        if(valorUnitario != null && quantidade != null){

            this.subtotal =
                    valorUnitario.multiply(
                            BigDecimal.valueOf(quantidade)
                    );
        }

    }

}