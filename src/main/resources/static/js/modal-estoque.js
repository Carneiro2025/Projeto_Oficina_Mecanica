/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-estoque.js
 * Descrição.....: Modais de Movimentação e Histórico de Estoque
 * ==========================================================
 */

let modalMovimentacao;
let formMovimentacao;
let btnSalvarMovimentacao;
let btnCancelarMovimentacao;
let btnFecharModalMovimentacao;

let modalHistorico;
let btnFecharModalHistorico;

function configurarEventosModalEstoque() {

    modalMovimentacao = document.getElementById("modalMovimentacao");
    formMovimentacao = document.getElementById("formMovimentacao");

    btnSalvarMovimentacao = document.getElementById("btnSalvarMovimentacao");
    btnCancelarMovimentacao = document.getElementById("btnCancelarMovimentacao");
    btnFecharModalMovimentacao = document.getElementById("btnFecharModalMovimentacao");

    modalHistorico = document.getElementById("modalHistorico");
    btnFecharModalHistorico = document.getElementById("btnFecharModalHistorico");

    btnSalvarMovimentacao.addEventListener("click", salvarMovimentacao);

    btnCancelarMovimentacao.addEventListener("click", fecharModalMovimentacao);

    btnFecharModalMovimentacao.addEventListener("click", fecharModalMovimentacao);

    btnFecharModalHistorico.addEventListener("click", fecharModalHistorico);

}

/* ==========================================================
   ABRIR MOVIMENTAÇÃO
========================================================== */

async function abrirModalMovimentacao(produtoId = null, produtoDescricao = null, estoqueAtual = null) {

    formMovimentacao.reset();

    document.getElementById("estoqueAtualInfo").innerText = "";

    await carregarProdutosSelect(produtoId);

    if (produtoId != null) {
        document.getElementById("estoqueAtualInfo").innerText =
            `Estoque atual: ${estoqueAtual}`;
    }

    modalMovimentacao.classList.add("show");

}

async function carregarProdutosSelect(produtoSelecionado) {

    try {

        const resposta = await api.get("/produtos");

        const produtos = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        const select = document.getElementById("produtoId");

        select.innerHTML = '<option value="">Selecione...</option>';

        produtos.forEach(p => {

            select.innerHTML += `
                <option value="${p.id}">
                    ${p.codigo} - ${p.descricao}
                </option>
            `;

        });

        if (produtoSelecionado != null) {
            select.value = produtoSelecionado;
        }

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar produtos.");

    }

}

/* ==========================================================
   FECHAR
========================================================== */

function fecharModalMovimentacao() {
    modalMovimentacao.classList.remove("show");
}

function fecharModalHistorico() {
    modalHistorico.classList.remove("show");
}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarMovimentacao() {

    try {

        mostrarLoading();

        const dados = {
            produtoId: document.getElementById("produtoId").value,
            tipo: document.getElementById("tipoMovimentacao").value,
            quantidade: document.getElementById("quantidade").value,
            observacao: document.getElementById("observacao").value
        };

        await api.post("/estoque/movimentar", dados);

        Toast.sucesso("Movimentação registrada com sucesso.");

        fecharModalMovimentacao();

        carregarEstoque();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao registrar movimentação.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   HISTÓRICO
========================================================== */

async function abrirModalHistorico(produtoId) {

    try {

        mostrarLoading();

        const resposta = await api.get(`/estoque/produto/${produtoId}/historico`);

        const movimentacoes = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        preencherHistorico(movimentacoes);

        modalHistorico.classList.add("show");

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar histórico.");

    } finally {

        ocultarLoading();

    }

}

function preencherHistorico(lista) {

    const tbody = document.getElementById("tbodyHistorico");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="4" style="text-align:center;">
                    Nenhuma movimentação encontrada.
                </td>
            </tr>
        `;

        return;

    }

    lista.forEach(m => {

        tbody.innerHTML += `
            <tr>
                <td>${new Date(m.dataMovimentacao).toLocaleString("pt-BR")}</td>
                <td>${m.tipo}</td>
                <td>${m.quantidade}</td>
                <td>${m.observacao || ""}</td>
            </tr>
        `;

    });

}
