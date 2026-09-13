/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: financeiro.js
 * Descrição.....: Controle da tela Financeira (Contas a Pagar,
 *                  Contas a Receber e Fluxo de Caixa)
 * ==========================================================
 */

let apenasPendentesPagar = false;
let apenasPendentesReceber = false;

function inicializarFinanceiro() {

    carregarContasPagar();

    carregarResumo();

    configurarEventos();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventos() {

    document
        .getElementById("btnNovaContaPagar")
        .addEventListener("click", () => abrirModalContaPagar());

    document
        .getElementById("btnFiltrarPendentesPagar")
        .addEventListener("click", () => {
            apenasPendentesPagar = !apenasPendentesPagar;
            carregarContasPagar();
        });

    document
        .getElementById("btnFiltrarPendentesReceber")
        .addEventListener("click", () => {
            apenasPendentesReceber = !apenasPendentesReceber;
            carregarContasReceber();
        });

}

/* ==========================================================
   ABAS
========================================================== */

function trocarAba(aba) {

    document.querySelectorAll(".aba-btn").forEach(btn => btn.classList.remove("ativa"));
    document.querySelector(`.aba-btn[data-aba="${aba}"]`).classList.add("ativa");

    document.querySelectorAll(".painel-aba").forEach(painel => painel.classList.remove("ativa"));

    if (aba === "pagar") {
        document.getElementById("painelPagar").classList.add("ativa");
        carregarContasPagar();
    } else if (aba === "receber") {
        document.getElementById("painelReceber").classList.add("ativa");
        carregarContasReceber();
    } else if (aba === "fluxo") {
        document.getElementById("painelFluxo").classList.add("ativa");
        carregarFluxoCaixa();
    }

}

/* ==========================================================
   RESUMO
========================================================== */

async function carregarResumo() {

    try {

        const pendentesPagar = await api.get("/contas-pagar/pendentes");
        const totalPagar = (pendentesPagar || []).reduce((soma, c) => soma + Number(c.valor), 0);

        document.getElementById("totalContasPagarPendentes").innerText = formatarMoeda(totalPagar);

        const contasReceber = await api.get("/contas-receber");
        const pendentesReceber = (contasReceber || []).filter(c => c.status === "PENDENTE");
        const totalReceber = pendentesReceber.reduce((soma, c) => soma + Number(c.valor), 0);

        document.getElementById("totalContasReceberPendentes").innerText = formatarMoeda(totalReceber);

    } catch (error) {
        console.error("Erro ao carregar resumo financeiro:", error);
    }

}

function formatarMoeda(valor) {
    return valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

/* ==========================================================
   CONTAS A PAGAR
========================================================== */

async function carregarContasPagar() {

    try {

        mostrarLoading();

        let lista;

        if (apenasPendentesPagar) {
            lista = await api.get("/contas-pagar/pendentes");
        } else {
            const resposta = await api.get("/contas-pagar");
            lista = Array.isArray(resposta) ? resposta : (resposta.content ?? []);
        }

        preencherTabelaContasPagar(lista);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar contas a pagar.");

    } finally {

        ocultarLoading();

    }

}

function preencherTabelaContasPagar(lista) {

    const tbody = document.getElementById("tbodyContasPagar");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr><td colspan="6" style="text-align:center;">Nenhuma conta encontrada.</td></tr>
        `;

        return;

    }

    lista.forEach(c => {

        tbody.innerHTML += `
            <tr>
                <td>${c.fornecedorRazaoSocial || ""}</td>
                <td>${c.descricao || ""}</td>
                <td>${formatarData(c.dataVencimento)}</td>
                <td>${formatarMoeda(Number(c.valor))}</td>
                <td><span class="badge-status ${classeBadgeStatus(c.status)}">${c.status}</span></td>
                <td>
                    ${c.status === "PENDENTE"
                        ? `<button class="btn-action btn-pagar" title="Registrar pagamento" onclick="abrirModalPagamentoConta(${c.id})">
                               <i class="bi bi-cash-coin"></i>
                           </button>`
                        : ""
                    }
                    <button class="btn-action btn-delete" title="Excluir" onclick="excluirContaPagar(${c.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;

    });

}

async function excluirContaPagar(id) {

    if (!confirm("Deseja excluir esta conta a pagar?")) return;

    try {

        await api.delete(`/contas-pagar/${id}`);

        Toast.sucesso("Conta excluída.");

        carregarContasPagar();

        carregarResumo();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao excluir conta.");

    }

}

/* ==========================================================
   CONTAS A RECEBER
========================================================== */

async function carregarContasReceber() {

    try {

        mostrarLoading();

        const resposta = await api.get("/contas-receber");

        let lista = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        if (apenasPendentesReceber) {
            lista = lista.filter(c => c.status === "PENDENTE");
        }

        preencherTabelaContasReceber(lista);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar contas a receber.");

    } finally {

        ocultarLoading();

    }

}

function preencherTabelaContasReceber(lista) {

    const tbody = document.getElementById("tbodyContasReceber");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr><td colspan="6" style="text-align:center;">Nenhuma conta encontrada.</td></tr>
        `;

        return;

    }

    lista.forEach(c => {

        tbody.innerHTML += `
            <tr>
                <td>${c.clienteNome || ""}</td>
                <td>${c.numeroOrdemServico || ""}</td>
                <td>${formatarData(c.dataVencimento)}</td>
                <td>${formatarMoeda(Number(c.valor))}</td>
                <td><span class="badge-status ${classeBadgeStatus(c.status)}">${c.status}</span></td>
                <td>
                    ${c.status === "PENDENTE"
                        ? `<button class="btn-action btn-pagar" title="Registrar recebimento" onclick="abrirModalRecebimentoConta(${c.id})">
                               <i class="bi bi-cash-coin"></i>
                           </button>`
                        : ""
                    }
                </td>
            </tr>
        `;

    });

}

/* ==========================================================
   FLUXO DE CAIXA
========================================================== */

async function carregarFluxoCaixa() {

    try {

        mostrarLoading();

        const resposta = await api.get("/fluxo-caixa");

        const lista = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        preencherTabelaFluxoCaixa(lista);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar fluxo de caixa.");

    } finally {

        ocultarLoading();

    }

}

function preencherTabelaFluxoCaixa(lista) {

    const tbody = document.getElementById("tbodyFluxoCaixa");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr><td colspan="6" style="text-align:center;">Nenhuma movimentação encontrada.</td></tr>
        `;

        return;

    }

    lista.forEach(f => {

        const entrada = f.tipoMovimentacao === "ENTRADA";

        tbody.innerHTML += `
            <tr>
                <td>${formatarData(f.dataMovimentacao)}</td>
                <td>${f.tipoMovimentacao}</td>
                <td>${f.origem || ""}</td>
                <td>${f.descricao || ""}</td>
                <td class="${entrada ? "valor-entrada" : "valor-saida"}">${formatarMoeda(Number(f.valor))}</td>
                <td>${formatarMoeda(Number(f.saldoAtual))}</td>
            </tr>
        `;

    });

}

/* ==========================================================
   AUXILIARES
========================================================== */

function classeBadgeStatus(status) {
    const mapa = {
        PENDENTE: "badge-pendente",
        PAGO: "badge-pago",
        ATRASADO: "badge-atrasado",
        CANCELADO: "badge-cancelado",
    };
    return mapa[status] || "badge-pendente";
}

function formatarData(data) {
    if (!data) return "";
    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
}
