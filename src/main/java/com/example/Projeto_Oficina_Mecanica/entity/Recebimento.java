package com.example.Projeto_Oficina_Mecanica.entity;

import com.example.Projeto_Oficina_Mecanica.enums.FormaPagamento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusRecebimento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Controle financeiro dos recebimentos da oficina.
 *
 * Regras de Negócio:
 * - Todo recebimento pertence a uma Ordem de Serviço.
 * - O recebimento é criado automaticamente quando a OS é finalizada.
 * - Inicialmente o status é PENDENTE.
 * - Quando pago, registra a data do pagamento.
 */
@Entity
@Table(name = "recebimentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recebimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ordem de Serviço que originou o recebimento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    /**
     * Cliente responsável pelo pagamento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusRecebimento status = StatusRecebimento.PENDENTE;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Column(length = 500)
    private String observacao;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = StatusRecebimento.PENDENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Marca o recebimento como pago.
     */
    public void registrarPagamento(FormaPagamento formaPagamento) {

        this.formaPagamento = formaPagamento;
        this.status = StatusRecebimento.PAGO;
        this.dataPagamento = LocalDate.now();
    }

    /**
     * Cancela o recebimento.
     */
    public void cancelar() {

        this.status = StatusRecebimento.CANCELADO;
    }
}
