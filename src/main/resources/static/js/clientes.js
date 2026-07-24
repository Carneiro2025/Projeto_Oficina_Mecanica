/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: clientes.js
 * Descrição.....: Controle da tela de Clientes
 * ==========================================================
 */

let clientes = [];

let paginaAtual = 1;

let totalPaginas = 1;

let itensPorPagina = 10;

let clienteSelecionado = null;


/* ==========================================================
   INICIALIZAÇÃO
========================================================== */

document.addEventListener("DOMContentLoaded",()=>{

    inicializarClientes();

});


/* ==========================================================
   INICIAR MÓDULO
========================================================== */

async function inicializarClientes(){

    configurarEventosClientes();

    await carregarClientes();

}


/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventosClientes(){


    const pesquisa = document.getElementById("campoPesquisa");


    if(pesquisa){

        pesquisa.addEventListener(

            "input",

            aplicarFiltroClientes

        );

    }


    const quantidade = document.getElementById("itensPorPagina");


    if(quantidade){

        quantidade.addEventListener(

            "change",

            ()=>{

                itensPorPagina =
                    Number(quantidade.value);

                paginaAtual = 1;

                renderizarTabelaClientes();

            }

        );

    }

}

/* ==========================================================
   BUSCAR CLIENTES
========================================================== */

async function carregarClientes(){

    try{

        mostrarLoadingTabela();


        clientes = await api.get("/clientes");


        calcularPaginacao();


        renderizarTabelaClientes();


        atualizarCardsClientes();


    }catch(erro){


        console.error(erro);


        Toast.erro(
            "Erro ao carregar clientes."
        );


    }finally{


        ocultarLoadingTabela();


    }

}

/* ==========================================================
   ATUALIZAR CARDS
========================================================== */

function atualizarCardsClientes(){


    const total =
        clientes.length;


    const ativos =
        clientes.filter(
            cliente=>cliente.ativo
        ).length;


    const inativos =
        total - ativos;


    const novos =
        clientes.filter(cliente=>{


            const data =
                new Date(cliente.dataCadastro);


            const hoje =
                new Date();


            return (

                data.getMonth()
                ===
                hoje.getMonth()

                &&

                data.getFullYear()
                ===
                hoje.getFullYear()

            );


        }).length;



    document.getElementById("totalClientes").innerText =
        total;


    document.getElementById("clientesAtivos").innerText =
        ativos;


    document.getElementById("clientesInativos").innerText =
        inativos;


    document.getElementById("novosClientes").innerText =
        novos;


}

/* ==========================================================
   CALCULAR PAGINAÇÃO
========================================================== */

function calcularPaginacao(){

    totalPaginas = Math.ceil(

        clientes.length /
        itensPorPagina

    );


    if(totalPaginas===0){

        totalPaginas=1;

    }

}

/* ==========================================================
   RENDERIZAR TABELA
========================================================== */

function renderizarTabelaClientes(){

    const tbody = document.getElementById("tbodyClientes");

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

                        <i class="bi bi-people"></i>

                        <h3>Nenhum cliente encontrado</h3>

                        <p>Não existem clientes cadastrados.</p>

                    </div>

                </td>

            </tr>

        `;

        atualizarPaginacao();

        return;

    }

    pagina.forEach(cliente=>{

        tbody.innerHTML += montarLinhaCliente(cliente);

    });

    atualizarPaginacao();

}

/* ==========================================================
   LINHA DA TABELA
========================================================== */

function montarLinhaCliente(cliente){

    return `

        <tr>

            <td>${cliente.id}</td>

            <td>${cliente.nome}</td>

            <td>${cliente.cpf}</td>

            <td>${cliente.telefone ?? "-"}</td>

            <td>${cliente.cidade ?? "-"}</td>

            <td>

                ${cliente.ativo

                    ? '<span class="badge badge-success">Ativo</span>'

                    : '<span class="badge badge-danger">Inativo</span>'

                }

            </td>

            <td>

                ${formatarData(cliente.dataCadastro)}

            </td>

            <td>

                <div class="table-actions">

                    <button

                        class="btn-action btn-view"

                        onclick="abrirModalVisualizarCliente(${cliente.id})"

                        title="Visualizar">

                        <i class="bi bi-eye-fill"></i>

                    </button>

                    <button

                        class="btn-action btn-edit"

                        onclick="abrirModalEditarCliente(${cliente.id})"

                        title="Editar">

                        <i class="bi bi-pencil-fill"></i>

                    </button>

                    <button

                        class="btn-action btn-delete"

                        onclick="excluirCliente(${cliente.id})"

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

    return new Date(data)

        .toLocaleDateString("pt-BR");

}

