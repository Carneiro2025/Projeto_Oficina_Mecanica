/**********************************************************************
 * MODAL ORDEM DE SERVIÇO
 * Projeto Oficina Mecânica
 * modal-os.js
 **********************************************************************/

/**********************************************************************
 * CONFIGURAÇÕES
 **********************************************************************/

const API_BASE = "/api";

const ENDPOINTS = {

    ORDEM_SERVICO: `${API_BASE}/ordens-servico`,
    CLIENTES: `${API_BASE}/clientes`,
    VEICULOS: `${API_BASE}/veiculos`,
    PRODUTOS: `${API_BASE}/produtos`,
    SERVICOS: `${API_BASE}/servicos`,
    USUARIOS: `${API_BASE}/usuarios`,
    HISTORICO: `${API_BASE}/historico-os`,
    UPLOAD: `${API_BASE}/upload`

};

/**********************************************************************
 * ELEMENTOS
 **********************************************************************/

const modalOS = document.getElementById("modalOS");

const formularioOS = document.getElementById("formOS");

const btnSalvarOS = document.getElementById("btnSalvarOS");
const btnFinalizarOS = document.getElementById("btnFinalizarOS");
const btnImprimirOS = document.getElementById("btnImprimirOS");
const btnGerarPdfOS = document.getElementById("btnGerarPdfOS");

const btnAdicionarServico = document.getElementById("btnAdicionarServico");
const btnAdicionarProduto = document.getElementById("btnAdicionarProduto");

const btnPesquisarCliente = document.getElementById("btnPesquisarClienteOS");

const progressoOS = document.getElementById("progressoOS");

/**********************************************************************
 * ESTADO DA TELA
 **********************************************************************/

let ordemServicoAtual = null;

let clienteSelecionado = null;

let veiculoSelecionado = null;

let listaServicos = [];

let listaProdutos = [];

let listaArquivos = [];

let historicoOS = [];

let editando = false;

/**********************************************************************
 * TOKEN
 **********************************************************************/

function obterToken() {

    return localStorage.getItem("token");

}

/**********************************************************************
 * CABEÇALHO
 **********************************************************************/

function cabecalhoJson() {

    return {

        "Content-Type": "application/json",

        "Authorization": `Bearer ${obterToken()}`

    };

}

/**********************************************************************
 * FETCH PADRÃO
 **********************************************************************/

async function requisicao(url, metodo = "GET", body = null) {

    const config = {

        method: metodo,

        headers: cabecalhoJson()

    };

    if (body != null) {

        config.body = JSON.stringify(body);

    }

    const response = await fetch(url, config);

    if (!response.ok) {

        throw new Error("Erro na comunicação com servidor.");

    }

    if (response.status === 204) {

        return null;

    }

    return await response.json();

}

/**********************************************************************
 * LOADING
 **********************************************************************/

function mostrarLoading() {

    document
        .querySelector(".loading-overlay")
        ?.classList
        .remove("hidden");

}

function esconderLoading() {

    document
        .querySelector(".loading-overlay")
        ?.classList
        .add("hidden");

}

/**********************************************************************
 * TOAST
 **********************************************************************/

function sucesso(mensagem){

    Swal.fire({

        icon:"success",

        title:"Sucesso",

        text:mensagem,

        timer:1800,

        showConfirmButton:false

    });

}

function erro(mensagem){

    Swal.fire({

        icon:"error",

        title:"Erro",

        text:mensagem

    });

}

function aviso(mensagem){

    Swal.fire({

        icon:"warning",

        title:"Atenção",

        text:mensagem

    });

}

/**********************************************************************
 * MODAL
 **********************************************************************/

function abrirModalNovaOS(){

    limparFormulario();

    editando=false;

    ordemServicoAtual=null;

    bootstrap.Modal
        .getOrCreateInstance(modalOS)
        .show();

}

