/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: produtos.js
 * Descrição.....: Controle da tela de Produtos
 * ==========================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    inicializarProdutos();

});

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

    document
        .getElementById("btnNovo")
        .addEventListener("click", abrirModalNovoProduto);

    document
        .getElementById("btnPesquisar")
        .addEventListener("click", pesquisarProdutos);

    document
        .getElementById("btnAtualizar")
        .addEventListener("click", carregarProdutos);

    document
        .getElementById("btnLimpar")
        .addEventListener("click", limparFiltros);

    document
        .getElementById("btnExcel")
        .addEventListener("click", exportarExcel);

    document
        .getElementById("btnPdf")
        .addEventListener("click", exportarPDF);

    document
        .getElementById("btnImprimir")
        .addEventListener("click", imprimirProdutos);

}

/* ==========================================================
   LISTAR PRODUTOS
========================================================== */

async function carregarProdutos() {

    try {

        mostrarLoading();

        const resposta = await api.get("/produtos");

        const produtos = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        preencherTabela(produtos);

        atualizarCards(produtos);

        atualizarResumo(produtos);

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao carregar produtos.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   CARREGAR CATEGORIAS
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
========================================================== */

async function pesquisarProdutos() {

    const pesquisa = document
        .getElementById("txtPesquisar")
        .value
        .trim();

    const categoria = document
        .getElementById("filtroCategoria")
        .value;

    const situacao = document
        .getElementById("filtroSituacao")
        .value;

    try {

        mostrarLoading();

        const produtos = await api.get(

            `/produtos?pesquisa=${encodeURIComponent(pesquisa)}&categoria=${categoria}&situacao=${situacao}`

        );

        preencherTabela(produtos);

        atualizarCards(produtos);

        atualizarResumo(produtos);

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao pesquisar produtos.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   LIMPAR FILTROS
========================================================== */

function limparFiltros() {

    document.getElementById("txtPesquisar").value = "";

    document.getElementById("filtroCategoria").value = "";

    document.getElementById("filtroSituacao").value = "";

    carregarProdutos();

}

/* ==========================================================
   ATUALIZAR CARDS
========================================================== */

function atualizarCards(produtos) {

    const ativos = produtos.filter(p => p.ativo);

    const inativos = produtos.filter(p => !p.ativo);

    const estoqueBaixo = produtos.filter(

        p => p.estoqueAtual <= p.estoqueMinimo

    );

    document.getElementById("totalProdutos").textContent =
        produtos.length;

    document.getElementById("produtosAtivos").textContent =
        ativos.length;

    document.getElementById("produtosInativos").textContent =
        inativos.length;

    document.getElementById("estoqueBaixo").textContent =
        estoqueBaixo.length;

}

/* ==========================================================
   RESUMO
========================================================== */

function atualizarResumo(produtos){

    document.getElementById("qtdeRegistros").textContent =
        produtos.length;

    document.getElementById("registroInicial").textContent =
        produtos.length > 0 ? 1 : 0;

    document.getElementById("registroFinal").textContent =
        produtos.length;

    document.getElementById("registroTotal").textContent =
        produtos.length;

    document.getElementById("ultimaAtualizacao").textContent =
        new Date().toLocaleString("pt-BR");

}

/* ==========================================================
   PREENCHER TABELA
========================================================== */

function preencherTabela(produtos){

    const tbody =
        document.getElementById("tbodyProdutos");

    tbody.innerHTML = "";

    if(produtos.length === 0){

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

function criarLinhaProduto(produto){

    let badgeSituacao = produto.ativo
        ? '<span class="badge badge-ativo">Ativo</span>'
        : '<span class="badge badge-inativo">Inativo</span>';

    let estoqueClasse = "";

    if(produto.estoqueAtual <= 0){

        estoqueClasse = "estoque-critico";

    }else if(produto.estoqueAtual <= produto.estoqueMinimo){

        estoqueClasse = "estoque-baixo";

    }else{

        estoqueClasse = "estoque-normal";

    }

    return `

        <tr>

            <td>${produto.id}</td>

            <td>${produto.codigo}</td>

            <td>${produto.descricao}</td>

            <td>${produto.categoria}</td>

            <td>${produto.unidade}</td>

            <td class="${estoqueClasse}">

                ${produto.estoqueAtual}

            </td>

            <td>

                ${formatarMoeda(produto.valorVenda)}

            </td>

            <td>

                ${badgeSituacao}

            </td>

            <td style="text-align:center;">

                <button
                    class="btn-action btn-view"
                    title="Visualizar"
                    onclick="visualizarProduto(${produto.id})">

                    <i class="bi bi-eye-fill"></i>

                </button>

                <button
                    class="btn-action btn-edit"
                    title="Editar"
                    onclick="editarProduto(${produto.id})">

                    <i class="bi bi-pencil-square"></i>

                </button>

                <button
                    class="btn-action btn-delete"
                    title="Excluir"
                    onclick="excluirProduto(${produto.id})">

                    <i class="bi bi-trash-fill"></i>

                </button>

            </td>

        </tr>

    `;

}

/* ==========================================================
   FORMATAR MOEDA
========================================================== */

function formatarMoeda(valor){

    return Number(valor).toLocaleString("pt-BR",{

        style:"currency",

        currency:"BRL"

    });

}

/* ==========================================================
   NOVO PRODUTO
========================================================== */

function abrirModalNovoProduto(){

    abrirModalProduto();

}

/* ==========================================================
   VISUALIZAR
========================================================== */

function visualizarProduto(id){

    abrirModalVisualizarProduto(id);

}

/* ==========================================================
   EDITAR
========================================================== */

function editarProduto(id){

    abrirModalEditarProduto(id);

}

/* ==========================================================
   EXCLUIR PRODUTO
========================================================== */

async function excluirProduto(id){

    const confirmar = confirm(
        "Deseja realmente excluir este produto?"
    );

    if(!confirmar){

        return;

    }

    try{

        mostrarLoading();

        await api.delete(`/produtos/${id}`);

        Toast.sucesso("Produto excluído com sucesso.");

        carregarProdutos();

    }catch(erro){

        console.error(erro);

        Toast.erro("Erro ao excluir produto.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   EXPORTAR EXCEL
========================================================== */

function exportarExcel(){

    Toast.info("Exportação para Excel será implementada.");

}

/* ==========================================================
   EXPORTAR PDF
========================================================== */

function exportarPDF(){

    Toast.info("Exportação para PDF será implementada.");

}

/* ==========================================================
   IMPRIMIR
========================================================== */

function imprimirProdutos(){

    window.print();

}

/* ==========================================================
   PAGINAÇÃO
========================================================== */

let paginaAtual = 1;

let totalPaginas = 1;

function atualizarPaginacao(){

    document.getElementById("paginaAtual").innerHTML =

        `Página ${paginaAtual} de ${totalPaginas}`;

}

document
.getElementById("btnPrimeiraPagina")
.addEventListener("click",()=>{

    paginaAtual=1;

    atualizarPaginacao();

});

document
.getElementById("btnPaginaAnterior")
.addEventListener("click",()=>{

    if(paginaAtual>1){

        paginaAtual--;

        atualizarPaginacao();

    }

});

document
.getElementById("btnProximaPagina")
.addEventListener("click",()=>{

    if(paginaAtual<totalPaginas){

        paginaAtual++;

        atualizarPaginacao();

    }

});

document
.getElementById("btnUltimaPagina")
.addEventListener("click",()=>{

    paginaAtual=totalPaginas;

    atualizarPaginacao();

});

/* ==========================================================
   REFRESH
========================================================== */

function atualizarTelaProdutos(){

    carregarProdutos();

}

/* ==========================================================
   CALLBACK APÓS SALVAR
========================================================== */

function produtoSalvo(){

    fecharModalProduto();

    carregarProdutos();

}

/* ==========================================================
   CALLBACK APÓS EDITAR
========================================================== */

function produtoAtualizado(){

    fecharModalProduto();

    carregarProdutos();

}

/* ==========================================================
   CALLBACK APÓS EXCLUIR
========================================================== */

function produtoExcluido(){

    carregarProdutos();

}

/* ==========================================================
   FIM DO ARQUIVO
========================================================== */