/* ==========================================================
   LOADING
========================================================== */

function mostrarLoadingTabela(){

    document.getElementById("tbodyClientes").innerHTML = `

        <tr>

            <td colspan="8">

                <div class="table-loading">

                    <div class="spinner"></div>

                    <span>Carregando clientes...</span>

                </div>

            </td>

        </tr>

    `;

}

function ocultarLoadingTabela(){

}

/* ==========================================================
   PAGINAÇÃO
========================================================== */

function atualizarPaginacao(){

    document.getElementById("paginaAtual")

        .innerText = paginaAtual;

    document.getElementById("totalPaginas")

        .innerText = totalPaginas;

}

/* ==========================================================
   PESQUISA
========================================================== */

function aplicarFiltro(){

    const pesquisa = document
        .getElementById("campoPesquisa")
        .value
        .toLowerCase()
        .trim();

    if(pesquisa===""){

        return clientes;

    }

    return clientes.filter(cliente=>{

        return (

            (cliente.nome ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (cliente.cpf ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (cliente.telefone ?? "")
                .toLowerCase()
                .includes(pesquisa)

            ||

            (cliente.cidade ?? "")
                .toLowerCase()
                .includes(pesquisa)

        );

    });

}

/* ==========================================================
   PESQUISA EM TEMPO REAL
========================================================== */

function aplicarFiltroClientes(){

    paginaAtual = 1;

    renderizarTabelaClientes();

}

/* ==========================================================
   ORDENAÇÃO
========================================================== */

let colunaOrdenacao = "nome";

let ordemAscendente = true;

function ordenarClientes(coluna){

    if(colunaOrdenacao===coluna){

        ordemAscendente=!ordemAscendente;

    }else{

        colunaOrdenacao=coluna;

        ordemAscendente=true;

    }

    clientes.sort((a,b)=>{

        let valorA = a[coluna] ?? "";

        let valorB = b[coluna] ?? "";

        if(typeof valorA==="string"){

            valorA = valorA.toLowerCase();

            valorB = valorB.toLowerCase();

        }

        if(valorA<valorB){

            return ordemAscendente ? -1 : 1;

        }

        if(valorA>valorB){

            return ordemAscendente ? 1 : -1;

        }

        return 0;

    });

    renderizarTabelaClientes();

}

/* ==========================================================
   PAGINAÇÃO
========================================================== */

function calcularPaginacaoLista(lista){

    totalPaginas = Math.ceil(

        lista.length /

        itensPorPagina

    );

    if(totalPaginas===0){

        totalPaginas=1;

    }

}

/* ==========================================================
   PRIMEIRA
========================================================== */

function primeiraPagina(){

    paginaAtual=1;

    renderizarTabelaClientes();

}

/* ==========================================================
   ANTERIOR
========================================================== */

function paginaAnterior(){

    if(paginaAtual>1){

        paginaAtual--;

        renderizarTabelaClientes();

    }

}

/* ==========================================================
   PRÓXIMA
========================================================== */

function proximaPagina(){

    if(paginaAtual<totalPaginas){

        paginaAtual++;

        renderizarTabelaClientes();

    }

}

/* ==========================================================
   ÚLTIMA
========================================================== */

function ultimaPagina(){

    paginaAtual=totalPaginas;

    renderizarTabelaClientes();

}

/* ==========================================================
   INFORMAÇÕES DA PAGINAÇÃO
========================================================== */

function atualizarPaginacao(){

    document
        .getElementById("paginaAtual")
        .innerText = paginaAtual;

    document
        .getElementById("totalPaginas")
        .innerText = totalPaginas;

    document
        .getElementById("totalRegistros")
        .innerText = clientes.length;

}

/* ==========================================================
   EXCLUIR CLIENTE
========================================================== */

async function excluirCliente(id){

    const confirmar = await Swal.fire({

        title:"Excluir Cliente?",

        text:"Esta operação não poderá ser desfeita.",

        icon:"warning",

        showCancelButton:true,

        confirmButtonColor:"#DC2626",

        cancelButtonColor:"#64748B",

        confirmButtonText:"Excluir",

        cancelButtonText:"Cancelar"

    });

    if(!confirmar.isConfirmed){

        return;

    }

    try{

        mostrarLoading();

        await api.delete(`/clientes/${id}`);

        Toast.sucesso("Cliente excluído com sucesso.");

        await carregarClientes();

    }catch(erro){

        console.error(erro);

        Toast.erro("Erro ao excluir cliente.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   EXPORTAR EXCEL
========================================================== */

function exportarExcelClientes(){

    const dados = clientes.map(cliente=>({

        Código: cliente.id,

        Nome: cliente.nome,

        CPF: cliente.cpf,

        Telefone: cliente.telefone,

        Cidade: cliente.cidade,

        Email: cliente.email,

        Situação: cliente.ativo ? "Ativo" : "Inativo"

    }));

    const planilha = XLSX.utils.json_to_sheet(dados);

    const workbook = XLSX.utils.book_new();

    XLSX.utils.book_append_sheet(

        workbook,

        planilha,

        "Clientes"

    );

    XLSX.writeFile(

        workbook,

        "clientes.xlsx"

    );

}

/* ==========================================================
   EXPORTAR PDF
========================================================== */

function exportarPdfClientes(){

    const { jsPDF } = window.jspdf;

    const pdf = new jsPDF();

    pdf.setFontSize(18);

    pdf.text(

        "Relatório de Clientes",

        14,

        20

    );

    const linhas = clientes.map(cliente=>([

        cliente.id,

        cliente.nome,

        cliente.cpf,

        cliente.telefone,

        cliente.cidade,

        cliente.ativo

            ? "Ativo"

            : "Inativo"

    ]));

    pdf.autoTable({

        head:[["ID","Nome","CPF","Telefone","Cidade","Status"]],

        body:linhas,

        startY:30

    });

    pdf.save("clientes.pdf");

}

/* ==========================================================
   IMPRIMIR
========================================================== */

function imprimirClientes(){

    window.print();

}

/* ==========================================================
   ATUALIZAR
========================================================== */

async function atualizarListaClientes(){

    await carregarClientes();

}

/* ==========================================================
   BOTÕES
========================================================== */

document
    .getElementById("btnExportarExcel")
    ?.addEventListener(

        "click",

        exportarExcelClientes

    );

document
    .getElementById("btnExportarPdf")
    ?.addEventListener(

        "click",

        exportarPdfClientes

    );

document
    .getElementById("btnImprimir")
    ?.addEventListener(

        "click",

        imprimirClientes

    );

document
    .getElementById("btnNovoCliente")
    ?.addEventListener(

        "click",

        abrirModalCliente

    );

    /* ==========================================================
   LIMPAR PESQUISA
========================================================== */

function limparPesquisaClientes(){

    const campo = document.getElementById("campoPesquisa");

    if(campo){

        campo.value = "";

    }

    paginaAtual = 1;

    renderizarTabelaClientes();

}

/* ==========================================================
   ATUALIZAR LISTA
========================================================== */

async function atualizarClientes(){

    try{

        await carregarClientes();

    }catch(erro){

        console.error(erro);

    }

}

/* ==========================================================
   RECARREGAR APÓS MODAL
========================================================== */

document.addEventListener("clienteSalvo",async()=>{

    await atualizarClientes();

});

document.addEventListener("clienteAtualizado",async()=>{

    await atualizarClientes();

});

/* ==========================================================
   TRATAMENTO GLOBAL
========================================================== */

window.addEventListener("unhandledrejection",(event)=>{

    console.error(event.reason);

    Toast.erro(

        "Ocorreu um erro inesperado."

    );

});

/* ==========================================================
   UTILITÁRIOS
========================================================== */

function obterCliente(id){

    return clientes.find(

        cliente=>cliente.id===id

    );

}

function clienteExiste(id){

    return obterCliente(id)!==undefined;

}

function totalClientes(){

    return clientes.length;

}

/* ==========================================================
   REFRESH
========================================================== */

setInterval(()=>{

    carregarClientes();

},300000);

/* ==========================================================
   FIM DO ARQUIVO
========================================================== */

/*

 ██████╗██╗     ██╗███████╗███╗   ██╗████████╗███████╗███████╗
██╔════╝██║     ██║██╔════╝████╗  ██║╚══██╔══╝██╔════╝██╔════╝
██║     ██║     ██║█████╗  ██╔██╗ ██║   ██║   █████╗  ███████╗
██║     ██║     ██║██╔══╝  ██║╚██╗██║   ██║   ██╔══╝  ╚════██║
╚██████╗███████╗██║███████╗██║ ╚████║   ██║   ███████╗███████║
 ╚═════╝╚══════╝╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚══════╝

clientes.js concluído.

*/