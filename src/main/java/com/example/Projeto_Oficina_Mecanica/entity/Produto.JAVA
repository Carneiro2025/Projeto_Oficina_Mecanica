package com.example.Projeto_Oficina_Mecanica.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Produto — catálogo com controle de estoque.
 *
 * Regras de Negócio:
 *  - Código deve ser único no sistema
 *  - Não é permitido utilizar produto com estoque zero em OS (verificado no EstoqueService Sprint 4)
 *  - Alerta automático quando estoque atingir estoque_minimo
 *  - Vinculado a um Fornecedor principal
 */
@Entity
@Table(name = "produtos", indexes = {
        @Index(name = "idx_produto_codigo",    columnList = "codigo",    unique = true),
        @Index(name = "idx_produto_categoria", columnList = "categoria")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;           // Código interno do produto

    @Column(nullable = false, length = 200)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaProduto categoria;

    @Column(length = 50)
    private String marca;

    @Column(length = 50)
    private String unidade;          // UN, L, KG, CX, PAR...

    @Column(name = "preco_custo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "estoque_atual", nullable = false)
    @Builder.Default
    private Integer estoqueAtual = 0;

    @Column(name = "estoque_minimo", nullable = false)
    @Builder.Default
    private Integer estoqueMinimo = 5;

    @Column(name = "estoque_maximo")
    private Integer estoqueMaximo;

    @Column(length = 100)
    private String localizacao;      // Localização física no estoque (ex: "Prateleira A3")

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    // Vinculação com Fornecedor principal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Helpers de negócio ───────────────────────────────────────────────────

    /**
     * Verifica se o produto está com estoque abaixo do mínimo.
     */
    public boolean isEstoqueAbaixoMinimo() {
        return this.estoqueAtual <= this.estoqueMinimo;
    }

    /**
     * Calcula a margem de lucro percentual.
     */
    public BigDecimal getMargemLucroPercent() {
        if (precoCusto == null || precoCusto.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return precoVenda.subtract(precoCusto)
                .divide(precoCusto, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}