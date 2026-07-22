package com.example.Projeto_Oficina_Mecanica.entity;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fluxo_caixa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FluxoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ENTRADA ou SAÍDA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentacaoCaixa tipoMovimentacao;

    // ORDEM_SERVICO, CONTA_RECEBER, CONTA_PAGAR...
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigemMovimentacaoCaixa origem;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(precision = 12, scale = 2)
    private BigDecimal saldoAnterior;

    @Column(precision = 12, scale = 2)
    private BigDecimal saldoAtual;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private FormaPagamento formaPagamento;

    @Column(nullable = false)
    private LocalDate dataMovimentacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receber_id")
    private ContaReceber contaReceber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pagar_id")
    private ContaPagar contaPagar;

    @Column(length = 500)
    private String observacao;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (dataMovimentacao == null) {
            dataMovimentacao = LocalDate.now();
        }

    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

}

