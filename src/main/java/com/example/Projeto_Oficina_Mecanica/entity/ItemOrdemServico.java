package com.example.Projeto_Oficina_Mecanica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_ordem_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "servico_id")
    private ServicoRealizado servico;

    private Integer quantidade;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {

        if(valorUnitario != null && quantidade != null) {

            subtotal = valorUnitario.multiply(
                    BigDecimal.valueOf(quantidade)
            );
        }
    }
}