function abrirModalEditarOS(os){

    ordemServicoAtual=os.id;

    editando=true;

    preencherFormulario(os);

    bootstrap.Modal
        .getOrCreateInstance(modalOS)
        .show();

}

function fecharModal(){

    bootstrap.Modal
        .getInstance(modalOS)
        .hide();

}

/**********************************************************************
 * LIMPEZA
 **********************************************************************/

function limparFormulario(){

    formularioOS.reset();

    clienteSelecionado=null;

    veiculoSelecionado=null;

    listaServicos=[];

    listaProdutos=[];

    listaArquivos=[];

    historicoOS=[];

    atualizarTabelaServicos();

    atualizarTabelaProdutos();

    atualizarTotais();

    atualizarProgresso();

}

/**********************************************************************
 * SALVAR
 **********************************************************************/

async function salvarOS(){

    try{

        mostrarLoading();

        const dto=montarDTO();

        if(editando){

            await requisicao(

                `${ENDPOINTS.ORDEM_SERVICO}/${ordemServicoAtual}`,

                "PUT",

                dto

            );

            sucesso("Ordem de Serviço atualizada.");

        }

        else{

            const os=await requisicao(

                ENDPOINTS.ORDEM_SERVICO,

                "POST",

                dto

            );

            ordemServicoAtual=os.id;

            editando=true;

            sucesso("Ordem de Serviço criada.");

        }

    }

    catch(e){

        erro(e.message);

    }

    finally{

        esconderLoading();

    }

}

/**********************************************************************
 * DTO
 **********************************************************************/

function montarDTO(){

    return{

        clienteId:clienteSelecionado?.id,

        veiculoId:veiculoSelecionado?.id,

        status:

            document
                .getElementById("statusOS")
                .value,

        prioridade:

            document
                .getElementById("prioridadeOS")
                .value,

        garantia:

            document
                .getElementById("garantiaOS")
                .value==="true",

        quilometragem:

            Number(

                document
                    .getElementById("quilometragem")
                    .value

            ),

        relatoCliente:

            document
                .getElementById("relatoCliente")
                .value,

        observacaoCliente:

            document
                .getElementById("observacaoCliente")
                .value,

        observacaoInterna:

            document
                .getElementById("observacaoInterna")
                .value,

        servicos:listaServicos,

        produtos:listaProdutos

    };

}

/**********************************************************************
 * CLIENTES
 **********************************************************************/

async function pesquisarClientes(filtro = "") {

    try {

        mostrarLoading();

        const clientes = await requisicao(

            `${ENDPOINTS.CLIENTES}?pesquisa=${encodeURIComponent(filtro)}`

        );

        preencherTabelaPesquisaClientes(clientes);

    } catch (e) {

        erro(e.message);

    } finally {

        esconderLoading();

    }

}

function preencherTabelaPesquisaClientes(clientes) {

    const tbody = document.getElementById("listaPesquisaClientes");

    if (!tbody) return;

    tbody.innerHTML = "";

    clientes.forEach(cliente => {

        const tr = document.createElement("tr");

        tr.innerHTML = `

            <td>${cliente.id}</td>
            <td>${cliente.nome}</td>
            <td>${cliente.documento ?? ""}</td>
            <td>${cliente.telefone ?? ""}</td>

            <td>

                <button
                    class="btn btn-primary btn-sm selecionar-cliente"
                    data-id="${cliente.id}">

                    Selecionar

                </button>

            </td>

        `;

        tbody.appendChild(tr);

    });

}

async function selecionarCliente(id){

    try{

        mostrarLoading();

        clienteSelecionado = await requisicao(

            `${ENDPOINTS.CLIENTES}/${id}`

        );

        preencherCliente();

        await carregarVeiculosCliente(id);

        atualizarProgresso();

    }

    catch(e){

        erro(e.message);

    }

    finally{

        esconderLoading();

    }

}

