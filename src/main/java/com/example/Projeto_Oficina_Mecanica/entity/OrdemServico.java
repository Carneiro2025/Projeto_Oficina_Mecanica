package com.example.Projeto_Oficina_Mecanica.entity;

import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(length = 150)
    private String mecanicoResponsavel;

    @Column(nullable = false)
    private LocalDate dataAbertura;

    private LocalDate previsaoEntrega;

    private LocalDate dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusOrdemServico status = StatusOrdemServico.ABERTA;

    private Integer quilometragem;

    @Column(length = 1000)
    private String observacoes;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorPecas = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorServicos = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(
            mappedBy = "ordemServico",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ItemOrdemServico> itens = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        updatedAt = LocalDateTime.now();

        if (dataAbertura == null) {
            dataAbertura = LocalDate.now();
        }

    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

    public void calcularTotal() {

        valorPecas = itens.stream()
                .filter(i -> i.getTipoItem().name().equals("PECA"))
                .map(ItemOrdemServico::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        valorServicos = itens.stream()
                .filter(i -> i.getTipoItem().name().equals("SERVICO"))
                .map(ItemOrdemServico::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        valorTotal = valorPecas
                .add(valorServicos)
                .subtract(valorDesconto);

    }

}