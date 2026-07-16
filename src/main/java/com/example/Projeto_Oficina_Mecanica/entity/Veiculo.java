package com.example.Projeto_Oficina_Mecanica.entity;

import com.example.Projeto_Oficina_Mecanica.enums.Combustivel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade responsável pelo cadastro dos veículos da oficina.
 *
 * Regras de Negócio:
 *
 * RN01 - Todo veículo pertence a um cliente.
 * RN02 - A placa deve ser única.
 * RN03 - Chassi deve ser único quando informado.
 * RN04 - Renavam deve ser único quando informado.
 * RN05 - A quilometragem nunca poderá diminuir.
 * RN06 - Exclusão lógica através do campo ativo.
 */
@Entity
@Table(
        name = "veiculos",
        indexes = {
                @Index(name = "idx_veiculo_placa", columnList = "placa", unique = true),
                @Index(name = "idx_veiculo_cliente", columnList = "cliente_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cliente proprietário do veículo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    /**
     * Placa Mercosul.
     */
    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    /**
     * Marca do veículo.
     */
    @Column(nullable = false, length = 100)
    private String marca;

    /**
     * Modelo do veículo.
     */
    @Column(nullable = false, length = 100)
    private String modelo;

    /**
     * Versão.
     * Ex.: XEI, GLI, LTZ...
     */
    @Column(length = 100)
    private String versao;

    /**
     * Ano de fabricação.
     */
    @Column(nullable = false)
    private Integer anoFabricacao;

    /**
     * Ano do modelo.
     */
    @Column(nullable = false)
    private Integer anoModelo;

    /**
     * Cor predominante.
     */
    @Column(length = 50)
    private String cor;

    /**
     * Número do Chassi.
     */
    @Column(unique = true, length = 30)
    private String chassi;

    /**
     * Número do Renavam.
     */
    @Column(unique = true, length = 20)
    private String renavam;

    /**
     * Quilometragem atual.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer quilometragem = 0;

    /**
     * Tipo de combustível.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Combustivel combustivel;

    /**
     * Observações gerais.
     */
    @Column(length = 500)
    private String observacoes;

    /**
     * Exclusão lógica.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Data de criação.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última alteração.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}