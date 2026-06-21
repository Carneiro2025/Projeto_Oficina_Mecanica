package com.example.Projeto_Oficina_Mecanica.model;

/**
 * Especialidades disponíveis para Mecânicos.
 * Um mecânico pode ter múltiplas especialidades (lista armazenada como @ElementCollection).
 */
 
public enum EspecialidadeMecanico {
    MOTOR,
    ELETRICA,
    SUSPENSAO,
    FREIOS,
    TRANSMISSAO,
    FUNILARIA,
    PINTURA,
    AR_CONDICIONADO,
    INJECAO_ELETRONICA,
    GERAL
}