function preencherCliente(){

    document.getElementById("clienteId").value =
        clienteSelecionado.id ?? "";

    document.getElementById("clienteNome").value =
        clienteSelecionado.nome ?? "";

    document.getElementById("clienteDocumento").value =
        clienteSelecionado.documento ?? "";

    document.getElementById("clienteTelefone").value =
        clienteSelecionado.telefone ?? "";

    document.getElementById("clienteEndereco").value =
        clienteSelecionado.endereco ?? "";

    document.getElementById("clienteCidade").value =
        clienteSelecionado.cidade ?? "";

    document.getElementById("clienteEmail").value =
        clienteSelecionado.email ?? "";

}

document.addEventListener("click",function(e){

    if(e.target.classList.contains("selecionar-cliente")){

        selecionarCliente(e.target.dataset.id);

    }

});

/**********************************************************************
 * VEÍCULOS
 **********************************************************************/

async function carregarVeiculosCliente(clienteId){

    const select=document.getElementById("veiculoSelecionado");

    select.innerHTML="";

    const lista=await requisicao(

        `${ENDPOINTS.CLIENTES}/${clienteId}/veiculos`

    );

    const vazio=document.createElement("option");

    vazio.value="";

    vazio.textContent="Selecione...";

    select.appendChild(vazio);

    lista.forEach(v=>{

        const option=document.createElement("option");

        option.value=v.id;

        option.textContent=`${v.placa} - ${v.modelo}`;

        select.appendChild(option);

    });

}

document
.getElementById("veiculoSelecionado")
.addEventListener("change",async function(){

    if(!this.value){

        return;

    }

    await carregarVeiculo(this.value);

});

async function carregarVeiculo(id){

    try{

        mostrarLoading();

        veiculoSelecionado=await requisicao(

            `${ENDPOINTS.VEICULOS}/${id}`

        );

        preencherVeiculo();

        atualizarProgresso();

    }

    catch(e){

        erro(e.message);

    }

    finally{

        esconderLoading();

    }

}

function preencherVeiculo(){

    document.getElementById("veiculoId").value=
        veiculoSelecionado.id;

    document.getElementById("placaVeiculo").value=
        veiculoSelecionado.placa;

    document.getElementById("marcaVeiculo").value=
        veiculoSelecionado.marca;

    document.getElementById("modeloVeiculo").value=
        veiculoSelecionado.modelo;

    document.getElementById("anoVeiculo").value=
        veiculoSelecionado.ano;

    document.getElementById("corVeiculo").value=
        veiculoSelecionado.cor;

    document.getElementById("combustivelVeiculo").value=
        veiculoSelecionado.combustivel;

    document.getElementById("cambioVeiculo").value=
        veiculoSelecionado.cambio;

    document.getElementById("renavamVeiculo").value=
        veiculoSelecionado.renavam;

    document.getElementById("chassiVeiculo").value=
        veiculoSelecionado.chassi;

    document.getElementById("motorVeiculo").value=
        veiculoSelecionado.motor;

    document.getElementById("kmAtualVeiculo").value=
        veiculoSelecionado.quilometragem;

}

/**********************************************************************
 * SERVIÇOS
 **********************************************************************/

async function pesquisarServicos(filtro=""){

    return await requisicao(

        `${ENDPOINTS.SERVICOS}?pesquisa=${encodeURIComponent(filtro)}`

    );

}

function adicionarServico(servico){

    listaServicos.push({

        id:servico.id,

        descricao:servico.nome,

        quantidade:1,

        valorUnitario:Number(servico.valor),

        desconto:0,

        subtotal:Number(servico.valor),

        mecanico:null

    });

    atualizarTabelaServicos();

    atualizarTotais();

    atualizarProgresso();

}

function removerServico(index){

    listaServicos.splice(index,1);

    atualizarTabelaServicos();

    atualizarTotais();

    atualizarProgresso();

}

