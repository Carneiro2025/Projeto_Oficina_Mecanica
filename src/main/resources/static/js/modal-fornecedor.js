/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-fornecedor.js
 * Descrição.....: Cadastro e edição de fornecedores
 * ==========================================================
 */

let modalFornecedor = null;

let fornecedorEditando = null;

document.addEventListener("DOMContentLoaded", () => {

    modalFornecedor = new bootstrap.Modal(
        document.getElementById("modalFornecedor")
    );

    configurarEventosFornecedor();

});

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventosFornecedor(){

    document
        .getElementById("btnSalvarFornecedor")
        ?.addEventListener(
            "click",
            salvarFornecedor
        );

    document
        .getElementById("btnLimparFornecedor")
        ?.addEventListener(
            "click",
            limparFormularioFornecedor
        );

    document
        .getElementById("btnBuscarCep")
        ?.addEventListener(
            "click",
            buscarCepFornecedor
        );

}

/* ==========================================================
   NOVO FORNECEDOR
========================================================== */

function abrirModalFornecedor(){

    fornecedorEditando = null;

    limparFormularioFornecedor();

    document.getElementById(
        "modalFornecedorLabel"
    ).innerHTML = `

        <i class="bi bi-plus-circle"></i>

        Novo Fornecedor

    `;

    modalFornecedor.show();

}

/* ==========================================================
   EDITAR
========================================================== */

