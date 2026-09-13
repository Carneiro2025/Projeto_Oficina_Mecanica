/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: produtos.js
 * Descrição.....: Controle da tela de Produtos
 *
 * REESCRITO: pesquisarProdutos() não extraía .content da resposta
 * paginada (quebrava ao pesquisar) e mandava parâmetros que a API não
 * reconhece (?pesquisa=, ?situacao= — o back-end só aceita descricao/
 * codigo/categoria/fornecedorId). A paginação nos botões também era
 * só cosmética: trocava o texto "Página X de Y" sem nunca buscar
 * dados novos. Agora usa paginação de verdade (page/size na API).
 * Corrigido também produto.valorVenda → produto.precoVenda (nome real
 * do campo no ProdutoResponseDTO).
 * ==========================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    inicializarProdutos();

});

let paginaAtualProdutos = 0;
let totalPaginasProdutos = 1;
const TAMANHO_PAGINA_PRODUTOS = 10;

/* ==========================================================
   INICIALIZAÇÃO
========================================================== */

function inicializarProdutos() {

    configurarEventos();

    carregarCategorias();

    carregarProdutos();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventos() {

    document.getElementById("btnNovo").addEventListener("click", abrirModalNovoProduto);

    document.getElementById("btnPesquisar").addEventListener("click", () => {
        paginaAtualProdutos = 0;
        carregarProdutos();
    });

    document.getElementById("btnAtualizar").addEventListener("click", carregarProdutos);

    document.getElementById("btnLimpar").addEventListener("click", limparFiltros);

    document.getElementById("btnExcel")?.addEventListener("click", exportarExcel);

    document.getElementById("btnPdf")?.addEventListener("click", exportarPDF);

    document.getElementById("btnImprimir")?.addEventListener("click", imprimirProdutos);

    document.getElementById("btnPrimeiraPagina")?.addEventListener("click", () => irParaPaginaProdutos(0));

    document.getElementById("btnPaginaAnterior")?.addEventListener("click", () => irParaPaginaProdutos(paginaAtualProdutos - 1));

    document.getElementById("btnProximaPagina")?.addEventListener("click", () => irParaPaginaProdutos(paginaAtualProdutos + 1));

    document.getElementById("btnUltimaPagina")?.addEventListener("click", () => irParaPaginaProdutos(totalPaginasProdutos - 1));

}

function irParaPaginaProdutos(pagina) {

    if (pagina < 0 || pagina >= totalPaginasProdutos) return;

    paginaAtualProdutos = pagina;

    carregarProdutos();

}

/* ==========================================================
   LISTAR PRODUTOS (paginação real, sem filtro de texto)
========================================================== */

async function carregarProdutos() {

    try {

        mostrarLoading();

<<<<<<< HEAD
        const params = new URLSearchParams();
        params.append("page", paginaAtualProdutos);
        params.append("size", TAMANHO_PAGINA_PRODUTOS);
=======
        const resposta = await api.get("/produtos");

        const produtos = Array.isArray(resposta) ? resposta : (resposta.content ?? []);
>>>>>>> origin/main

        const pagina = await api.get(`/produtos?${params.toString()}`);

        aplicarResultadoPaginado(pagina);

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao carregar produtos.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   CARREGAR CATEGORIAS
   (o back-end tem GET /api/produtos/categorias — mas como é um
   enum fixo de 13 valores, populamos aqui direto pra evitar mais
   uma chamada de rede no carregamento inicial da tela)
========================================================== */

function carregarCategorias() {

    const categorias = [
        { valor: "OLEO_LUBRIFICANTE", nome: "Óleo/Lubrificante" },
        { valor: "FILTRO",            nome: "Filtro" },
        { valor: "PECA_MOTOR",        nome: "Peça de Motor" },
        { valor: "PECA_FREIO",        nome: "Peça de Freio" },
        { valor: "PECA_SUSPENSAO",    nome: "Peça de Suspensão" },
        { valor: "PECA_ELETRICA",     nome: "Peça Elétrica" },
        { valor: "PECA_TRANSMISSAO",  nome: "Peça de Transmissão" },
        { valor: "PNEU",              nome: "Pneu" },
        { valor: "BATERIA",           nome: "Bateria" },
        { valor: "FLUIDO",            nome: "Fluido" },
        { valor: "ACESSORIO",         nome: "Acessório" },
        { valor: "FERRAMENTA",        nome: "Ferramenta" },
        { valor: "OUTROS",            nome: "Outros" },
    ];

    const select = document.getElementById("filtroCategoria");

    if (!select) return;

    categorias.forEach(categoria => {

        select.innerHTML += `
            <option value="${categoria.valor}">
                ${categoria.nome}
            </option>
        `;

    });

}

/* ==========================================================
   PESQUISAR PRODUTOS
   (o back-end só filtra por descricao/codigo/categoria/fornecedorId
   — não existe filtro de "situação"/ativo nesta API; o texto digitado
   é usado como filtro de descrição)
========================================================== */

async function pesquisarProdutos() {

    const pesquisa = document.getElementById("txtPesquisar").value.trim();
    const categoria = document.getElementById("filtroCategoria").value;

    try {

        mostrarLoading();

        const params = new URLSearchParams();
        if (pesquisa) params.append("descricao", pesquisa);
        if (categoria) params.append("categoria", categoria);
        params.append("page", 0);
        params.append("size", TAMANHO_PAGINA_PRODUTOS);

        paginaAtualProdutos = 0;

        const pagina = await api.get(`/produtos?${params.toString()}`);

        aplicarResultadoPaginado(pagina);

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao pesquisar produtos.");

    } finally {

        ocultarLoading();

    }

}

function aplicarResultadoPaginado(pagina) {

    const produtos = pagina.content ?? (Array.isArray(pagina) ? pagina : []);

    totalPaginasProdutos = pagina.totalPages ?? 1;

    preencherTabela(produtos);

    atualizarCards(produtos, pagina.totalElements ?? produtos.length);

    atualizarResumo(produtos, pagina.totalElements ?? produtos.length);

    atualizarPaginacao();

}

/* ==========================================================
   LIMPAR FILTROS
========================================================== */

function limparFiltros() {

    document.getElementById("txtPesquisar").value = "";

    document.getElementById("filtroCategoria").value = "";

    const filtroSituacao = document.getElementById("filtroSituacao");
    if (filtroSituacao) filtroSituacao.value = "";

    paginaAtualProdutos = 0;

    carregarProdutos();

}

/* ==========================================================
   ATUALIZAR CARDS
   (refletem só a página atual — a API não expõe um endpoint de
   estatísticas globais de produtos)
========================================================== */

function atualizarCards(produtos, totalGeral) {

    const ativos = produtos.filter(p => p.ativo);

    const inativos = produtos.filter(p => !p.ativo);

    const estoqueBaixo = produtos.filter(p => p.estoqueAbaixoMinimo === true);

    document.getElementById("totalProdutos").textContent = totalGeral;

    document.getElementById("produtosAtivos").textContent = ativos.length;

    document.getElementById("produtosInativos").textContent = inativos.length;

    document.getElementById("estoqueBaixo").textContent = estoqueBaixo.length;

}

/* ==========================================================
   RESUMO
========================================================== */

function atualizarResumo(produtos, totalGeral) {

    document.getElementById("qtdeRegistros").textContent = totalGeral;

    document.getElementById("registroInicial").textContent =
        totalGeral > 0 ? (paginaAtualProdutos * TAMANHO_PAGINA_PRODUTOS) + 1 : 0;

    document.getElementById("registroFinal").textContent =
        (paginaAtualProdutos * TAMANHO_PAGINA_PRODUTOS) + produtos.length;

    document.getElementById("registroTotal").textContent = totalGeral;

    document.getElementById("ultimaAtualizacao").textContent =
        new Date().toLocaleString("pt-BR");

}

/* ==========================================================
   PREENCHER TABELA
========================================================== */

function preencherTabela(produtos) {

    const tbody = document.getElementById("tbodyProdutos");

    tbody.innerHTML = "";

    if (produtos.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align:center">
                    Nenhum produto encontrado.
                </td>
            </tr>
        `;

        return;

    }

    produtos.forEach(produto => {

        tbody.innerHTML += criarLinhaProduto(produto);

    });

}

/* ==========================================================
   CRIAR LINHA DA TABELA
========================================================== */

function criarLinhaProduto(produto) {

    let badgeSituacao = produto.ativo
        ? '<span class="badge badge-ativo">Ativo</span>'
        : '<span class="badge badge-inativo">Inativo</span>';

    let estoqueClasse = "estoque-normal";

    if (produto.estoqueAtual <= 0) {
        estoqueClasse = "estoque-critico";
    } else if (produto.estoqueAbaixoMinimo) {
        estoqueClasse = "estoque-baixo";
    }

    return `
        <tr>
            <td>${produto.id}</td>
            <td>${produto.codigo}</td>
            <td>${produto.descricao}</td>
            <td>${produto.categoria ?? ""}</td>
            <td>${produto.unidade ?? ""}</td>
            <td class="${estoqueClasse}">${produto.estoqueAtual}</td>
            <td>${formatarMoeda(produto.precoVenda)}</td>
            <td>${badgeSituacao}</td>
            <td style="text-align:center;">
                <button class="btn-action btn-view" title="Visualizar" onclick="visualizarProduto(${produto.id})">
                    <i class="bi bi-eye-fill"></i>
                </button>
                <button class="btn-action btn-edit" title="Editar" onclick="editarProduto(${produto.id})">
                    <i class="bi bi-pencil-square"></i>
                </button>
                ${produto.ativo
                    ? `<button class="btn-action btn-delete" title="Desativar" onclick="excluirProduto(${produto.id})">
                           <i class="bi bi-trash-fill"></i>
                       </button>`
                    : `<button class="btn-action btn-reativar" title="Reativar" onclick="reativarProduto(${produto.id})">
                           <i class="bi bi-arrow-counterclockwise"></i>
                       </button>`
                }
            </td>
        </tr>
    `;

}

function formatarMoeda(valor) {

    return Number(valor || 0).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });

}

/* ==========================================================
   NOVO / VISUALIZAR / EDITAR
========================================================== */

function abrirModalNovoProduto() {
    abrirModalProduto();
}

function visualizarProduto(id) {
    abrirModalVisualizarProduto(id);
}

function editarProduto(id) {
    abrirModalEditarProduto(id);
}

/* ==========================================================
   DESATIVAR / REATIVAR
   (o back-end não tem DELETE de produto — só desativar/reativar,
   pra manter o histórico de vendas/estoque íntegro)
========================================================== */

async function excluirProduto(id) {

    const confirmar = confirm("Deseja desativar este produto?");

    if (!confirmar) return;

    try {

        mostrarLoading();

        await api.patch(`/produtos/${id}/desativar`);

        Toast.sucesso("Produto desativado com sucesso.");

        carregarProdutos();

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao desativar produto.");

    } finally {

        ocultarLoading();

    }

}

async function reativarProduto(id) {

    try {

        mostrarLoading();

        await api.patch(`/produtos/${id}/reativar`);

        Toast.sucesso("Produto reativado com sucesso.");

        carregarProdutos();

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao reativar produto.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   EXPORTAR / IMPRIMIR
========================================================== */

function exportarExcel() {
    Toast.info("Exportação para Excel ainda não disponível nesta tela.");
}

function exportarPDF() {
    Toast.info("Exportação para PDF ainda não disponível nesta tela.");
}

function imprimirProdutos() {
    window.print();
}

/* ==========================================================
   PAGINAÇÃO (exibição — a navegação real está em configurarEventos())
========================================================== */

function atualizarPaginacao() {

    const elemento = document.getElementById("paginaAtual");

    if (elemento) {
        elemento.innerHTML = `Página ${paginaAtualProdutos + 1} de ${totalPaginasProdutos}`;
    }

}

/* ==========================================================
   CALLBACKS APÓS SALVAR / EDITAR / EXCLUIR
========================================================== */

function produtoSalvo() {
    fecharModalProduto();
    carregarProdutos();
}

function produtoAtualizado() {
    fecharModalProduto();
    carregarProdutos();
}

function produtoExcluido() {
    carregarProdutos();
}
