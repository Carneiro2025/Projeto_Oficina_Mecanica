/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: api.js
 * Descrição.....: Classe responsável pela comunicação com a API REST.
 * Autor.........: Rafael Carneiro
 * Versão........: 1.0.1
 * =====================================================================
 */

class Api {

    constructor() {
        this.baseUrl = CONFIG.API_URL;
    }

    /**
     * Obtém o Access Token armazenado.
     */
    getToken() {
        return localStorage.getItem(CONFIG.TOKEN_KEY);
    }

    /**
     * Obtém o Refresh Token.
     */
    getRefreshToken() {
        return localStorage.getItem(CONFIG.REFRESH_TOKEN_KEY);
    }

    /**
     * Salva os tokens retornados pela API.
     */
    saveTokens(data) {

        if (!data) {
            return;
        }

        if (data.accessToken) {
            localStorage.setItem(
                CONFIG.TOKEN_KEY,
                data.accessToken
            );
        }

        if (data.refreshToken) {
            localStorage.setItem(
                CONFIG.REFRESH_TOKEN_KEY,
                data.refreshToken
            );
        }

    }

    /**
     * Salva os dados do usuário autenticado.
     */
    saveUser(usuario) {

        if (!usuario) {
            return;
        }

        localStorage.setItem(
            CONFIG.USER_KEY,
            JSON.stringify(usuario)
        );
    }

    /**
     * Remove toda a sessão.
     */
    clearSession() {

        localStorage.removeItem(CONFIG.TOKEN_KEY);
        localStorage.removeItem(CONFIG.REFRESH_TOKEN_KEY);
        localStorage.removeItem(CONFIG.USER_KEY);

    }

    /**
     * Monta o Header HTTP.
     */
    getHeaders() {

        const headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        };

        const token = this.getToken();

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        return headers;
    }

    /**
     * Trata as respostas da API.
     */
    async handleResponse(response) {

        // ==========================================================
        // RESPOSTA COM SUCESSO
        // ==========================================================

        if (response.ok) {

            // HTTP 204 - No Content
            if (response.status === 204) {
                return null;
            }

            /*
             * Lemos a resposta como texto primeiro.
             *
             * Isso evita:
             * SyntaxError: Unexpected end of JSON input
             *
             * caso o servidor retorne uma resposta vazia.
             */
            const texto = await response.text();

            // Resposta vazia
            if (!texto || !texto.trim()) {
                return null;
            }

            try {

                return JSON.parse(texto);

            } catch (e) {

                console.error(
                    "A API respondeu com conteúdo que não é JSON:",
                    texto
                );

                throw new Error(
                    "Resposta inválida recebida da API."
                );
            }
        }

        // ==========================================================
        // 401 - NÃO AUTORIZADO
        // ==========================================================

        if (response.status === 401) {

            console.warn(
                "401 - Sessão inválida ou token expirado."
            );

            this.clearSession();

            window.location.href = "login.html";

            return;
        }

        // ==========================================================
        // LER CORPO DA RESPOSTA
        // ==========================================================

        /*
         * Usamos response.text() em vez de response.json().
         *
         * Dessa forma conseguimos tratar:
         *
         * - resposta JSON
         * - resposta vazia
         * - resposta texto
         */
        const texto = await response.text();

        // ==========================================================
        // 403 - ACESSO NEGADO
        // ==========================================================

        if (response.status === 403) {

            console.error(
                "403 - Acesso negado pela API."
            );

            // ------------------------------------------------------
            // Backend retornou alguma informação
            // ------------------------------------------------------

            if (texto && texto.trim()) {

                try {

                    const body = JSON.parse(texto);

                    const mensagem =
                        body.message ||
                        body.error ||
                        "Você não possui permissão para acessar este recurso.";

                    throw new Error(mensagem);

                } catch (e) {

                    /*
                     * Se o próprio JSON gerou nosso Error,
                     * propagamos a mensagem.
                     */
                    if (e instanceof Error && e.message) {
                        throw e;
                    }

                    throw new Error(
                        "Você não possui permissão para acessar este recurso."
                    );
                }
            }

            // ------------------------------------------------------
            // Backend retornou 403 sem corpo
            // ------------------------------------------------------

            throw new Error(
                "Você não possui permissão para acessar este recurso."
            );
        }

        // ==========================================================
        // OUTROS ERROS HTTP
        // ==========================================================

        let erro = `Erro HTTP ${response.status}.`;

        // ----------------------------------------------------------
        // Se existe resposta do servidor
        // ----------------------------------------------------------

        if (texto && texto.trim()) {

            try {

                const body = JSON.parse(texto);

                erro =
                    body.message ||
                    body.error ||
                    erro;

            } catch (e) {

                /*
                 * Caso o backend tenha retornado texto simples,
                 * usamos esse texto como mensagem.
                 */
                erro = texto;

                console.error(
                    "Resposta da API não está em JSON:",
                    texto
                );
            }
        }

        throw new Error(erro);
    }

    /**
     * GET
     */
    async get(endpoint) {

        const response = await fetch(

            this.baseUrl + endpoint,

            {
                method: "GET",
                headers: this.getHeaders()
            }

        );

        return this.handleResponse(response);
    }

    /**
     * POST
     */
    async post(endpoint, data) {

        const response = await fetch(

            this.baseUrl + endpoint,

            {
                method: "POST",
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            }

        );

        return this.handleResponse(response);
    }

    /**
     * PUT
     */
    async put(endpoint, data) {

        const response = await fetch(

            this.baseUrl + endpoint,

            {
                method: "PUT",
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            }

        );

        return this.handleResponse(response);
    }

    /**
     * PATCH
     */
    async patch(endpoint, data) {

        const response = await fetch(

            this.baseUrl + endpoint,

            {
                method: "PATCH",
                headers: this.getHeaders(),
                body:
                    data !== undefined
                        ? JSON.stringify(data)
                        : undefined
            }

        );

        return this.handleResponse(response);
    }

    /**
     * DELETE
     */
    async delete(endpoint) {

        const response = await fetch(

            this.baseUrl + endpoint,

            {
                method: "DELETE",
                headers: this.getHeaders()
            }

        );

        return this.handleResponse(response);
    }

}


// =====================================================================
// INSTÂNCIA GLOBAL DA API
// =====================================================================

const api = new Api();