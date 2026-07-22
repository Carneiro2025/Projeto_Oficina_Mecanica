/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: dashboard.js
 * Descrição.....: Dashboard Principal
 * ==========================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    verificarLogin();

    carregarUsuario();

});

/* ==========================================================
   VERIFICAR LOGIN
========================================================== */

function verificarLogin() {

    const token = localStorage.getItem("accessToken");

    if (!token) {

        window.location.href = "login.html";

    }

}

/* ==========================================================
   CARREGAR USUÁRIO
========================================================== */

async function carregarUsuario() {

    try {

        const usuario = await api.get("/usuarios/me");

        document.getElementById("usuarioLogado").innerHTML =
            `Olá, <strong>${usuario.nome}</strong>`;

        controlarPermissoes(usuario.perfil);

    } catch (error) {

        console.error(error);

        logout();

    }

}

/* ==========================================================
   CONTROLE DE PERFIL
========================================================== */

function controlarPermissoes(perfil) {

    switch (perfil) {

        case "ADMIN":

            console.log("Administrador");

            break;

        case "GERENTE":

            console.log("Gerente");

            break;

        case "ATENDENTE":

            console.log("Atendente");

            break;

        case "MECANICO":

            console.log("Mecânico");

            break;

        default:

            logout();

    }

}

/* ==========================================================
   LOGOUT
========================================================== */

function logout() {

    localStorage.removeItem("accessToken");

    localStorage.removeItem("refreshToken");

    localStorage.removeItem("usuario");

    window.location.href = "login.html";

}