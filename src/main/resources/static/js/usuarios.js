/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: usuarios.js
 * Descrição.....: Controle da tela de Usuários
 * ==========================================================
 */

function inicializarUsuarios() {

    carregarUsuarios();

    configurarEventos();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventos() {

    document
        .getElementById("btnNovo")
        .addEventListener("click", novoUsuario);

    document
        .getElementById("btnPesquisar")
        .addEventListener("click", pesquisarUsuarios);

}

/* ==========================================================
   LISTAR USUÁRIOS
========================================================== */

async function carregarUsuarios() {

    try {

        mostrarLoading();

        // GET /api/usuarios devolve uma página (Page<UsuarioResponseDTO>),
        // não uma lista pura — por isso extraímos .content aqui.
        const resposta = await api.get("/usuarios");

        const usuarios = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        preencherTabela(usuarios);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar usuários.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   TABELA
========================================================== */

function preencherTabela(lista) {

    const tbody = document.getElementById("tbodyUsuarios");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align:center;">
                    Nenhum usuário encontrado.
                </td>
            </tr>
        `;

        return;

    }

    lista.forEach(u => {

        tbody.innerHTML += `

        <tr>

            <td>${u.id}</td>

            <td>${u.nome}</td>

            <td>${u.email}</td>

            <td>
                <span class="badge ${classeBadgePerfil(u.perfil)}">
                    ${rotuloPerfil(u.perfil)}
                </span>
            </td>

            <td>
                <span class="badge ${u.ativo ? "badge-ativo" : "badge-inativo"}">
                    ${u.ativo ? "Ativo" : "Inativo"}
                </span>
            </td>

            <td>

                <button
                    class="btn-action btn-edit"
                    title="Editar"
                    onclick="editarUsuario(${u.id})">

                    <i class="bi bi-pencil"></i>

                </button>

                ${u.ativo
                    ? `<button class="btn-action btn-delete" title="Desativar" onclick="desativarUsuario(${u.id})">
                           <i class="bi bi-person-dash"></i>
                       </button>`
                    : `<button class="btn-action btn-reativar" title="Reativar" onclick="reativarUsuario(${u.id})">
                           <i class="bi bi-person-check"></i>
                       </button>`
                }

            </td>

        </tr>

        `;

    });

}

function classeBadgePerfil(perfil) {
    const mapa = {
        ADMIN: "badge-admin",
        GERENTE: "badge-gerente",
        ATENDENTE: "badge-atendente",
        MECANICO: "badge-mecanico",
    };
    return mapa[perfil] || "badge-atendente";
}

function rotuloPerfil(perfil) {
    const mapa = {
        ADMIN: "Admin",
        GERENTE: "Gerente",
        ATENDENTE: "Atendente",
        MECANICO: "Mecânico",
    };
    return mapa[perfil] || perfil;
}

/* ==========================================================
   PESQUISA
========================================================== */

async function pesquisarUsuarios() {

    const nome = document.getElementById("txtPesquisar").value.trim();
    const perfil = document.getElementById("selectPerfil").value;

    try {

        mostrarLoading();

        const params = new URLSearchParams();
        if (nome) params.append("nome", nome);
        if (perfil) params.append("perfil", perfil);

        const resposta = await api.get(`/usuarios?${params.toString()}`);

        const usuarios = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        preencherTabela(usuarios);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro na pesquisa.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   NOVO / EDITAR
========================================================== */

function novoUsuario() {
    abrirModalNovoUsuario();
}

function editarUsuario(id) {
    abrirModalEditarUsuario(id);
}

/* ==========================================================
   DESATIVAR / REATIVAR
========================================================== */

async function desativarUsuario(id) {

    if (!confirm("Deseja desativar este usuário?")) {
        return;
    }

    try {

        await api.patch(`/usuarios/${id}/desativar`);

        Toast.sucesso("Usuário desativado.");

        carregarUsuarios();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao desativar usuário.");

    }

}

async function reativarUsuario(id) {

    try {

        await api.patch(`/usuarios/${id}/reativar`);

        Toast.sucesso("Usuário reativado.");

        carregarUsuarios();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao reativar usuário.");

    }

}
