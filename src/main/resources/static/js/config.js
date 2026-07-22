/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: config.js
 * Descrição.....: Configurações globais do sistema.
 * Autor.........: Rafael Carneiro
 * Versão........: 1.0.0
 * =====================================================================
 */

const CONFIG = {

    /**
     * ==========================================================
     * Dados da Aplicação
     * ==========================================================
     */
    APP_NAME: "OficinaPRO",

    VERSION: "1.0.0",

    COMPANY: "Oficina Mecânica",

    /**
     * ==========================================================
     * API
     * ==========================================================
     */

    API_URL: "http://localhost:8080/api",

    /**
     * ==========================================================
     * Endpoints
     * ==========================================================
     */

    LOGIN_ENDPOINT: "/auth/login",

    REFRESH_ENDPOINT: "/auth/refresh",

    LOGOUT_ENDPOINT: "/auth/logout",

    /**
     * ==========================================================
     * LocalStorage
     * ==========================================================
     */

    TOKEN_KEY: "accessToken",

    REFRESH_TOKEN_KEY: "refreshToken",

    USER_KEY: "usuario",

    /**
     * ==========================================================
     * Tempo das Mensagens
     * ==========================================================
     */

    MESSAGE_TIME: 3000,

    /**
     * ==========================================================
     * Rotas
     * ==========================================================
     */

    LOGIN_PAGE: "login.html",

    DASHBOARD_PAGE: "dashboard.html",

    /**
     * ==========================================================
     * Perfis do Sistema
     * ==========================================================
     */

    ROLES: {

        ADMIN: "ADMIN",

        GERENTE: "GERENTE",

        MECANICO: "MECANICO",

        ATENDENTE: "ATENDENTE"

    }

};

/**
 * Impede alteração das configurações durante a execução.
 */
Object.freeze(CONFIG);