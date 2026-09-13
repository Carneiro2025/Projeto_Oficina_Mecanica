/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-cliente.js
 * Descrição.....: Controle do Modal de Clientes
 *
 * REESCRITO: (1) o arquivo anterior acessava o DOM (getElementById,
 * addEventListener) direto no topo do arquivo, no momento em que o
 * script era carregado — mas clientes.html só injeta modal-cliente.html
 * bem depois, dentro do DOMContentLoaded, então modalCliente/formCliente
 * etc. eram sempre null e "Novo Cliente" nunca funcionava. Agora tudo
 * fica em configurarEventosModalCliente(), chamada pelo clientes.html
 * só depois da injeção. (2) os campos enviados/lidos usavam nomes que
 * não existem na API (cliente.cpf, cliente.cidade soltos) — o back-end
 * espera cpfCnpj e um objeto aninhado "endereco" (logradouro/numero/
 * complemento/bairro/cidade/uf/cep). Não existe campo "rg" na API —
 * o campo do formulário continua existindo mas não é mais enviado.
 * ==========================================================
 */

let modalCliente;
let formCliente;
let tituloModal;
let btnSalvarCliente;
let btnCancelar;
let btnFecharModal;

let clienteEditando = null;

function configurarEventosModalCliente() {

    modalCliente = document.getElementById("modalCliente");
    formCliente = document.getElementById("formCliente");
    tituloModal = document.getElementById("tituloModal");
    btnSalvarCliente = document.getElementById("btnSalvarCliente");
    btnCancelar = document.getElementById("btnCancelar");
    btnFecharModal = document.getElementById("btnFecharModal");

    btnCancelar.addEventListener("click", fecharModal);

    btnFecharModal.addEventListener("click", fecharModal);

    btnSalvarCliente.addEventListener("click", salvarCliente);

}

/* ==========================================================
   ABRIR MODAL — NOVO
========================================================== */

function abrirModalNovoCliente() {

    clienteEditando = null;

    tituloModal.innerText = "Novo Cliente";

    limparFormulario();

    habilitarFormulario();

    modalCliente.classList.add("show");

}

/* ==========================================================
   EDITAR
========================================================== */

async function abrirModalEditar(id) {

    try {

        mostrarLoading();

        const cliente = await api.get(`/clientes/${id}`);

        clienteEditando = id;

        preencherFormulario(cliente);

        habilitarFormulario();

        tituloModal.innerText = "Editar Cliente";

        modalCliente.classList.add("show");

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar cliente.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   VISUALIZAR
========================================================== */

async function abrirModalVisualizar(id) {

    try {

        mostrarLoading();

        const cliente = await api.get(`/clientes/${id}`);

        clienteEditando = null;

        preencherFormulario(cliente);

        desabilitarFormulario();

        tituloModal.innerText = "Visualizar Cliente";

        modalCliente.classList.add("show");

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar cliente.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   FECHAR
========================================================== */

function fecharModal() {

    modalCliente.classList.remove("show");

}

/* ==========================================================
   LIMPAR
========================================================== */

function limparFormulario() {

    formCliente.reset();

    document.getElementById("clienteId").value = "";

}

/* ==========================================================
   PREENCHER (a partir de ClienteResponseDTO real)
========================================================== */

function preencherFormulario(cliente) {

    const endereco = cliente.endereco || {};

    document.getElementById("clienteId").value = cliente.id || "";

    document.getElementById("nome").value = cliente.nome || cliente.razaoSocial || "";

    document.getElementById("cpf").value = cliente.cpfCnpj || "";

    document.getElementById("telefone").value = cliente.telefone || "";

    document.getElementById("celular").value = cliente.celular || "";

    document.getElementById("email").value = cliente.email || "";

    document.getElementById("cep").value = endereco.cep || "";

    document.getElementById("numero").value = endereco.numero || "";

    document.getElementById("rua").value = endereco.logradouro || "";

    document.getElementById("bairro").value = endereco.bairro || "";

    document.getElementById("cidade").value = endereco.cidade || "";

    document.getElementById("estado").value = endereco.uf || "";

    document.getElementById("complemento").value = endereco.complemento || "";

    document.getElementById("observacoes").value = cliente.observacoes || "";

}

/* ==========================================================
   DADOS DO FORMULÁRIO → formato real da API
   (CriarClienteRequestDTO / AtualizarClienteRequestDTO)
========================================================== */

function obterDadosFormulario() {

    return {

        nome: document.getElementById("nome").value,

        cpfCnpj: document.getElementById("cpf").value,

        tipo: "PF",

        telefone: document.getElementById("telefone").value,

        celular: document.getElementById("celular").value,

        email: document.getElementById("email").value,

        endereco: {
            cep: document.getElementById("cep").value,
            numero: document.getElementById("numero").value,
            logradouro: document.getElementById("rua").value,
            bairro: document.getElementById("bairro").value,
            cidade: document.getElementById("cidade").value,
            uf: document.getElementById("estado").value,
            complemento: document.getElementById("complemento").value
        },

        observacoes: document.getElementById("observacoes").value

    };

}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarCliente() {

    const cliente = obterDadosFormulario();

    try {

        mostrarLoading();

        if (clienteEditando == null) {

            await api.post("/clientes", cliente);

            Toast.sucesso("Cliente cadastrado com sucesso.");

        } else {

            await api.put(`/clientes/${clienteEditando}`, cliente);

            Toast.sucesso("Cliente atualizado com sucesso.");

        }

        fecharModal();

        carregarClientes();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao salvar cliente.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   HABILITAR / DESABILITAR (modo visualização)
========================================================== */

function habilitarFormulario() {

    formCliente
        .querySelectorAll("input, textarea, select")
        .forEach(campo => campo.disabled = false);

    btnSalvarCliente.style.display = "inline-block";

}

function desabilitarFormulario() {

    formCliente
        .querySelectorAll("input, textarea, select")
        .forEach(campo => campo.disabled = true);

    btnSalvarCliente.style.display = "none";

}