function atualizarTabelaServicos(){

    const tbody=document.getElementById("listaServicosOS");

    if(!tbody) return;

    tbody.innerHTML="";

    listaServicos.forEach((item,index)=>{

        const tr=document.createElement("tr");

        tr.innerHTML=`

            <td>${index+1}</td>

            <td>${item.descricao}</td>

            <td>

                <input

                    type="number"

                    class="form-control qtd-servico"

                    data-index="${index}"

                    value="${item.quantidade}"

                    min="1">

            </td>

            <td>

                ${formatarMoeda(item.valorUnitario)}

            </td>

            <td>

                ${formatarMoeda(item.desconto)}

            </td>

            <td>

                ${formatarMoeda(item.subtotal)}

            </td>

            <td>

                -

            </td>

            <td>

                <button

                    class="btn btn-danger btn-sm excluir-servico"

                    data-index="${index}">

                    <i class="bi bi-trash"></i>

                </button>

            </td>

        `;

        tbody.appendChild(tr);

    });

}

/**********************************************************************
 * EVENTOS DOS SERVIÇOS
 **********************************************************************/

document.addEventListener("click",function(e){

    if(e.target.closest(".excluir-servico")){

        removerServico(

            e.target.closest(".excluir-servico").dataset.index

        );

    }

});

document.addEventListener("change",function(e){

    if(e.target.classList.contains("qtd-servico")){

        const index=e.target.dataset.index;

        const quantidade=Number(e.target.value);

        listaServicos[index].quantidade=quantidade;

        listaServicos[index].subtotal=

            (listaServicos[index].valorUnitario*quantidade)

            -

            listaServicos[index].desconto;

        atualizarTabelaServicos();

        atualizarTotais();

    }

});

/**********************************************************************
 * PRODUTOS
 **********************************************************************/

async function pesquisarProdutos(filtro = "") {

    try {

        return await requisicao(

            `${ENDPOINTS.PRODUTOS}?pesquisa=${encodeURIComponent(filtro)}`

        );

    }

    catch (e) {

        erro(e.message);

        return [];

    }

}

function adicionarProduto(produto) {

    const existente = listaProdutos.find(p => p.id === produto.id);

    if (existente) {

        existente.quantidade++;

        existente.subtotal =
            (existente.quantidade * existente.valorUnitario)
            - existente.desconto;

    }

    else {

        listaProdutos.push({

            id: produto.id,

            codigo: produto.codigo,

            descricao: produto.nome,

            quantidade: 1,

            estoque: produto.estoque,

            valorUnitario: Number(produto.precoVenda),

            desconto: 0,

            subtotal: Number(produto.precoVenda)

        });

    }

    atualizarTabelaProdutos();

    atualizarTotais();

    atualizarProgresso();

}

function removerProduto(index) {

    listaProdutos.splice(index, 1);

    atualizarTabelaProdutos();

    atualizarTotais();

    atualizarProgresso();

}

function atualizarTabelaProdutos() {

    const tbody = document.getElementById("listaProdutosOS");

    if (!tbody) return;

    tbody.innerHTML = "";

    listaProdutos.forEach((produto, index) => {

        const tr = document.createElement("tr");

        tr.innerHTML = `

            <td>${produto.codigo ?? ""}</td>

            <td>${produto.descricao}</td>

            <td>

                <input

                    type="number"

                    min="1"

                    class="form-control qtd-produto"

                    data-index="${index}"

                    value="${produto.quantidade}">

            </td>

            <td>

                ${produto.estoque}

            </td>

            <td>

                ${formatarMoeda(produto.valorUnitario)}

            </td>

            <td>

                ${formatarMoeda(produto.desconto)}

            </td>

            <td>

                ${formatarMoeda(produto.subtotal)}

            </td>

            <td>

                <button

                    class="btn btn-danger btn-sm excluir-produto"

                    data-index="${index}">

                    <i class="bi bi-trash"></i>

                </button>

            </td>

        `;

        tbody.appendChild(tr);

    });

}

/**********************************************************************
 * EVENTOS PRODUTOS
 **********************************************************************/

