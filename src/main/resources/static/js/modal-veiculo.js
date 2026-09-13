/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-veiculo.js
 * Descrição.....: Modal de Veículos
 *
 * CORRIGIDO: as referências ao DOM e o addEventListener aqui embaixo
 * rodavam direto no topo do arquivo, no momento em que o script era
 * carregado — só que o HTML deste modal (#modalVeiculo) nunca era
 * injetado na página (veiculos.html não tinha fetch nem container pra
 * ele), então modalVeiculo/btnSalvarVeiculo etc. eram sempre null e
 * "Novo Veículo" nunca funcionava. Agora tudo fica dentro de
 * configurarEventosModalVeiculo(), chamada pelo veiculos.html só depois
 * de injetar modal-veiculo.html no DOM.
 * ==========================================================
 */

let modalVeiculo;
let formVeiculo;
let btnSalvarVeiculo;
let btnCancelarVeiculo;
let btnFecharModalVeiculo;

let veiculoEditando = null;

function configurarEventosModalVeiculo() {

    modalVeiculo = document.getElementById("modalVeiculo");
    formVeiculo = document.getElementById("formVeiculo");

    btnSalvarVeiculo = document.getElementById("btnSalvarVeiculo");
    btnCancelarVeiculo = document.getElementById("btnCancelarVeiculo");
    btnFecharModalVeiculo = document.getElementById("btnFecharModalVeiculo");

    btnSalvarVeiculo.addEventListener("click", salvarVeiculo);

    btnCancelarVeiculo.addEventListener("click", fecharModalVeiculo);

    btnFecharModalVeiculo.addEventListener("click", fecharModalVeiculo);

}

/* ==========================================================
   NOVO
========================================================== */

function abrirModalNovoVeiculo() {

    veiculoEditando = null;

    formVeiculo.reset();

    document.getElementById("tituloModalVeiculo").innerText =
        "Novo Veículo";

    habilitarFormularioVeiculo();

    carregarClientes();

    modalVeiculo.classList.add("show");

}

/* ==========================================================
   EDITAR
========================================================== */

async function abrirModalEditarVeiculo(id) {

    try {

        mostrarLoading();

        const veiculo = await api.get(`/veiculos/${id}`);

        preencherFormularioVeiculo(veiculo);

        carregarClientes(veiculo.clienteId);

        veiculoEditando = id;

        document.getElementById("tituloModalVeiculo").innerText =
            "Editar Veículo";

        habilitarFormularioVeiculo();

        modalVeiculo.classList.add("show");

    } catch (error) {

        Toast.erro("Erro ao carregar veículo.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   VISUALIZAR
========================================================== */

async function abrirModalVisualizarVeiculo(id) {

    await abrirModalEditarVeiculo(id);

    desabilitarFormularioVeiculo();

    btnSalvarVeiculo.style.display = "none";

    document.getElementById("tituloModalVeiculo").innerText =
        "Visualizar Veículo";

}

/* ==========================================================
   FECHAR
========================================================== */

function fecharModalVeiculo() {

    modalVeiculo.classList.remove("show");

}

/* ==========================================================
   CLIENTES
========================================================== */

async function carregarClientes(clienteSelecionado = null) {

    try {

        const clientes = await api.get("/clientes");

        const select = document.getElementById("clienteId");

        select.innerHTML =
            '<option value="">Selecione...</option>';

        clientes.forEach(cliente => {

            select.innerHTML += `

                <option value="${cliente.id}">

                    ${cliente.nome}

                </option>

            `;

        });

        if(clienteSelecionado){

            select.value = clienteSelecionado;

        }

    } catch (error) {

        Toast.erro("Erro ao carregar clientes.");

    }

}

/* ==========================================================
   FORMULÁRIO
========================================================== */

function preencherFormularioVeiculo(v){

    document.getElementById("veiculoId").value = v.id;

    document.getElementById("clienteId").value = v.clienteId;

    document.getElementById("placa").value = v.placa;

    document.getElementById("renavam").value = v.renavam;

    document.getElementById("marca").value = v.marca;

    document.getElementById("modelo").value = v.modelo;

    document.getElementById("anoFabricacao").value = v.anoFabricacao;

    document.getElementById("anoModelo").value = v.anoModelo;

    document.getElementById("cor").value = v.cor;

    document.getElementById("combustivel").value = v.combustivel;

    document.getElementById("quilometragem").value = v.quilometragem;

    document.getElementById("observacoes").value = v.observacoes;

}

/* ==========================================================
   DADOS
========================================================== */

function obterDadosVeiculo(){

    return{

        clienteId:document.getElementById("clienteId").value,

        placa:document.getElementById("placa").value,

        renavam:document.getElementById("renavam").value,

        marca:document.getElementById("marca").value,

        modelo:document.getElementById("modelo").value,

        anoFabricacao:document.getElementById("anoFabricacao").value,

        anoModelo:document.getElementById("anoModelo").value,

        cor:document.getElementById("cor").value,

        combustivel:document.getElementById("combustivel").value,

        quilometragem:document.getElementById("quilometragem").value,

        observacoes:document.getElementById("observacoes").value

    };

}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarVeiculo(){

    try{

        mostrarLoading();

        const dados = obterDadosVeiculo();

        if(veiculoEditando == null){

            await api.post("/veiculos", dados);

            Toast.sucesso("Veículo cadastrado com sucesso.");

        }else{

            await api.put(`/veiculos/${veiculoEditando}`, dados);

            Toast.sucesso("Veículo atualizado com sucesso.");

        }

        fecharModalVeiculo();

        carregarVeiculos();

    }catch(error){

        Toast.erro("Erro ao salvar veículo.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   HABILITAR
========================================================== */

function habilitarFormularioVeiculo(){

    formVeiculo
        .querySelectorAll("input, select, textarea")
        .forEach(campo => campo.disabled = false);

    btnSalvarVeiculo.style.display = "inline-block";

}

/* ==========================================================
   DESABILITAR
========================================================== */

function desabilitarFormularioVeiculo(){

    formVeiculo
        .querySelectorAll("input, select, textarea")
        .forEach(campo => campo.disabled = true);

}