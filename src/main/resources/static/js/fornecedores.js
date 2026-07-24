/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: fornecedores.js
 * Descrição.....: Controle da tela de Fornecedores
 * ==========================================================
 */

let fornecedores = [];

let paginaAtual = 1;

let totalPaginas = 1;

let itensPorPagina = 10;

let fornecedorSelecionado = null;

/* ==========================================================
   INICIALIZAÇÃO
========================================================== */

document.addEventListener("DOMContentLoaded", () => {

    inicializarFornecedores();

});

/* ==========================================================
   INICIAR MÓDULO
========================================================== */

async function inicializarFornecedores(){

    configurarEventosFornecedores();

    await carregarFornecedores();

}

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventosFornecedores(){

    const pesquisa = document.getElementById("campoPesquisa");

    if(pesquisa){

        pesquisa.addEventListener(

            "input",

            aplicarFiltroFornecedores

        );

    }

    const quantidade = document.getElementById("itensPorPagina");

    if(quantidade){

        quantidade.addEventListener(

            "change",

            ()=>{

                itensPorPagina = Number(quantidade.value);

                paginaAtual = 1;

                renderizarTabelaFornecedores();

            }

        );

    }

}

/* ==========================================================
   CARREGAR FORNECEDORES
========================================================== */

async function carregarFornecedores(){

    try{

        mostrarLoadingTabela();

        fornecedores = await api.get("/fornecedores");

        calcularPaginacao();

        atualizarCardsFornecedores();

        renderizarTabelaFornecedores();

    }catch(erro){

        console.error(erro);

        Toast.erro(

            "Erro ao carregar fornecedores."

        );

    }finally{

        ocultarLoadingTabela();

    }

}

/* ==========================================================
   DASHBOARD
========================================================== */

function atualizarCardsFornecedores(){

    const total = fornecedores.length;

    const ativos = fornecedores.filter(

        fornecedor=>fornecedor.ativo

    ).length;

    const inativos = total - ativos;

    const novos = fornecedores.filter(fornecedor=>{

        const cadastro = new Date(

            fornecedor.dataCadastro

        );

        const hoje = new Date();

        return (

            cadastro.getMonth()===hoje.getMonth()

            &&

            cadastro.getFullYear()===hoje.getFullYear()

        );

    }).length;

    document.getElementById("totalFornecedores").innerText =
        total;

    document.getElementById("fornecedoresAtivos").innerText =
        ativos;

    document.getElementById("fornecedoresInativos").innerText =
        inativos;

    document.getElementById("novosFornecedores").innerText =
        novos;

}

/* ==========================================================
   PAGINAÇÃO
========================================================== */

function calcularPaginacao(){

    totalPaginas = Math.ceil(

        fornecedores.length /

        itensPorPagina

    );

    if(totalPaginas===0){

        totalPaginas = 1;

    }

}

/* ==========================================================
   LOADING
========================================================== */

function mostrarLoadingTabela(){

    document.getElementById("tbodyFornecedores").innerHTML = `

        <tr>

            <td colspan="8">

                <div class="table-loading">

                    <div class="spinner"></div>

                    <span>Carregando fornecedores...</span>

                </div>

            </td>

        </tr>

    `;

}

function ocultarLoadingTabela(){

    // Controlado automaticamente pelo render da tabela

}

/* ==========================================================
   RENDERIZAR TABELA
========================================================== */

function renderizarTabelaFornecedores(){

    const tbody = document.getElementById("tbodyFornecedores");

    tbody.innerHTML = "";

    const lista = aplicarFiltro();

    calcularPaginacaoLista(lista);

    const inicio = (paginaAtual - 1) * itensPorPagina;

    const fim = inicio + itensPorPagina;

    const pagina = lista.slice(inicio, fim);

    if(pagina.length === 0){

        tbody.innerHTML = `

            <tr>

                <td colspan="8">

                    <div class="table-empty">

                        <i class="bi bi-truck"></i>

                        <h3>Nenhum fornecedor encontrado</h3>

                        <p>Não existem fornecedores cadastrados.</p>

                    </div>

                </td>

            </tr>

        `;

        atualizarPaginacao();

        return;

    }

    pagina.forEach(fornecedor=>{

        tbody.innerHTML += montarLinhaFornecedor(fornecedor);

    });

    atualizarPaginacao();

}

