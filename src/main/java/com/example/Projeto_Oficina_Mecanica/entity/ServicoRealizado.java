package com.example.Projeto_Oficina_Mecanica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 200)
    private String descricao;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
