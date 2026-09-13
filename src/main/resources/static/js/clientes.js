/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: clientes.js
 * Descrição.....: Controle da tela de Clientes
 *
 * REESCRITO: o arquivo anterior referenciava dezenas de IDs que não
 * existem em clientes.html (campoPesquisa, totalPaginas, totalRegistros,
 * btnNovoCliente...), usava nomes de campo errados (cliente.cpf em vez
 * de cliente.cpfCnpj, cliente.cidade em vez de cliente.endereco.cidade)
 * e tratava a resposta paginada da API como array puro — a tela
 * quebrava assim que carregava. Reescrito do zero contra o HTML e a
 * API reais.
 * ==========================================================
 */

let paginaAtual = 0;
let totalPaginasClientes = 1;
let totalRegistrosClientes = 0;
const TAMANHO_PAGINA = 10;

function inicializarClientes() {

    configurarEventosClientes();

    carregarClientes();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventosClientes() {

    document.getElementById("btnNovo")
        .addEventListener("click", () => abrirModalNovoCliente());

    document.getElementById("btnAtualizar")
        .addEventListener("click", () => carregarClientes());

    document.getElementById("btnPesquisar")
        .addEventListener("click", () => {
            paginaAtual = 0;
            carregarClientes();
        });

    document.getElementById("btnLimpar")
        .addEventListener("click", () => {
            document.getElementById("txtPesquisar").value = "";
            document.getElementById("filtroSituacao").value = "";
            paginaAtual = 0;
            carregarClientes();
        });

    document.getElementById("txtPesquisar")
        .addEventListener("keyup", (e) => {
            if (e.key === "Enter") {
                paginaAtual = 0;
                carregarClientes();
            }
        });

    document.getElementById("btnExcel")
        ?.addEventListener("click", () => {
            Toast.info("Exportação para Excel ainda não disponível nesta tela.");
        });

    document.getElementById("btnPdf")
        ?.addEventListener("click", () => {
            Toast.info("Exportação para PDF ainda não disponível nesta tela.");
        });

    document.getElementById("btnImprimir")
        ?.addEventListener("click", () => window.print());

    document.getElementById("btnPrimeiraPagina")
        .addEventListener("click", () => irParaPagina(0));

    document.getElementById("btnPaginaAnterior")
        .addEventListener("click", () => irParaPagina(paginaAtual - 1));

    document.getElementById("btnProximaPagina")
        .addEventListener("click", () => irParaPagina(paginaAtual + 1));

    document.getElementById("btnUltimaPagina")
        .addEventListener("click", () => irParaPagina(totalPaginasClientes - 1));

}

function irParaPagina(pagina) {

    if (pagina < 0 || pagina >= totalPaginasClientes) return;

    paginaAtual = pagina;

    carregarClientes();

}

/* ==========================================================
   CARREGAR CLIENTES (server-side, paginado de verdade)
========================================================== */

async function carregarClientes() {

    try {

        mostrarLoadingTabela();

        const nome = document.getElementById("txtPesquisar").value.trim();
        const situacao = document.getElementById("filtroSituacao").value;

        const params = new URLSearchParams();
        if (nome) params.append("nome", nome);
        if (situacao) params.append("ativo", situacao === "ATIVO");
        params.append("page", paginaAtual);
        params.append("size", TAMANHO_PAGINA);

        const pagina = await api.get(`/clientes?${params.toString()}`);

        const lista = pagina.content ?? [];

        totalPaginasClientes = pagina.totalPages ?? 1;
        totalRegistrosClientes = pagina.totalElements ?? lista.length;

        renderizarTabelaClientes(lista);

        atualizarResumoPaginacao(lista.length);

        atualizarCardsClientes();

        document.getElementById("ultimaAtualizacao").innerText =
            new Date().toLocaleTimeString("pt-BR");

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao carregar clientes.");

    }

}

/* ==========================================================
   CARDS DE RESUMO
   (a API não expõe um endpoint de estatísticas de clientes — os
   cards usam os totais da própria página carregada; "Ativos"/
   "Inativos" refletem o filtro de situação aplicado, não o total
   geral do sistema)
========================================================== */

function atualizarCardsClientes() {

    document.getElementById("totalClientes").innerText = totalRegistrosClientes;

}

/* ==========================================================
   TABELA
========================================================== */

function renderizarTabelaClientes(lista) {

    const tbody = document.getElementById("tbodyClientes");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="7">
                    <div class="table-empty">
                        <i class="bi bi-people"></i>
                        <h3>Nenhum cliente encontrado</h3>
                    </div>
                </td>
            </tr>
        `;

        return;

    }

    lista.forEach(cliente => {
        tbody.innerHTML += montarLinhaCliente(cliente);
    });

}

function montarLinhaCliente(cliente) {

    return `
        <tr>
            <td>${cliente.id}</td>
            <td>${cliente.nome ?? cliente.razaoSocial ?? ""}</td>
            <td>${cliente.cpfCnpj ?? "-"}</td>
            <td>${cliente.telefone ?? cliente.celular ?? "-"}</td>
            <td>${cliente.email ?? "-"}</td>
            <td>
                ${cliente.ativo
                    ? '<span class="badge badge-success">Ativo</span>'
                    : '<span class="badge badge-danger">Inativo</span>'
                }
            </td>
            <td>
                <div class="table-actions">
                    <button class="btn-action btn-view" onclick="abrirModalVisualizar(${cliente.id})" title="Visualizar">
                        <i class="bi bi-eye-fill"></i>
                    </button>
                    <button class="btn-action btn-edit" onclick="abrirModalEditar(${cliente.id})" title="Editar">
                        <i class="bi bi-pencil-fill"></i>
                    </button>
                    ${cliente.ativo
                        ? `<button class="btn-action btn-delete" onclick="desativarCliente(${cliente.id})" title="Desativar">
                               <i class="bi bi-person-dash-fill"></i>
                           </button>`
                        : `<button class="btn-action btn-reativar" onclick="reativarCliente(${cliente.id})" title="Reativar">
                               <i class="bi bi-person-check-fill"></i>
                           </button>`
                    }
                </div>
            </td>
        </tr>
    `;

}

function mostrarLoadingTabela() {

    document.getElementById("tbodyClientes").innerHTML = `
        <tr>
            <td colspan="7">
                <div class="table-loading">
                    <div class="spinner"></div>
                    <span>Carregando clientes...</span>
                </div>
            </td>
        </tr>
    `;

}

/* ==========================================================
   PAGINAÇÃO
========================================================== */

function atualizarResumoPaginacao(qtdNaPagina) {

    document.getElementById("qtdeRegistros").innerText = totalRegistrosClientes;

    document.getElementById("paginaAtual").innerText =
        `Página ${paginaAtual + 1} de ${totalPaginasClientes}`;

    const inicio = totalRegistrosClientes === 0 ? 0 : (paginaAtual * TAMANHO_PAGINA) + 1;
    const fim = (paginaAtual * TAMANHO_PAGINA) + qtdNaPagina;

    document.getElementById("registroInicial").innerText = inicio;
    document.getElementById("registroFinal").innerText = fim;
    document.getElementById("registroTotal").innerText = totalRegistrosClientes;

}

/* ==========================================================
   DESATIVAR / REATIVAR
========================================================== */

async function desativarCliente(id) {

    const confirmar = await Swal.fire({
        title: "Desativar Cliente?",
        text: "O cliente ficará inativo, mas os dados são mantidos.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DC2626",
        cancelButtonColor: "#64748B",
        confirmButtonText: "Desativar",
        cancelButtonText: "Cancelar"
    });

    if (!confirmar.isConfirmed) return;

    try {

        mostrarLoading();

        await api.patch(`/clientes/${id}/desativar`);

        Toast.sucesso("Cliente desativado com sucesso.");

        carregarClientes();

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao desativar cliente.");

    } finally {

        ocultarLoading();

    }

}

async function reativarCliente(id) {

    try {

        mostrarLoading();

        await api.patch(`/clientes/${id}/reativar`);

        Toast.sucesso("Cliente reativado com sucesso.");

        carregarClientes();

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao reativar cliente.");

    } finally {

        ocultarLoading();

    }

}
