package com.example.Projeto_Oficina_Mecanica.enums;

/**
 * Perfis de acesso do sistema.
 *
 * ADMIN     → acesso total, gerencia usuários
 * GERENTE   → acesso a todos os módulos exceto usuários
 * ATENDENTE → cadastros, orçamentos, OS, emissão de recibos
 * MECANICO  → visualiza e executa OS atribuídas
 */

public enum PerfilUsuario {
    ADMIN,
    GERENTE,
    ATENDENTE,
    MECANICO
}
