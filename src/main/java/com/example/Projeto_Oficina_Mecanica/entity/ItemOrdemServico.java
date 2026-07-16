package com.example.Projeto_Oficina_Mecanica.entity;

import com.example.Projeto_Oficina_Mecanica.enums.TipoItemOrdemServico;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItemOrdemServico tipoItem;

    @Column(length = 250)
    private String descricaoServico;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {

        if (valorUnitario != null && quantidade != null) {

            subtotal = valorUnitario.multiply(
                    BigDecimal.valueOf(quantidade)
            );

        }

    }

}