/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: relatorios.js
 * Descrição.....: Controle da tela de Relatórios
 * ==========================================================
 */

function inicializarRelatorios() {

    carregarRelatorioFinanceiro();

}

/* ==========================================================
   ABAS
========================================================== */

function trocarAbaRelatorio(aba) {

    document.querySelectorAll(".aba-btn").forEach(btn => btn.classList.remove("ativa"));
    document.querySelector(`.aba-btn[data-aba="${aba}"]`).classList.add("ativa");

    document.querySelectorAll(".painel-aba").forEach(painel => painel.classList.remove("ativa"));

    if (aba === "financeiro") {
        document.getElementById("painelRelatorioFinanceiro").classList.add("ativa");
        carregarRelatorioFinanceiro();
    } else if (aba === "estoque") {
        document.getElementById("painelRelatorioEstoque").classList.add("ativa");
        carregarRelatorioEstoque();
    } else if (aba === "os") {
        document.getElementById("painelRelatorioOS").classList.add("ativa");
        carregarRelatorioOS();
    } else if (aba === "clientes") {
        document.getElementById("painelRelatorioClientes").classList.add("ativa");
        carregarRelatorioClientes();
    }

}

/* ==========================================================
   FINANCEIRO
========================================================== */

async function carregarRelatorioFinanceiro() {

    try {

        mostrarLoading();

        const r = await api.get("/relatorios/financeiro");

        document.getElementById("totalReceitas").innerText = formatarMoedaRelatorio(Number(r.totalReceitas || 0));
        document.getElementById("totalDespesas").innerText = formatarMoedaRelatorio(Number(r.totalDespesas || 0));
        document.getElementById("totalLucro").innerText = formatarMoedaRelatorio(Number(r.lucro || 0));
        document.getElementById("qtdMovimentacoes").innerText =
            `${r.quantidadeRecebimentos || 0} / ${r.quantidadePagamentos || 0}`;

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar relatório financeiro.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   ESTOQUE
========================================================== */

async function carregarRelatorioEstoque() {

    try {

        mostrarLoading();

        const lista = await api.get("/relatorios/estoque");

        const tbody = document.getElementById("tbodyRelatorioEstoque");

        tbody.innerHTML = "";

        if (!lista || lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;">Nenhum produto encontrado.</td></tr>`;
            return;
        }

        lista.forEach(p => {

            tbody.innerHTML += `
                <tr class="${p.abaixoMinimo ? "linha-estoque-baixo" : ""}">
                    <td>${p.codigoProduto}</td>
                    <td>${p.descricao}</td>
                    <td>${p.estoqueAtual}</td>
                    <td>${p.estoqueMinimo}</td>
                    <td>${p.abaixoMinimo ? "Estoque baixo" : "OK"}</td>
                </tr>
            `;

        });

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar relatório de estoque.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   ORDENS DE SERVIÇO
========================================================== */

async function carregarRelatorioOS() {

    try {

        mostrarLoading();

        const lista = await api.get("/relatorios/ordens-servico");

        const tbody = document.getElementById("tbodyRelatorioOS");

        tbody.innerHTML = "";

        if (!lista || lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;">Nenhuma OS encontrada.</td></tr>`;
            return;
        }

        lista.forEach(os => {

            tbody.innerHTML += `
                <tr>
                    <td>${os.numero}</td>
                    <td>${os.cliente}</td>
                    <td>${os.veiculo}</td>
                    <td>${os.mecanico || ""}</td>
                    <td>${os.status}</td>
                    <td>${formatarDataRelatorio(os.dataAbertura)}</td>
                    <td>${formatarDataRelatorio(os.dataConclusao)}</td>
                    <td>${formatarMoedaRelatorio(Number(os.valorTotal || 0))}</td>
                </tr>
            `;

        });

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar relatório de ordens de serviço.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   CLIENTES
========================================================== */

async function carregarRelatorioClientes() {

    try {

        mostrarLoading();

        const lista = await api.get("/relatorios/clientes");

        const tbody = document.getElementById("tbodyRelatorioClientes");

        tbody.innerHTML = "";

        if (!lista || lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;">Nenhum cliente encontrado.</td></tr>`;
            return;
        }

        lista.forEach(c => {

            tbody.innerHTML += `
                <tr>
                    <td>${c.nome}</td>
                    <td>${c.telefone || ""}</td>
                    <td>${c.email || ""}</td>
                    <td>${c.quantidadeVeiculos}</td>
                    <td>${c.quantidadeOrdensServico}</td>
                </tr>
            `;

        });

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar relatório de clientes.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   AUXILIARES
========================================================== */

function formatarMoedaRelatorio(valor) {
    return valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function formatarDataRelatorio(data) {
    if (!data) return "";
    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
}
