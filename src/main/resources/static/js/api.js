/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: api.js
 * Descrição.....: Classe responsável pela comunicação com a API REST.
 * Autor.........: Rafael Carneiro
 * Versão........: 1.0.0
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

        if (data.accessToken) {
            localStorage.setItem(CONFIG.TOKEN_KEY, data.accessToken);
        }

        if (data.refreshToken) {
            localStorage.setItem(CONFIG.REFRESH_TOKEN_KEY, data.refreshToken);
        }

    }

    /**
     * Salva os dados do usuário autenticado.
     */
    saveUser(usuario) {
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
            "Content-Type": "application/json"
        };

        const token = this.getToken();

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        return headers;
    }

    /**
     * Trata erros da API.
     */
    async handleResponse(response) {

        if (response.ok) {

            if (response.status === 204) {
                return null;
            }

            return await response.json();
        }

        if (response.status === 401) {

            this.clearSession();

            window.location.href = "login.html";

            return;
        }

        let erro = "Erro inesperado.";

        try {

            const body = await response.json();

            erro = body.message || erro;

        } catch (e) {

            console.error("Erro ao interpretar resposta da API.", e);

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

const api = new Api();