/* ==========================================================
   LINHA DA TABELA
========================================================== */

function montarLinhaFornecedor(fornecedor){

    return `

        <tr>

            <td>${fornecedor.id}</td>

            <td>${fornecedor.razaoSocial}</td>

            <td>${fornecedor.nomeFantasia}</td>

            <td>${fornecedor.cnpj}</td>

            <td>${fornecedor.cidade ?? "-"}</td>

            <td>${fornecedor.telefone ?? "-"}</td>

            <td>

                ${fornecedor.ativo

                    ? '<span class="badge badge-success">Ativo</span>'

                    : '<span class="badge badge-danger">Inativo</span>'

                }

            </td>

            <td>

                <div class="table-actions">

                    <button

                        class="btn-action btn-view"

                        onclick="abrirModalVisualizarFornecedor(${fornecedor.id})"

                        title="Visualizar">

                        <i class="bi bi-eye-fill"></i>

                    </button>

                    <button

                        class="btn-action btn-edit"

                        onclick="abrirModalEditarFornecedor(${fornecedor.id})"

                        title="Editar">

                        <i class="bi bi-pencil-fill"></i>

                    </button>

                    <button

                        class="btn-action btn-delete"

                        onclick="excluirFornecedor(${fornecedor.id})"

                        title="Excluir">

                        <i class="bi bi-trash-fill"></i>

                    </button>

                </div>

            </td>

        </tr>

    `;

}

/* ==========================================================
   FORMATAR DATA
========================================================== */

function formatarData(data){

    if(!data){

        return "-";

    }

    return new Date(data).toLocaleDateString("pt-BR");

}

/* ==========================================================
   PAGINAÇÃO
========================================================== */

function atualizarPaginacao(){

    document.getElementById("paginaAtual").innerText = paginaAtual;

    document.getElementById("totalPaginas").innerText = totalPaginas;

    document.getElementById("totalRegistros").innerText = fornecedores.length;

}

/* ==========================================================
   PAGINAÇÃO DA LISTA
========================================================== */

function calcularPaginacaoLista(lista){

    totalPaginas = Math.ceil(

        lista.length /

        itensPorPagina

    );

    if(totalPaginas === 0){

        totalPaginas = 1;

    }

}

/* ==========================================================
   FILTRO
========================================================== */

