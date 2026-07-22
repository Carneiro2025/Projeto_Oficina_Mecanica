/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: auth.js
 * Descrição.....: Controle de autenticação, sessão e permissões.
 * Autor.........: Rafael Carneiro
 * Versão........: 1.0.0
 * =====================================================================
 */

class Auth {

    /**
     * Realiza o login do usuário.
     * @param {Object} credenciais
     * @returns {Promise<Object>}
     */
    async login(credenciais) {

        const resposta = await api.post(
            CONFIG.LOGIN_ENDPOINT,
            credenciais
        );

        // Salva os tokens
        api.saveTokens(resposta);

        // Salva os dados do usuário
        if (resposta.usuario) {
            api.saveUser(resposta.usuario);
        }

        return resposta;
    }

    /**
     * Logout do sistema.
     */
    logout() {

        api.clearSession();

        window.location.href = CONFIG.LOGIN_PAGE;

    }

    /**
     * Verifica se existe usuário logado.
     */
    isAuthenticated() {

        return localStorage.getItem(CONFIG.TOKEN_KEY) !== null;

    }

    /**
     * Retorna o usuário logado.
     */
    getUser() {

        const usuario = localStorage.getItem(CONFIG.USER_KEY);

        if (!usuario) {
            return null;
        }

        return JSON.parse(usuario);

    }

    /**
     * Retorna apenas o nome.
     */
    getNome() {

        const usuario = this.getUser();

        return usuario ? usuario.nome : "";

    }

    /**
     * Retorna o e-mail.
     */
    getEmail() {

        const usuario = this.getUser();

        return usuario ? usuario.email : "";

    }

    /**
     * Retorna o perfil.
     */
    getPerfil() {

        const usuario = this.getUser();

        return usuario ? usuario.perfil : "";

    }

    /**
     * Verifica se é ADMIN.
     */
    isAdmin() {

        return this.getPerfil() === CONFIG.ROLES.ADMIN;

    }

    /**
     * Verifica se é GERENTE.
     */
    isGerente() {

        return this.getPerfil() === CONFIG.ROLES.GERENTE;

    }

    /**
     * Verifica se é MECÂNICO.
     */
    isMecanico() {

        return this.getPerfil() === CONFIG.ROLES.MECANICO;

    }

    /**
     * Verifica se é ATENDENTE.
     */
    isAtendente() {

        return this.getPerfil() === CONFIG.ROLES.ATENDENTE;

    }

    /**
     * Verifica se possui determinado perfil.
     */
    hasRole(role) {

        return this.getPerfil() === role;

    }

    /**
     * Redireciona para login caso não esteja autenticado.
     */
    checkAuthentication() {

        if (!this.isAuthenticated()) {

            window.location.href = CONFIG.LOGIN_PAGE;

        }

    }

    /**
     * Preenche automaticamente os dados do usuário na tela.
     */
    preencherUsuario() {

        const usuario = this.getUser();

        if (!usuario) return;

        const nome = document.getElementById("usuarioNome");

        const email = document.getElementById("usuarioEmail");

        const perfil = document.getElementById("usuarioPerfil");

        if (nome) {
            nome.textContent = usuario.nome;
        }

        if (email) {
            email.textContent = usuario.email;
        }

        if (perfil) {
            perfil.textContent = usuario.perfil;
        }

    }

}

const auth = new Auth();