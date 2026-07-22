/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: login.js
 * Descrição.....: Controle da tela de login.
 * Autor.........: Rafael Carneiro
 * Versão........: 1.0.0
 * =====================================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    // Se já estiver logado, vai para o Dashboard
    if (auth.isAuthenticated()) {
        window.location.href = CONFIG.DASHBOARD_PAGE;
        return;
    }

    inicializarLogin();

});

/**
 * Inicializa os eventos da tela.
 */
function inicializarLogin() {

    const form = document.getElementById("formLogin");

    const btnMostrarSenha = document.getElementById("mostrarSenha");

    form.addEventListener("submit", realizarLogin);

    btnMostrarSenha.addEventListener("click", mostrarSenha);

}

/**
 * Exibe ou oculta a senha.
 */
function mostrarSenha() {

    const senha = document.getElementById("senha");

    const icone = document.querySelector("#mostrarSenha i");

    if (senha.type === "password") {

        senha.type = "text";

        icone.classList.remove("bi-eye");

        icone.classList.add("bi-eye-slash");

    } else {

        senha.type = "password";

        icone.classList.remove("bi-eye-slash");

        icone.classList.add("bi-eye");

    }

}

/**
 * Realiza login.
 */
async function realizarLogin(event) {

    event.preventDefault();

    const email = document.getElementById("email").value.trim();

    const senha = document.getElementById("senha").value.trim();

    if (!email || !senha) {

        util.alerta("Informe e-mail e senha.");

        return;

    }

    if (!util.emailValido(email)) {

        util.alerta("E-mail inválido.");

        return;

    }

    util.mostrarLoading();

    try {

        const resposta = await auth.login({

            email: email,

            senha: senha

        });

        util.esconderLoading();

        util.sucesso("Login realizado com sucesso!");

        console.log(resposta);

        setTimeout(() => {

            window.location.href = CONFIG.DASHBOARD_PAGE;

        }, 800);

    } catch (erro) {

        util.esconderLoading();

        util.erro(erro.message);

    }

}
