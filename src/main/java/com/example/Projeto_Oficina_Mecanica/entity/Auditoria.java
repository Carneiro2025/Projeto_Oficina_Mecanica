package com.example.Projeto_Oficina_Mecanica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 50)
    private String acao;

    @Column(nullable = false, length = 80)
    private String entidade;

    @Column(name = "registro_id")
    private Long registroId;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(length = 50)
    private String ip;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @PrePersist
    public void prePersist() {
        dataHora = LocalDateTime.now();
    }

}