function aplicarFiltro(){

    const pesquisa = document
        .getElementById("campoPesquisa")
        .value
        .toLowerCase()
        .trim();

    if(pesquisa === ""){

        return fornecedores;

    }

    return fornecedores.filter(fornecedor=>{

        return (

            (fornecedor.razaoSocial ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (fornecedor.nomeFantasia ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (fornecedor.cnpj ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (fornecedor.cidade ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (fornecedor.telefone ?? "")
                .toLowerCase()
                .includes(pesquisa)

        );

    });

}

/* ==========================================================
   FILTRO EM TEMPO REAL
========================================================== */

function aplicarFiltroFornecedores(){

    paginaAtual = 1;

    renderizarTabelaFornecedores();

}

/* ==========================================================
   ORDENAÇÃO
========================================================== */

let colunaOrdenacao = "razaoSocial";

let ordemAscendente = true;

function ordenarFornecedores(coluna){

    if(colunaOrdenacao === coluna){

        ordemAscendente = !ordemAscendente;

    }else{

        colunaOrdenacao = coluna;

        ordemAscendente = true;

    }

    fornecedores.sort((a,b)=>{

        let valorA = a[coluna] ?? "";

        let valorB = b[coluna] ?? "";

        if(typeof valorA === "string"){

            valorA = valorA.toLowerCase();

            valorB = valorB.toLowerCase();

        }

        if(valorA < valorB){

            return ordemAscendente ? -1 : 1;

        }

        if(valorA > valorB){

            return ordemAscendente ? 1 : -1;

        }

        return 0;

    });

    renderizarTabelaFornecedores();

}

/* ==========================================================
   PRIMEIRA PÁGINA
========================================================== */

function primeiraPagina(){

    paginaAtual = 1;

    renderizarTabelaFornecedores();

}

/* ==========================================================
   PÁGINA ANTERIOR
========================================================== */

function paginaAnterior(){

    if(paginaAtual > 1){

        paginaAtual--;

        renderizarTabelaFornecedores();

    }

}

/* ==========================================================
   PRÓXIMA PÁGINA
========================================================== */

function proximaPagina(){

    if(paginaAtual < totalPaginas){

        paginaAtual++;

        renderizarTabelaFornecedores();

    }

}

/* ==========================================================
   ÚLTIMA PÁGINA
========================================================== */

function ultimaPagina(){

    paginaAtual = totalPaginas;

    renderizarTabelaFornecedores();

}

/* ==========================================================
   ATUALIZAR PAGINAÇÃO
========================================================== */

function atualizarPaginacao(){

    document.getElementById("paginaAtual").innerText =
        paginaAtual;

    document.getElementById("totalPaginas").innerText =
        totalPaginas;

    document.getElementById("totalRegistros").innerText =
        fornecedores.length;

}

/* ==========================================================
   LIMPAR PESQUISA
========================================================== */

function limparPesquisa(){

    const campo = document.getElementById("campoPesquisa");

    if(campo){

        campo.value = "";

    }

    paginaAtual = 1;

    renderizarTabelaFornecedores();

}

document.getElementById("btnLimparPesquisa")
    ?.addEventListener(

        "click",

        limparPesquisa

    );

    /* ==========================================================
   EXCLUIR FORNECEDOR
========================================================== */

async function excluirFornecedor(id){

    const resposta = await Swal.fire({

        title: "Excluir fornecedor?",

        text: "Esta operação não poderá ser desfeita.",

        icon: "warning",

        showCancelButton: true,

        confirmButtonColor: "#DC2626",

        cancelButtonColor: "#64748B",

        confirmButtonText: "Excluir",

        cancelButtonText: "Cancelar"

    });

    if(!resposta.isConfirmed){

        return;

    }

    try{

        mostrarLoading();

        await api.delete(`/fornecedores/${id}`);

        Toast.sucesso(

            "Fornecedor excluído com sucesso."

        );

        await carregarFornecedores();

    }catch(erro){

        console.error(erro);

        Toast.erro(

            "Erro ao excluir fornecedor."

        );

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   EXPORTAR EXCEL
========================================================== */

function exportarExcelFornecedores(){

    const dados = fornecedores.map(fornecedor => ({

        Código: fornecedor.id,

        "Razão Social": fornecedor.razaoSocial,

        "Nome Fantasia": fornecedor.nomeFantasia,

        CNPJ: fornecedor.cnpj,

        Cidade: fornecedor.cidade,

        Telefone: fornecedor.telefone,

        Status: fornecedor.ativo ? "Ativo" : "Inativo"

    }));

    const planilha = XLSX.utils.json_to_sheet(dados);

    const workbook = XLSX.utils.book_new();

    XLSX.utils.book_append_sheet(

        workbook,

        planilha,

        "Fornecedores"

    );

    XLSX.writeFile(

        workbook,

        "fornecedores.xlsx"

    );

}

/* ==========================================================
   EXPORTAR PDF
========================================================== */

function exportarPdfFornecedores(){

    const { jsPDF } = window.jspdf;

    const pdf = new jsPDF();

    pdf.setFontSize(18);

    pdf.text(

        "Relatório de Fornecedores",

        14,

        20

    );

    const linhas = fornecedores.map(fornecedor => ([

        fornecedor.id,

        fornecedor.razaoSocial,

        fornecedor.nomeFantasia,

        fornecedor.cnpj,

        fornecedor.cidade,

        fornecedor.telefone,

        fornecedor.ativo ? "Ativo" : "Inativo"

    ]));

    pdf.autoTable({

        head:[[
            "ID",
            "Razão Social",
            "Nome Fantasia",
            "CNPJ",
            "Cidade",
            "Telefone",
            "Status"
        ]],

        body: linhas,

        startY: 30

    });

    pdf.save(

        "fornecedores.pdf"

    );

}

/* ==========================================================
   IMPRIMIR
========================================================== */

function imprimirFornecedores(){

    window.print();

}

/* ==========================================================
   ATUALIZAR
========================================================== */

async function atualizarListaFornecedores(){

    await carregarFornecedores();

}

/* ==========================================================
   EVENTOS DOS BOTÕES
========================================================== */

document
    .getElementById("btnExportarExcel")
    ?.addEventListener(

        "click",

        exportarExcelFornecedores

    );

document
    .getElementById("btnExportarPdf")
    ?.addEventListener(

        "click",

        exportarPdfFornecedores

    );

document
    .getElementById("btnImprimir")
    ?.addEventListener(

        "click",

        imprimirFornecedores

    );

document
    .getElementById("btnNovoFornecedor")
    ?.addEventListener(

        "click",

        abrirModalFornecedor

    );

    /* ==========================================================
   LIMPAR FILTROS
========================================================== */

function limparFiltrosFornecedores(){

    const campoPesquisa = document.getElementById("campoPesquisa");

    if(campoPesquisa){

        campoPesquisa.value = "";

    }

    paginaAtual = 1;

    renderizarTabelaFornecedores();

}

/* ==========================================================
   ATUALIZAR LISTA
========================================================== */

async function atualizarFornecedores(){

    try{

        await carregarFornecedores();

    }catch(erro){

        console.error(erro);

    }

}

/* ==========================================================
   EVENTOS DO MODAL
========================================================== */

document.addEventListener(

    "fornecedorSalvo",

    async()=>{

        await atualizarFornecedores();

    }

);

document.addEventListener(

    "fornecedorAtualizado",

    async()=>{

        await atualizarFornecedores();

    }

);

/* ==========================================================
   TRATAMENTO GLOBAL
========================================================== */

window.addEventListener(

    "unhandledrejection",

    (event)=>{

        console.error(event.reason);

        Toast.erro(

            "Ocorreu um erro inesperado."

        );

    }

);

/* ==========================================================
   UTILITÁRIOS
========================================================== */

function obterFornecedor(id){

    return fornecedores.find(

        fornecedor => fornecedor.id === id

    );

}

function fornecedorExiste(id){

    return obterFornecedor(id) !== undefined;

}

function totalFornecedores(){

    return fornecedores.length;

}

/* ==========================================================
   REFRESH AUTOMÁTICO
========================================================== */

setInterval(

    ()=>{

        carregarFornecedores();

    },

    300000

);

/* ==========================================================
   FIM DO ARQUIVO
========================================================== */

/*

███████╗ ██████╗ ██████╗ ███╗   ██╗███████╗ ██████╗███████╗██████╗
██╔════╝██╔═══██╗██╔══██╗████╗  ██║██╔════╝██╔════╝██╔════╝██╔══██╗
█████╗  ██║   ██║██████╔╝██╔██╗ ██║█████╗  ██║     █████╗  ██║  ██║
██╔══╝  ██║   ██║██╔══██╗██║╚██╗██║██╔══╝  ██║     ██╔══╝  ██║  ██║
██║     ╚██████╔╝██║  ██║██║ ╚████║███████╗╚██████╗███████╗██████╔╝
╚═╝      ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝╚══════╝╚═════╝

fornecedores.js concluído.

*/