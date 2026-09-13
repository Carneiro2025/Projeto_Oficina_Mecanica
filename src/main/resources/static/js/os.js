/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: os.js
 * Descrição.....: Listagem da tela de Ordens de Serviço
 *
 * NOVO: este arquivo não existia. A tabela #corpoTabelaOS em os.html
 * tinha o comentário "Preenchido via JavaScript" mas nenhum arquivo
 * de fato fazia isso — modal-os.js só cuida do formulário de criar/
 * editar uma OS, nunca listava as OS existentes. A tela principal
 * ficava permanentemente vazia.
 * ==========================================================
 */

let paginaAtualOS = 0;
let totalPaginasOS = 1;
const TAMANHO_PAGINA_OS = 10;

document.addEventListener("DOMContentLoaded", () => {

    inicializarListaOS();

});

function inicializarListaOS() {

    configurarEventosListaOS();

    carregarOrdensServico();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventosListaOS() {

    document.getElementById("btnNovaOS")
        ?.addEventListener("click", abrirModalNovaOS);

    document.getElementById("btnPesquisar")
        ?.addEventListener("click", () => {
            paginaAtualOS = 0;
            carregarOrdensServico();
        });

    document.getElementById("btnAtualizar")
        ?.addEventListener("click", carregarOrdensServico);

}

/* ==========================================================
   LISTAR (GET /api/ordens-servico/filtros)
========================================================== */

async function carregarOrdensServico() {

    const tbody = document.getElementById("corpoTabelaOS");

    tbody.innerHTML = `
        <tr>
            <td colspan="10" style="text-align:center;">
                Carregando ordens de serviço...
            </td>
        </tr>
    `;

    try {

        const numero = document.getElementById("campoPesquisa")?.value.trim();
        const status = document.getElementById("filtroStatus")?.value;
        const dataInicial = document.getElementById("dataInicial")?.value;
        const dataFinal = document.getElementById("dataFinal")?.value;

        const params = new URLSearchParams();
        if (numero) params.append("numero", numero);
        if (status) params.append("status", status);
        if (dataInicial) params.append("dataInicial", dataInicial);
        if (dataFinal) params.append("dataFinal", dataFinal);
        params.append("page", paginaAtualOS);
        params.append("size", TAMANHO_PAGINA_OS);

        const resposta = await fetch(
            `${CONFIG.API_URL}/ordens-servico/filtros?${params.toString()}`,
            { headers: auth.obterHeaders ? auth.obterHeaders() : { "Authorization": `Bearer ${localStorage.getItem("accessToken")}` } }
        );

        if (!resposta.ok) {
            throw new Error("Erro ao carregar ordens de serviço.");
        }

        const pagina = await resposta.json();

        totalPaginasOS = pagina.totalPages ?? 1;

        renderizarTabelaOS(pagina.content ?? []);

    } catch (erro) {

        console.error(erro);

        tbody.innerHTML = `
            <tr>
                <td colspan="10" style="text-align:center;">
                    Erro ao carregar ordens de serviço.
                </td>
            </tr>
        `;

        if (typeof Toast !== "undefined") {
            Toast.erro("Erro ao carregar ordens de serviço.");
        }

    }

}

/* ==========================================================
   TABELA
========================================================== */

function renderizarTabelaOS(lista) {

    const tbody = document.getElementById("corpoTabelaOS");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="10" style="text-align:center;">
                    Nenhuma ordem de serviço encontrada.
                </td>
            </tr>
        `;

        return;

    }

    lista.forEach(os => {
        tbody.innerHTML += montarLinhaOS(os);
    });

}

function montarLinhaOS(os) {

    return `
        <tr>
            <td>${os.numero}</td>
            <td>${os.clienteNome ?? ""}</td>
            <td>${os.modeloVeiculo ?? ""}</td>
            <td>${os.placaVeiculo ?? ""}</td>
            <td>${os.mecanicoResponsavel ?? "-"}</td>
            <td>${formatarDataOS(os.dataAbertura)}</td>
            <td>${formatarDataOS(os.previsaoEntrega)}</td>
            <td>${badgeStatusOS(os.status)}</td>
            <td>${formatarMoedaOS(os.valorTotal)}</td>
            <td class="text-center">
                <button class="btn-action btn-view" title="Editar" onclick="abrirModalEditarOSPorId(${os.id})">
                    <i class="bi bi-pencil-square"></i>
                </button>
                ${os.status !== "FINALIZADA" && os.status !== "CANCELADA"
                    ? `<button class="btn-action btn-success" title="Finalizar" onclick="finalizarOSDaLista(${os.id})">
                           <i class="bi bi-check-circle"></i>
                       </button>
                       <button class="btn-action btn-delete" title="Cancelar" onclick="cancelarOSDaLista(${os.id})">
                           <i class="bi bi-x-circle"></i>
                       </button>`
                    : ""
                }
            </td>
        </tr>
    `;

}

function badgeStatusOS(status) {

    const mapa = {
        ABERTA: "badge-pendente",
        EM_ANDAMENTO: "badge-info",
        AGUARDANDO_PECA: "badge-aviso",
        AGUARDANDO_CLIENTE: "badge-aviso",
        FINALIZADA: "badge-pago",
        ENTREGUE: "badge-pago",
        CANCELADA: "badge-cancelado"
    };

    const classe = mapa[status] || "badge-pendente";

    return `<span class="badge-status ${classe}">${status}</span>`;

}

function formatarMoedaOS(valor) {
    return Number(valor || 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarDataOS(data) {
    if (!data) return "-";
    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
}

/* ==========================================================
   AÇÕES A PARTIR DA LISTA
========================================================== */

async function abrirModalEditarOSPorId(id) {

    try {

        const resposta = await fetch(
            `${CONFIG.API_URL}/ordens-servico/${id}`,
            { headers: { "Authorization": `Bearer ${localStorage.getItem("accessToken")}` } }
        );

        const os = await resposta.json();

        if (typeof abrirModalEditarOS === "function") {
            abrirModalEditarOS(os);
        }

    } catch (erro) {

        console.error(erro);

        if (typeof Toast !== "undefined") {
            Toast.erro("Erro ao carregar a OS para edição.");
        }

    }

}

async function finalizarOSDaLista(id) {

    if (!confirm("Deseja finalizar esta ordem de serviço?")) return;

    try {

        const resposta = await fetch(
            `${CONFIG.API_URL}/ordens-servico/${id}/finalizar`,
            {
                method: "PATCH",
                headers: { "Authorization": `Bearer ${localStorage.getItem("accessToken")}` }
            }
        );

        if (!resposta.ok) throw new Error("Erro ao finalizar.");

        if (typeof Toast !== "undefined") Toast.sucesso("Ordem de serviço finalizada.");

        carregarOrdensServico();

    } catch (erro) {

        console.error(erro);

        if (typeof Toast !== "undefined") Toast.erro("Erro ao finalizar a OS.");

    }

}

async function cancelarOSDaLista(id) {

    if (!confirm("Deseja cancelar esta ordem de serviço?")) return;

    try {

        const resposta = await fetch(
            `${CONFIG.API_URL}/ordens-servico/${id}/cancelar`,
            {
                method: "PATCH",
                headers: { "Authorization": `Bearer ${localStorage.getItem("accessToken")}` }
            }
        );

        if (!resposta.ok) throw new Error("Erro ao cancelar.");

        if (typeof Toast !== "undefined") Toast.sucesso("Ordem de serviço cancelada.");

        carregarOrdensServico();

    } catch (erro) {

        console.error(erro);

        if (typeof Toast !== "undefined") Toast.erro("Erro ao cancelar a OS.");

    }

}
