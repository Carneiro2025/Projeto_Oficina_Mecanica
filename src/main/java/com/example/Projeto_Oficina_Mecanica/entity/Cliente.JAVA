package com.example.Projeto_Oficina_Mecanica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.Projeto_Oficina_Mecanica.enums.TipoPessoa;

/**
 * Entidade Cliente — suporta Pessoa Física (CPF) e Pessoa Jurídica (CNPJ).
 *
 * Regras de Negócio:
 *  RN03 - CPF/CNPJ deve ser único no sistema
 *  RN04 - Cliente inativo não pode abrir orçamento
 *  Soft-delete: ativo = false (sem exclusão física)
 */
@Entity
@Table(name = "clientes", indexes = {
        @Index(name = "idx_cliente_cpf_cnpj", columnList = "cpf_cnpj", unique = true),
        @Index(name = "idx_cliente_email",    columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private TipoPessoa tipo;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String celular;

    @Column(length = 100)
    private String email;

    @Embedded
    private Endereco endereco;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Veiculo> veiculos = new ArrayList<>();

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