package com.example.Projeto_Oficina_Mecanica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Mecânico.
 *
 * Regras de Negócio:
 *  - CPF deve ser único no sistema
 *  - Mecânico pode ter múltiplas especialidades
 *  - Soft-delete: ativo = false
 *  - Vinculado opcionalmente a um Usuario do sistema
 */
@Entity
@Table(name = "mecanicos", indexes = {
        @Index(name = "idx_mecanico_cpf", columnList = "cpf", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mecanico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String celular;

    @Column(length = 100)
    private String email;

    @Column(name = "crea", length = 20)
    private String crea;   // Registro profissional (opcional)

    /**
     * Lista de especialidades — armazenada em tabela auxiliar mecanico_especialidades.
     * Permite múltiplas especialidades por mecânico.
     */
    @ElementCollection(targetClass = EspecialidadeMecanico.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "mecanico_especialidades",
            joinColumns = @JoinColumn(name = "mecanico_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "especialidade", length = 30)
    @Builder.Default
    private List<EspecialidadeMecanico> especialidades = new ArrayList<>();

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Vinculação opcional com usuário do sistema.
     * Quando vinculado, o mecânico pode fazer login e gerenciar suas OS.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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