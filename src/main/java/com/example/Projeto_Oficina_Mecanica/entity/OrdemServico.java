package com.example.Projeto_Oficina_Mecanica.entity;

import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(unique = true, nullable = false)
    private Long numero;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;

    @Enumerated(EnumType.STRING)
    private StatusOrdemServico status;

    @Column(length = 1000)
    private String problemaRelatado;

    @Column(length = 1000)
    private String diagnostico;

    @Column(length = 1000)
    private String observacoes;

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(
            mappedBy = "ordemServico",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ItemOrdemServico> itens = new ArrayList<>();

    @PrePersist
    public void onCreate() {

        dataAbertura = LocalDateTime.now();

        if(status == null){
            status = StatusOrdemServico.ABERTA;
        }
    }
}
