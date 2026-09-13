/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: estoque.js
 * Descrição.....: Controle da tela de Estoque
 * ==========================================================
 */

function inicializarEstoque() {

    carregarEstoque();

    configurarEventos();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventos() {

    document
        .getElementById("btnNovaMovimentacao")
        .addEventListener("click", () => abrirModalMovimentacao());

    document
        .getElementById("btnPesquisar")
        .addEventListener("click", pesquisarEstoque);

    document
        .getElementById("chkApenasEstoqueBaixo")
        .addEventListener("change", pesquisarEstoque);

}

/* ==========================================================
   LISTAR (reaproveita GET /api/produtos — a tela de estoque
   mostra a posição de estoque de cada produto)
========================================================== */

async function carregarEstoque() {

    try {

        mostrarLoading();

        const resposta = await api.get("/produtos");

        const produtos = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        preencherTabela(produtos);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar estoque.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   TABELA
========================================================== */

function preencherTabela(lista) {

    const tbody = document.getElementById("tbodyEstoque");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align:center;">
                    Nenhum produto encontrado.
                </td>
            </tr>
        `;

        return;

    }

    lista.forEach(p => {

        const abaixoDoMinimo = p.estoqueAbaixoMinimo === true;

        tbody.innerHTML += `

        <tr class="${abaixoDoMinimo ? "linha-estoque-baixo" : ""}">

            <td>${p.codigo}</td>

            <td>${p.descricao}</td>

            <td>${p.estoqueAtual}</td>

            <td>${p.estoqueMinimo}</td>

            <td>
                <span class="${abaixoDoMinimo ? "badge-estoque-baixo" : "badge-estoque-ok"}">
                    ${abaixoDoMinimo ? "Estoque baixo" : "OK"}
                </span>
            </td>

            <td>

                <button
                    class="btn-action btn-movimentar"
                    title="Movimentar"
                    onclick="abrirModalMovimentacao(${p.id}, '${escapeAspas(p.descricao)}', ${p.estoqueAtual})">

                    <i class="bi bi-arrow-left-right"></i>

                </button>

                <button
                    class="btn-action btn-view"
                    title="Histórico"
                    onclick="verHistorico(${p.id})">

                    <i class="bi bi-clock-history"></i>

                </button>

            </td>

        </tr>

        `;

    });

}

function escapeAspas(texto) {
    return (texto || "").replace(/'/g, "\\'");
}

/* ==========================================================
   PESQUISA
========================================================== */

async function pesquisarEstoque() {

    const texto = document.getElementById("txtPesquisar").value.trim();
    const apenasEstoqueBaixo = document.getElementById("chkApenasEstoqueBaixo").checked;

    try {

        mostrarLoading();

        const params = new URLSearchParams();
        if (texto) params.append("descricao", texto);

        const resposta = await api.get(`/produtos?${params.toString()}`);

        let produtos = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        if (apenasEstoqueBaixo) {
            produtos = produtos.filter(p => p.estoqueAbaixoMinimo === true);
        }

        preencherTabela(produtos);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro na pesquisa.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   HISTÓRICO
========================================================== */

async function verHistorico(produtoId) {
    abrirModalHistorico(produtoId);
}
