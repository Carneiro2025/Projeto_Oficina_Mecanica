/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: dashboard.js
 * Descrição.....: Dashboard Principal
 *
 * REESCRITO: o arquivo anterior só buscava o nome do usuário logado —
 * nunca chamava GET /api/dashboard, então nenhum dos indicadores da
 * Fase 3 (receita/despesa/lucro do mês, estoque baixo, contas
 * vencidas) aparecia. A tela era só uma grade de atalhos.
 * ==========================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    verificarLogin();

    carregarUsuario();

    carregarDashboard();

});

/* ==========================================================
   VERIFICAR LOGIN
========================================================== */

function verificarLogin() {

    const token = localStorage.getItem(CONFIG.TOKEN_KEY);

    if (!token) {

        window.location.href = "login.html";

    }

}

/* ==========================================================
   USUÁRIO LOGADO
========================================================== */

async function carregarUsuario() {

    try {

        const usuario = await api.get("/usuarios/me");

        const span = document.getElementById("usuarioLogado");

        if (span) {
            span.innerText = `Olá, ${usuario.nome}`;
        }

    } catch (erro) {

        console.error("Erro ao carregar usuário logado:", erro);

    }

}

/* ==========================================================
   DASHBOARD (GET /api/dashboard)
========================================================== */

async function carregarDashboard() {

    try {

        mostrarLoading();

        const dados = await api.get("/dashboard");

        preencherIndicadoresGerais(dados);

        preencherFinanceiroDoMes(dados);

        preencherEstoqueBaixo(dados);

        preencherContasVencidas(dados);

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao carregar os indicadores do dashboard.");

    } finally {

        ocultarLoading();

    }

}

function preencherIndicadoresGerais(dados) {

    document.getElementById("dashTotalClientes").innerText = dados.totalClientes ?? 0;

    document.getElementById("dashTotalVeiculos").innerText = dados.totalVeiculos ?? 0;

    document.getElementById("dashTotalProdutos").innerText = dados.totalProdutos ?? 0;

    document.getElementById("dashOrdensAbertas").innerText = dados.ordensAbertas ?? 0;

    document.getElementById("dashOrdensFinalizadas").innerText = dados.ordensFinalizadas ?? 0;

}

function preencherFinanceiroDoMes(dados) {

    document.getElementById("dashReceitaMes").innerText = formatarMoedaDash(dados.receitaMes);

    document.getElementById("dashDespesaMes").innerText = formatarMoedaDash(dados.despesaMes);

    document.getElementById("dashLucroMes").innerText = formatarMoedaDash(dados.lucroMes);

}

function preencherEstoqueBaixo(dados) {

    const total = dados.totalProdutosEstoqueBaixo ?? 0;

    document.getElementById("dashEstoqueBaixoResumo").innerText =
        total === 0
            ? "Nenhum produto com estoque baixo."
            : `${total} produto(s) precisando de reposição:`;

    const lista = document.getElementById("dashListaEstoqueBaixo");

    lista.innerHTML = "";

    (dados.produtosEstoqueBaixo ?? []).slice(0, 5).forEach(produto => {

        lista.innerHTML += `
            <li>${produto.codigo} — ${produto.descricao} (${produto.estoqueAtual}/${produto.estoqueMinimo})</li>
        `;

    });

}

function preencherContasVencidas(dados) {

    const qtdPagar = dados.qtdContasPagarVencidas ?? 0;
    const valorPagar = dados.valorContasPagarVencidas ?? 0;

    document.getElementById("dashContasVencidasPagar").innerText =
        qtdPagar === 0
            ? "Nenhuma conta a pagar vencida."
            : `${qtdPagar} conta(s) a pagar vencida(s) — ${formatarMoedaDash(valorPagar)}`;

    const qtdReceber = dados.qtdContasReceberVencidas ?? 0;
    const valorReceber = dados.valorContasReceberVencidas ?? 0;

    document.getElementById("dashContasVencidasReceber").innerText =
        qtdReceber === 0
            ? "Nenhuma conta a receber vencida."
            : `${qtdReceber} conta(s) a receber vencida(s) — ${formatarMoedaDash(valorReceber)}`;

}

function formatarMoedaDash(valor) {

    return Number(valor || 0).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });

}