document.addEventListener("click", function (e) {

    const botao = e.target.closest(".excluir-produto");

    if (!botao) return;

    removerProduto(botao.dataset.index);

});

document.addEventListener("change", function (e) {

    if (!e.target.classList.contains("qtd-produto")) return;

    const index = Number(e.target.dataset.index);

    const quantidade = Number(e.target.value);

    listaProdutos[index].quantidade = quantidade;

    listaProdutos[index].subtotal =

        (listaProdutos[index].valorUnitario * quantidade)

        -

        listaProdutos[index].desconto;

    atualizarTabelaProdutos();

    atualizarTotais();

});

/**********************************************************************
 * CÁLCULOS FINANCEIROS
 **********************************************************************/

function totalServicos() {

    return listaServicos.reduce(

        (total, item) => total + item.subtotal,

        0

    );

}

function totalProdutos() {

    return listaProdutos.reduce(

        (total, item) => total + item.subtotal,

        0

    );

}

function valorDesconto() {

    return Number(

        document.getElementById("valorDesconto").value || 0

    );

}

function valorAcrescimo() {

    return Number(

        document.getElementById("valorAcrescimo").value || 0

    );

}

function valorFrete() {

    return Number(

        document.getElementById("valorFrete").value || 0

    );

}

function valorFinal() {

    return (

        totalServicos()

        +

        totalProdutos()

        +

        valorFrete()

        +

        valorAcrescimo()

        -

        valorDesconto()

    );

}

function atualizarTotais() {

    const totalS = totalServicos();

    const totalP = totalProdutos();

    const desconto = valorDesconto();

    const acrescimo = valorAcrescimo();

    const frete = valorFrete();

    const total = valorFinal();

    document.getElementById("totalServicos").textContent =
        formatarMoeda(totalS);

    document.getElementById("totalProdutos").textContent =
        formatarMoeda(totalP);

    document.getElementById("valorTotalOS").textContent =
        formatarMoeda(total);

    document.getElementById("subtotalOS").textContent =
        formatarMoeda(totalS + totalP);

    document.getElementById("totalDesconto").textContent =
        formatarMoeda(desconto);

    document.getElementById("totalAcrescimo").textContent =
        formatarMoeda(acrescimo);

    document.getElementById("totalFrete").textContent =
        formatarMoeda(frete);

}

/**********************************************************************
 * EVENTOS FINANCEIROS
 **********************************************************************/

[
    "valorDesconto",
    "valorAcrescimo",
    "valorFrete"

].forEach(id => {

    const campo = document.getElementById(id);

    if (!campo) return;

    campo.addEventListener("input", atualizarTotais);

});

/**********************************************************************
 * PROGRESSO
 **********************************************************************/

function atualizarProgresso() {

    let percentual = 0;

    if (clienteSelecionado) percentual += 20;

    if (veiculoSelecionado) percentual += 20;

    if (listaServicos.length > 0) percentual += 20;

    if (listaProdutos.length > 0) percentual += 20;

    if (

        document.getElementById("relatoCliente").value.trim()

        !== ""

    ) {

        percentual += 20;

    }

    progressoOS.style.width = percentual + "%";

    progressoOS.innerText = percentual + "%";

}

/**********************************************************************
 * UPLOAD
 **********************************************************************/

async function uploadArquivos() {

    if (!ordemServicoAtual) {

        aviso("Salve a Ordem de Serviço antes de anexar arquivos.");

        return;

    }

    const input = document.getElementById("arquivosOS");

    if (!input.files.length) {

        return;

    }

    const formData = new FormData();

    [...input.files].forEach(file => {

        formData.append("files", file);

    });

    mostrarLoading();

    try {

        const response = await fetch(

            `${ENDPOINTS.UPLOAD}/${ordemServicoAtual}`,

            {

                method: "POST",

                headers: {

                    Authorization:

                        `Bearer ${obterToken()}`

                },

                body: formData

            }

        );

        if (!response.ok) {

            throw new Error("Falha ao enviar arquivos.");

        }

        listaArquivos = await response.json();

        atualizarListaArquivos();

        sucesso("Arquivos enviados.");

    }

    catch (e) {

        erro(e.message);

    }

    finally {

        esconderLoading();

    }

}