async function abrirModalEditarFornecedor(id){

    try{

        mostrarLoading();

        fornecedorEditando = id;

        const fornecedor = await api.get(

            `/fornecedores/${id}`

        );

        preencherFormularioFornecedor(

            fornecedor

        );

        document.getElementById(

            "modalFornecedorLabel"

        ).innerHTML = `

            <i class="bi bi-pencil-square"></i>

            Editar Fornecedor

        `;

        modalFornecedor.show();

    }catch(erro){

        console.error(erro);

        Toast.erro(

            "Erro ao carregar fornecedor."

        );

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   VISUALIZAR
========================================================== */

async function abrirModalVisualizarFornecedor(id){

    await abrirModalEditarFornecedor(id);

    bloquearFormularioFornecedor(true);

}

/* ==========================================================
   BLOQUEAR FORMULÁRIO
========================================================== */

function bloquearFormularioFornecedor(bloquear){

    document

        .querySelectorAll(

            "#formFornecedor input, #formFornecedor select, #formFornecedor textarea"

        )

        .forEach(campo=>{

            campo.disabled = bloquear;

        });

    document.getElementById(

        "btnSalvarFornecedor"

    ).style.display = bloquear

        ? "none"

        : "";

}

/* ==========================================================
   PREENCHER FORMULÁRIO
========================================================== */

function preencherFormularioFornecedor(fornecedor){

    document.getElementById("idFornecedor").value =
        fornecedor.id ?? "";

    document.getElementById("ativoFornecedor").value =
        fornecedor.ativo;

    document.getElementById("razaoSocial").value =
        fornecedor.razaoSocial ?? "";

    document.getElementById("nomeFantasia").value =
        fornecedor.nomeFantasia ?? "";

    document.getElementById("cnpj").value =
        fornecedor.cnpj ?? "";

    document.getElementById("inscricaoEstadual").value =
        fornecedor.inscricaoEstadual ?? "";

    document.getElementById("inscricaoMunicipal").value =
        fornecedor.inscricaoMunicipal ?? "";

    document.getElementById("cnae").value =
        fornecedor.cnae ?? "";

    document.getElementById("dataCadastro").value =
        fornecedor.dataCadastro ?? "";

    document.getElementById("categoriaFornecedor").value =
        fornecedor.categoriaFornecedor ?? "";

    document.getElementById("responsavel").value =
        fornecedor.responsavel ?? "";

    document.getElementById("cargo").value =
        fornecedor.cargo ?? "";

    document.getElementById("telefone").value =
        fornecedor.telefone ?? "";

    document.getElementById("celular").value =
        fornecedor.celular ?? "";

    document.getElementById("whatsapp").value =
        fornecedor.whatsapp ?? "";

    document.getElementById("email").value =
        fornecedor.email ?? "";

    document.getElementById("site").value =
        fornecedor.site ?? "";

    document.getElementById("departamento").value =
        fornecedor.departamento ?? "";

    document.getElementById("horarioContato").value =
        fornecedor.horarioContato ?? "";

    document.getElementById("cep").value =
        fornecedor.cep ?? "";

    document.getElementById("logradouro").value =
        fornecedor.logradouro ?? "";

    document.getElementById("numero").value =
        fornecedor.numero ?? "";

    document.getElementById("complemento").value =
        fornecedor.complemento ?? "";

    document.getElementById("bairro").value =
        fornecedor.bairro ?? "";

    document.getElementById("cidade").value =
        fornecedor.cidade ?? "";

    document.getElementById("estado").value =
        fornecedor.estado ?? "";

    document.getElementById("referencia").value =
        fornecedor.referencia ?? "";

    document.getElementById("pais").value =
        fornecedor.pais ?? "";

    document.getElementById("regiao").value =
        fornecedor.regiao ?? "";

    document.getElementById("codigoIbge").value =
        fornecedor.codigoIbge ?? "";

    document.getElementById("banco").value =
        fornecedor.banco ?? "";

    document.getElementById("agencia").value =
        fornecedor.agencia ?? "";

    document.getElementById("conta").value =
        fornecedor.conta ?? "";

    document.getElementById("tipoPix").value =
        fornecedor.tipoPix ?? "";

    document.getElementById("chavePix").value =
        fornecedor.chavePix ?? "";

    document.getElementById("observacoes").value =
        fornecedor.observacoes ?? "";

}

/* ==========================================================
   LIMPAR FORMULÁRIO
========================================================== */

function limparFormularioFornecedor(){

    document.getElementById("formFornecedor").reset();

    document.getElementById("idFornecedor").value = "";

    bloquearFormularioFornecedor(false);

    document.querySelector(
        "#empresa-tab"
    ).click();

}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarFornecedor(){

    if(!validarFornecedor()){

        return;

    }

    const fornecedor = obterFornecedorFormulario();

    try{

        mostrarLoading();

        if(fornecedorEditando){

            await api.put(

                `/fornecedores/${fornecedorEditando}`,

                fornecedor

            );

            Toast.sucesso(

                "Fornecedor atualizado com sucesso."

            );

        }else{

            await api.post(

                "/fornecedores",

                fornecedor

            );

            Toast.sucesso(

                "Fornecedor cadastrado com sucesso."

            );

        }

        modalFornecedor.hide();

        document.dispatchEvent(

            new Event("fornecedorSalvo")

        );

    }catch(erro){

        console.error(erro);

        Toast.erro(

            "Erro ao salvar fornecedor."

        );

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   FECHAR MODAL
========================================================== */

function fecharModalFornecedor(){

    modalFornecedor.hide();

}

/* ==========================================================
   ATUALIZAR LISTA
========================================================== */

document.addEventListener(

    "fornecedorSalvo",

    async()=>{

        await carregarFornecedores();

    }

);

/* ==========================================================
   BUSCAR CEP (ViaCEP)
========================================================== */

async function buscarCepFornecedor(){

    const cep = document
        .getElementById("cep")
        .value
        .replace(/\D/g,"");

    if(cep.length !== 8){

        Toast.erro("CEP inválido.");

        return;

    }

    try{

        mostrarLoading();

        const response = await fetch(

            `https://viacep.com.br/ws/${cep}/json/`

        );

        const endereco = await response.json();

        if(endereco.erro){

            Toast.erro("CEP não encontrado.");

            return;

        }

        document.getElementById("logradouro").value =
            endereco.logradouro || "";

        document.getElementById("bairro").value =
            endereco.bairro || "";

        document.getElementById("cidade").value =
            endereco.localidade || "";

        document.getElementById("estado").value =
            endereco.uf || "";

        document.getElementById("codigoIbge").value =
            endereco.ibge || "";

        document.getElementById("numero").focus();

    }catch(error){

        console.error(error);

        Toast.erro(

            "Erro ao consultar o CEP."

        );

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   MÁSCARAS
========================================================== */

function configurarMascarasFornecedor(){

    IMask(

        document.getElementById("cnpj"),

        {

            mask:"00.000.000/0000-00"

        }

    );

    IMask(

        document.getElementById("cep"),

        {

            mask:"00000-000"

        }

    );

    IMask(

        document.getElementById("telefone"),

        {

            mask:"(00) 0000-0000"

        }

    );

    IMask(

        document.getElementById("celular"),

        {

            mask:"(00) 00000-0000"

        }

    );

    IMask(

        document.getElementById("whatsapp"),

        {

            mask:"(00) 00000-0000"

        }

    );

}

/* ==========================================================
   VALIDAÇÃO
========================================================== */

function validarFornecedor(){

    if(

        document.getElementById("razaoSocial").value.trim()===""

    ){

        Toast.erro(

            "Informe a razão social."

        );

        document.getElementById("razaoSocial").focus();

        return false;

    }

    if(

        document.getElementById("cnpj").value.trim()===""

    ){

        Toast.erro(

            "Informe o CNPJ."

        );

        document.getElementById("cnpj").focus();

        return false;

    }

    return true;

}

/* ==========================================================
   CONFIGURAÇÃO
========================================================== */

document.addEventListener(

    "DOMContentLoaded",

    ()=>{

        configurarMascarasFornecedor();

    }

);

/* ==========================================================
   CEP AUTOMÁTICO
========================================================== */

document

    .getElementById("cep")

    ?.addEventListener(

        "blur",

        buscarCepFornecedor

    );

    /* ==========================================================
   LIMPAR VALIDAÇÃO
========================================================== */

function limparValidacaoFornecedor(){

    document

        .querySelectorAll(

            "#formFornecedor .is-invalid"

        )

        .forEach(campo=>{

            campo.classList.remove(

                "is-invalid"

            );

        });

}

/* ==========================================================
   VALIDAR E-MAIL
========================================================== */

function validarEmailFornecedor(){

    const email = document
        .getElementById("email")
        .value;

    if(

        email !== "" &&

        !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)

    ){

        Toast.erro(

            "E-mail inválido."

        );

        return false;

    }

    return true;

}

/* ==========================================================
   MONTAR OBJETO DO FORMULÁRIO
========================================================== */

function obterFornecedorFormulario(){

    return{

        ativo:
            document.getElementById("ativoFornecedor").value === "true",

        razaoSocial:
            document.getElementById("razaoSocial").value.trim(),

        nomeFantasia:
            document.getElementById("nomeFantasia").value.trim(),

        cnpj:
            document.getElementById("cnpj").value.trim(),

        inscricaoEstadual:
            document.getElementById("inscricaoEstadual").value.trim(),

        inscricaoMunicipal:
            document.getElementById("inscricaoMunicipal").value.trim(),

        cnae:
            document.getElementById("cnae").value.trim(),

        dataCadastro:
            document.getElementById("dataCadastro").value,

        categoriaFornecedor:
            document.getElementById("categoriaFornecedor").value,

        responsavel:
            document.getElementById("responsavel").value.trim(),

        cargo:
            document.getElementById("cargo").value.trim(),

        telefone:
            document.getElementById("telefone").value.trim(),

        celular:
            document.getElementById("celular").value.trim(),

        whatsapp:
            document.getElementById("whatsapp").value.trim(),

        email:
            document.getElementById("email").value.trim(),

        site:
            document.getElementById("site").value.trim(),

        departamento:
            document.getElementById("departamento").value,

        horarioContato:
            document.getElementById("horarioContato").value.trim(),

        cep:
            document.getElementById("cep").value.trim(),

        logradouro:
            document.getElementById("logradouro").value.trim(),

        numero:
            document.getElementById("numero").value.trim(),

        complemento:
            document.getElementById("complemento").value.trim(),

        bairro:
            document.getElementById("bairro").value.trim(),

        cidade:
            document.getElementById("cidade").value.trim(),

        estado:
            document.getElementById("estado").value.trim(),

        referencia:
            document.getElementById("referencia").value.trim(),

        pais:
            document.getElementById("pais").value.trim(),

        regiao:
            document.getElementById("regiao").value,

        codigoIbge:
            document.getElementById("codigoIbge").value.trim(),

        banco:
            document.getElementById("banco").value.trim(),

        agencia:
            document.getElementById("agencia").value.trim(),

        conta:
            document.getElementById("conta").value.trim(),

        tipoPix:
            document.getElementById("tipoPix").value,

        chavePix:
            document.getElementById("chavePix").value.trim(),

        observacoes:
            document.getElementById("observacoes").value.trim()

    };

}

/* ==========================================================
   ABRIR ABA
========================================================== */

function abrirAbaFornecedor(idAba){

    const aba = document.querySelector(

        `button[data-bs-target="#${idAba}"]`

    );

    if(aba){

        bootstrap.Tab.getOrCreateInstance(aba).show();

    }

}

/* ==========================================================
   PRIMEIRA ABA
========================================================== */

function primeiraAbaFornecedor(){

    abrirAbaFornecedor("empresa");

}

/* ==========================================================
   PRÓXIMA ABA
========================================================== */

function proximaAbaFornecedor(){

    const abas = [

        "empresa",

        "contato",

        "endereco",

        "banco",

        "observacao"

    ];

    const atual = document.querySelector(

        ".tab-pane.active"

    );

    if(!atual){

        return;

    }

    const indice = abas.indexOf(atual.id);

    if(indice < abas.length - 1){

        abrirAbaFornecedor(

            abas[indice + 1]

        );

    }

}

/* ==========================================================
   ABA ANTERIOR
========================================================== */

function abaAnteriorFornecedor(){

    const abas = [

        "empresa",

        "contato",

        "endereco",

        "banco",

        "observacao"

    ];

    const atual = document.querySelector(

        ".tab-pane.active"

    );

    if(!atual){

        return;

    }

    const indice = abas.indexOf(atual.id);

    if(indice > 0){

        abrirAbaFornecedor(

            abas[indice - 1]

        );

    }

}

/* ==========================================================
   ATALHOS
========================================================== */

document.addEventListener(

    "keydown",

    function(event){

        if(

            !document
                .getElementById("modalFornecedor")
                .classList
                .contains("show")

        ){

            return;

        }

        if(event.key === "Escape"){

            fecharModalFornecedor();

        }

        if(event.ctrlKey && event.key === "ArrowRight"){

            event.preventDefault();

            proximaAbaFornecedor();

        }

        if(event.ctrlKey && event.key === "ArrowLeft"){

            event.preventDefault();

            abaAnteriorFornecedor();

        }

        if(event.ctrlKey && event.key.toLowerCase() === "s"){

            event.preventDefault();

            salvarFornecedor();

        }

    }

);

/* ==========================================================
   LIMPAR AO FECHAR
========================================================== */

document

    .getElementById("modalFornecedor")

    ?.addEventListener(

        "hidden.bs.modal",

        ()=>{

            limparFormularioFornecedor();

        }

    );

    /* ==========================================================
   TRATAMENTO DE ERROS DA API
========================================================== */

function tratarErroFornecedor(error){

    console.error(error);

    if(error.response){

        switch(error.response.status){

            case 400:

                Toast.erro(
                    "Dados inválidos. Verifique os campos."
                );
                break;

            case 401:

                Toast.erro(
                    "Sessão expirada. Faça login novamente."
                );
                break;

            case 403:

                Toast.erro(
                    "Você não possui permissão para esta operação."
                );
                break;

            case 404:

                Toast.erro(
                    "Fornecedor não encontrado."
                );
                break;

            case 409:

                Toast.erro(
                    "Já existe um fornecedor com este CNPJ."
                );
                break;

            case 500:

                Toast.erro(
                    "Erro interno do servidor."
                );
                break;

            default:

                Toast.erro(
                    "Erro inesperado."
                );

        }

    }else{

        Toast.erro(
            "Não foi possível conectar ao servidor."
        );

    }

}

/* ==========================================================
   ALTERAÇÕES NO FORMULÁRIO
========================================================== */

let formularioAlterado = false;

document

    .querySelectorAll(

        "#formFornecedor input, #formFornecedor select, #formFornecedor textarea"

    )

    .forEach(campo=>{

        campo.addEventListener(

            "change",

            ()=>{

                formularioAlterado = true;

            }

        );

    });

    /* ==========================================================
   CONFIRMAR FECHAMENTO
========================================================== */

document

    .getElementById("modalFornecedor")

    ?.addEventListener(

        "hide.bs.modal",

        async function(event){

            if(!formularioAlterado){

                return;

            }

            event.preventDefault();

            const resposta = await Swal.fire({

                title:"Descartar alterações?",

                text:"Existem alterações não salvas.",

                icon:"warning",

                showCancelButton:true,

                confirmButtonText:"Descartar",

                cancelButtonText:"Continuar editando",

                confirmButtonColor:"#DC2626"

            });

            if(resposta.isConfirmed){

                formularioAlterado = false;

                bootstrap.Modal
                    .getInstance(this)
                    .hide();

            }

        }

    );

    /* ==========================================================
   RESETAR CONTROLE
========================================================== */

function resetarControleFornecedor(){

    formularioAlterado = false;

    fornecedorEditando = null;

}

modalFornecedor.hide();

resetarControleFornecedor();

document.dispatchEvent(

    new Event(

        "fornecedorSalvo"

    )

);

/* ==========================================================
   AO FECHAR O MODAL
========================================================== */

document

    .getElementById("modalFornecedor")

    ?.addEventListener(

        "hidden.bs.modal",

        ()=>{

            limparFormularioFornecedor();

            resetarControleFornecedor();

            bloquearFormularioFornecedor(false);

            primeiraAbaFornecedor();

        }

    );

    /* ==========================================================
   INICIALIZAÇÃO
========================================================== */

document.addEventListener(

    "DOMContentLoaded",

    ()=>{

        configurarEventosFornecedor();

        configurarMascarasFornecedor();

        primeiraAbaFornecedor();

    }

);

/* ==========================================================
   FIM DO ARQUIVO
========================================================== */

/*

 ███╗   ███╗ ██████╗ ██████╗  █████╗ ██╗
 ████╗ ████║██╔═══██╗██╔══██╗██╔══██╗██║
 ██╔████╔██║██║   ██║██║  ██║███████║██║
 ██║╚██╔╝██║██║   ██║██║  ██║██╔══██║██║
 ██║ ╚═╝ ██║╚██████╔╝██████╔╝██║  ██║███████╗
 ╚═╝     ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝

modal-fornecedor.js
VERSÃO FINAL

Projeto OficinaPRO
Sprint 6

*/



