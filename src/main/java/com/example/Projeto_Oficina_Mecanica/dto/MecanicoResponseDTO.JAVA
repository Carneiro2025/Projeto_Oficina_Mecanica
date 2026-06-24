package com.example.Projeto_Oficina_Mecanica.dto.response;

import com.example.Projeto_Oficina_Mecanica.entity.EspecialidadeMecanico;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MecanicoResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String celular;
    private String email;
    private String crea;
    private List<EspecialidadeMecanico> especialidades;
    private String observacoes;
    private Boolean ativo;
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}