/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: layout.js
 * Descrição.....: Carrega os componentes de layout (sidebar, navbar e
 *                  modal) usados pela tela de Fornecedores.
 * =====================================================================
 */

/**
 * Carrega o menu lateral dentro de #sidebar-container.
 */
async function carregarSidebar() {

    const container = document.getElementById("sidebar-container");

    if (!container) return;

    const resposta = await fetch("sidebar.html");

    container.innerHTML = await resposta.text();

}

/**
 * Carrega a barra superior dentro de #navbar-container e liga
 * os dados do usuário logado e o botão de logout.
 */
async function carregarNavbar() {

    const container = document.getElementById("navbar-container");

    if (!container) return;

    const resposta = await fetch("navbar.html");

    container.innerHTML = await resposta.text();

    if (typeof auth !== "undefined") {

        auth.preencherUsuario();

        const btnLogout = document.getElementById("btnLogout");

        if (btnLogout) {
            btnLogout.addEventListener("click", () => auth.logout());
        }

    }

}

/**
 * Carrega o modal de fornecedor dentro de #modalFornecedorContainer.
 */
async function carregarModalFornecedor() {

    const container = document.getElementById("modalFornecedorContainer");

    if (!container) return;

    const resposta = await fetch("modal-fornecedor.html");

    container.innerHTML = await resposta.text();

}