function atualizarListaArquivos() {

    const lista = document.getElementById("listaArquivosOS");

    if (!lista) return;

    lista.innerHTML = "";

    listaArquivos.forEach(arquivo => {

        const item = document.createElement("div");

        item.className = "arquivo-item";

        item.innerHTML = `

            <div>

                <i class="bi bi-paperclip"></i>

                ${arquivo.nome}

            </div>

            <div>

                <button

                    class="btn btn-outline-danger btn-sm excluir-arquivo"

                    data-id="${arquivo.id}">

                    <i class="bi bi-trash"></i>

                </button>

            </div>

        `;

        lista.appendChild(item);

    });

}

/**********************************************************************
 * FORMATAÇÃO
 **********************************************************************/

function formatarMoeda(valor) {

    return Number(valor).toLocaleString(

        "pt-BR",

        {

            style: "currency",

            currency: "BRL"

        }

    );

}

function formatarData(data) {

    return new Date(data)

        .toLocaleDateString(

            "pt-BR"

        );

}

function formatarDataHora(data) {

    return new Date(data)

        .toLocaleString(

            "pt-BR"

        );

}

/**********************************************************************
 * HISTÓRICO DA ORDEM DE SERVIÇO
 **********************************************************************/

async function carregarHistoricoOS(idOS){

    try{

        historicoOS = await requisicao(

            `${ENDPOINTS.HISTORICO}/${idOS}`

        );

        atualizarHistorico();

    }

    catch(e){

        console.error(e);

    }

}

function atualizarHistorico(){

    const timeline=document.getElementById("timelineOS");

    if(!timeline) return;

    timeline.innerHTML="";

    historicoOS.forEach(item=>{

        const div=document.createElement("div");

        div.className="timeline-item";

        div.innerHTML=`

            <div class="timeline-card">

                <div class="d-flex justify-content-between">

                    <strong>${item.acao}</strong>

                    <small>

                        ${formatarDataHora(item.dataHora)}

                    </small>

                </div>

                <div>

                    <strong>Usuário:</strong>

                    ${item.usuario}

                </div>

                <div>

                    ${item.descricao}

                </div>

            </div>

        `;

        timeline.appendChild(div);

    });

}

/**********************************************************************
 * VALIDAÇÃO
 **********************************************************************/

function validarFormulario(){

    if(!clienteSelecionado){

        aviso("Selecione um cliente.");

        return false;

    }

    if(!veiculoSelecionado){

        aviso("Selecione um veículo.");

        return false;

    }

    if(listaServicos.length===0){

        aviso("Adicione pelo menos um serviço.");

        return false;

    }

    return true;

}

/**********************************************************************
 * FINALIZAR ORDEM DE SERVIÇO
 **********************************************************************/

async function finalizarOS(){

    if(!ordemServicoAtual){

        aviso("Salve a Ordem de Serviço antes.");

        return;

    }

    const confirma=await Swal.fire({

        icon:"question",

        title:"Finalizar Ordem de Serviço?",

        text:"Após finalizar não será possível editar alguns dados.",

        showCancelButton:true,

        confirmButtonText:"Finalizar",

        cancelButtonText:"Cancelar"

    });

    if(!confirma.isConfirmed){

        return;

    }

    try{

        mostrarLoading();

        await requisicao(

            `${ENDPOINTS.ORDEM_SERVICO}/${ordemServicoAtual}/finalizar`,

            "PUT"

        );

        sucesso("Ordem de Serviço finalizada.");

        document.getElementById("statusAtualOS").textContent="Finalizada";

    }

    catch(e){

        erro(e.message);

    }

    finally{

        esconderLoading();

    }

}

/**********************************************************************
 * IMPRESSÃO
 **********************************************************************/

