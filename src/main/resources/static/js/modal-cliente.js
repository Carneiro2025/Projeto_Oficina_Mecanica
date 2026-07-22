/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-cliente.js
 * Descrição.....: Controle do Modal de Clientes
 * ==========================================================
 */

const modalCliente = document.getElementById("modalCliente");

const formCliente = document.getElementById("formCliente");

const tituloModal = document.getElementById("tituloModal");

const btnSalvarCliente = document.getElementById("btnSalvarCliente");

const btnCancelar = document.getElementById("btnCancelar");

const btnFecharModal = document.getElementById("btnFecharModal");

let clienteEditando = null;

/* ==========================================================
   EVENTOS
========================================================== */

btnCancelar.addEventListener("click", fecharModal);

btnFecharModal.addEventListener("click", fecharModal);

btnSalvarCliente.addEventListener("click", salvarCliente);

/* ==========================================================
   ABRIR MODAL
========================================================== */

function abrirModalNovoCliente(){

    clienteEditando = null;

    tituloModal.innerText = "Novo Cliente";

    limparFormulario();

    habilitarFormulario();

    modalCliente.classList.add("show");

}

/* ==========================================================
   EDITAR
========================================================== */

async function abrirModalEditar(id){

    try{

        mostrarLoading();

        const cliente = await api.get(`/clientes/${id}`);

        clienteEditando = id;

        preencherFormulario(cliente);

        habilitarFormulario();

        tituloModal.innerText = "Editar Cliente";

        modalCliente.classList.add("show");

    }catch(error){

        Toast.erro("Erro ao carregar cliente.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   VISUALIZAR
========================================================== */

async function abrirModalVisualizar(id){

    try{

        mostrarLoading();

        const cliente = await api.get(`/clientes/${id}`);

        preencherFormulario(cliente);

        desabilitarFormulario();

        tituloModal.innerText = "Visualizar Cliente";

        modalCliente.classList.add("show");

    }catch(error){

        Toast.erro("Erro ao carregar cliente.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   FECHAR
========================================================== */

function fecharModal(){

    modalCliente.classList.remove("show");

}

/* ==========================================================
   LIMPAR
========================================================== */

function limparFormulario(){

    formCliente.reset();

    document.getElementById("clienteId").value = "";

}

/* ==========================================================
   PREENCHER
========================================================== */

function preencherFormulario(cliente){

    document.getElementById("clienteId").value = cliente.id || "";

    document.getElementById("nome").value = cliente.nome || "";

    document.getElementById("cpf").value = cliente.cpf || "";

    document.getElementById("rg").value = cliente.rg || "";

    document.getElementById("telefone").value = cliente.telefone || "";

    document.getElementById("celular").value = cliente.celular || "";

    document.getElementById("email").value = cliente.email || "";

    document.getElementById("cep").value = cliente.cep || "";

    document.getElementById("numero").value = cliente.numero || "";

    document.getElementById("rua").value = cliente.rua || "";

    document.getElementById("bairro").value = cliente.bairro || "";

    document.getElementById("cidade").value = cliente.cidade || "";

    document.getElementById("estado").value = cliente.estado || "";

    document.getElementById("complemento").value = cliente.complemento || "";

    document.getElementById("observacoes").value = cliente.observacoes || "";

}

/* ==========================================================
   DADOS DO FORMULÁRIO
========================================================== */

function obterDadosFormulario(){

    return{

        nome:document.getElementById("nome").value,

        cpf:document.getElementById("cpf").value,

        rg:document.getElementById("rg").value,

        telefone:document.getElementById("telefone").value,

        celular:document.getElementById("celular").value,

        email:document.getElementById("email").value,

        cep:document.getElementById("cep").value,

        numero:document.getElementById("numero").value,

        rua:document.getElementById("rua").value,

        bairro:document.getElementById("bairro").value,

        cidade:document.getElementById("cidade").value,

        estado:document.getElementById("estado").value,

        complemento:document.getElementById("complemento").value,

        observacoes:document.getElementById("observacoes").value

    };

}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarCliente(){

    const cliente = obterDadosFormulario();

    try{

        mostrarLoading();

        if(clienteEditando==null){

            await api.post("/clientes",cliente);

            Toast.sucesso("Cliente cadastrado com sucesso.");

        }else{

            await api.put(`/clientes/${clienteEditando}`,cliente);

            Toast.sucesso("Cliente atualizado com sucesso.");

        }

        fecharModal();

        carregarClientes();

    }catch(error){

        Toast.erro("Erro ao salvar cliente.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   HABILITAR
========================================================== */

function habilitarFormulario(){

    formCliente
        .querySelectorAll("input, textarea, select")
        .forEach(campo=>campo.disabled=false);

    btnSalvarCliente.style.display="inline-block";

}

/* ==========================================================
   DESABILITAR
========================================================== */

function desabilitarFormulario(){

    formCliente
        .querySelectorAll("input, textarea, select")
        .forEach(campo=>campo.disabled=true);

    btnSalvarCliente.style.display="none";

}