function imprimirOS(){

    window.print();

}

/**********************************************************************
 * PDF
 **********************************************************************/

async function gerarPDF(){

    if(!ordemServicoAtual){

        aviso("Salve a Ordem de Serviço.");

        return;

    }

    window.open(

        `${ENDPOINTS.ORDEM_SERVICO}/${ordemServicoAtual}/pdf`,

        "_blank"

    );

}

/**********************************************************************
 * DRAG & DROP
 **********************************************************************/

const uploadArea=document.querySelector(".upload-area");

if(uploadArea){

    uploadArea.addEventListener("dragover",e=>{

        e.preventDefault();

        uploadArea.classList.add("dragover");

    });

    uploadArea.addEventListener("dragleave",()=>{

        uploadArea.classList.remove("dragover");

    });

    uploadArea.addEventListener("drop",e=>{

        e.preventDefault();

        uploadArea.classList.remove("dragover");

        document.getElementById("arquivosOS").files=e.dataTransfer.files;

        uploadArquivos();

    });

}

/**********************************************************************
 * EVENTOS DOS BOTÕES
 **********************************************************************/

btnSalvarOS?.addEventListener("click",async()=>{

    if(!validarFormulario()){

        return;

    }

    await salvarOS();

});

btnFinalizarOS?.addEventListener("click",finalizarOS);

btnImprimirOS?.addEventListener("click",imprimirOS);

btnGerarPdfOS?.addEventListener("click",gerarPDF);

document
.getElementById("arquivosOS")
?.addEventListener("change",uploadArquivos);

/**********************************************************************
 * PESQUISAS AUTOMÁTICAS
 **********************************************************************/

document
.getElementById("pesquisaCliente")
?.addEventListener(

    "keyup",

    debounce(function(){

        pesquisarClientes(this.value);

    },400)

);

document
.getElementById("pesquisaServico")
?.addEventListener(

    "keyup",

    debounce(async function(){

        const lista=await pesquisarServicos(this.value);

        preencherPesquisaServicos(lista);

    },400)

);

document
.getElementById("pesquisaProduto")
?.addEventListener(

    "keyup",

    debounce(async function(){

        const lista=await pesquisarProdutos(this.value);

        preencherPesquisaProdutos(lista);

    },400)

);

/**********************************************************************
 * DEBOUNCE
 **********************************************************************/

function debounce(func,tempo){

    let timer;

    return function(){

        clearTimeout(timer);

        timer=setTimeout(()=>{

            func.apply(this,arguments);

        },tempo);

    };

}

/**********************************************************************
 * PREENCHIMENTO PARA EDIÇÃO
 **********************************************************************/

function preencherFormulario(os){

    clienteSelecionado=os.cliente;

    veiculoSelecionado=os.veiculo;

    listaServicos=os.servicos ?? [];

    listaProdutos=os.produtos ?? [];

    preencherCliente();

    preencherVeiculo();

    atualizarTabelaServicos();

    atualizarTabelaProdutos();

    atualizarTotais();

    atualizarProgresso();

    document.getElementById("statusOS").value=os.status;

    document.getElementById("prioridadeOS").value=os.prioridade;

    document.getElementById("quilometragem").value=os.quilometragem;

    document.getElementById("relatoCliente").value=os.relatoCliente;

    document.getElementById("observacaoCliente").value=os.observacaoCliente;

    document.getElementById("observacaoInterna").value=os.observacaoInterna;

}

/**********************************************************************
 * INICIALIZAÇÃO
 **********************************************************************/

document.addEventListener("DOMContentLoaded",()=>{

    atualizarTabelaServicos();

    atualizarTabelaProdutos();

    atualizarTotais();

    atualizarProgresso();

    console.log("========================================");

    console.log(" Projeto Oficina Mecânica");

    console.log(" Módulo Ordem de Serviço");

    console.log(" modal-os.js carregado");

    console.log("========================================");

});


