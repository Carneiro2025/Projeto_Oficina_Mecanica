/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-financeiro.js
 * Descrição.....: Modais de Conta a Pagar, Pagamento e Recebimento
 * ==========================================================
 */

let modalContaPagar, formContaPagar, btnSalvarContaPagar, btnCancelarContaPagar, btnFecharModalContaPagar;
let modalPagamento, btnConfirmarPagamento, btnCancelarPagamento, btnFecharModalPagamento;
let modalRecebimento, btnConfirmarRecebimento, btnCancelarRecebimento, btnFecharModalRecebimento;

function configurarEventosModalFinanceiro() {

    modalContaPagar = document.getElementById("modalContaPagar");
    formContaPagar = document.getElementById("formContaPagar");
    btnSalvarContaPagar = document.getElementById("btnSalvarContaPagar");
    btnCancelarContaPagar = document.getElementById("btnCancelarContaPagar");
    btnFecharModalContaPagar = document.getElementById("btnFecharModalContaPagar");

    modalPagamento = document.getElementById("modalPagamento");
    btnConfirmarPagamento = document.getElementById("btnConfirmarPagamento");
    btnCancelarPagamento = document.getElementById("btnCancelarPagamento");
    btnFecharModalPagamento = document.getElementById("btnFecharModalPagamento");

    modalRecebimento = document.getElementById("modalRecebimento");
    btnConfirmarRecebimento = document.getElementById("btnConfirmarRecebimento");
    btnCancelarRecebimento = document.getElementById("btnCancelarRecebimento");
    btnFecharModalRecebimento = document.getElementById("btnFecharModalRecebimento");

    btnSalvarContaPagar.addEventListener("click", salvarContaPagar);
    btnCancelarContaPagar.addEventListener("click", () => modalContaPagar.classList.remove("show"));
    btnFecharModalContaPagar.addEventListener("click", () => modalContaPagar.classList.remove("show"));

    btnConfirmarPagamento.addEventListener("click", confirmarPagamento);
    btnCancelarPagamento.addEventListener("click", () => modalPagamento.classList.remove("show"));
    btnFecharModalPagamento.addEventListener("click", () => modalPagamento.classList.remove("show"));

    btnConfirmarRecebimento.addEventListener("click", confirmarRecebimento);
    btnCancelarRecebimento.addEventListener("click", () => modalRecebimento.classList.remove("show"));
    btnFecharModalRecebimento.addEventListener("click", () => modalRecebimento.classList.remove("show"));

}

/* ==========================================================
   NOVA CONTA A PAGAR
========================================================== */

async function abrirModalContaPagar() {

    formContaPagar.reset();

    await carregarFornecedoresSelect();

    modalContaPagar.classList.add("show");

}

async function carregarFornecedoresSelect() {

    try {

        const resposta = await api.get("/fornecedores");

        const fornecedores = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

        const select = document.getElementById("fornecedorId");

        select.innerHTML = '<option value="">Selecione...</option>';

        fornecedores.forEach(f => {
            select.innerHTML += `<option value="${f.id}">${f.razaoSocial}</option>`;
        });

    } catch (error) {
        console.error("Erro ao carregar fornecedores:", error);
    }

}

async function salvarContaPagar() {

    try {

        mostrarLoading();

        const dados = {
            fornecedorId: document.getElementById("fornecedorId").value,
            descricao: document.getElementById("descricaoContaPagar").value,
            valor: document.getElementById("valorContaPagar").value,
            dataVencimento: document.getElementById("dataVencimentoContaPagar").value,
            observacao: document.getElementById("observacaoContaPagar").value
        };

        await api.post("/contas-pagar", dados);

        Toast.sucesso("Conta a pagar cadastrada com sucesso.");

        modalContaPagar.classList.remove("show");

        carregarContasPagar();

        carregarResumo();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao salvar conta a pagar.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   PAGAMENTO
========================================================== */

function abrirModalPagamentoConta(contaId) {

    document.getElementById("formPagamento").reset();

    document.getElementById("contaPagarId").value = contaId;

    document.getElementById("dataPagamento").valueAsDate = new Date();

    modalPagamento.classList.add("show");

}

async function confirmarPagamento() {

    try {

        mostrarLoading();

        const contaId = document.getElementById("contaPagarId").value;

        const dados = {
            dataPagamento: document.getElementById("dataPagamento").value,
            formaPagamento: document.getElementById("formaPagamento").value
        };

        await api.patch(`/contas-pagar/${contaId}/pagamento`, dados);

        Toast.sucesso("Pagamento registrado com sucesso.");

        modalPagamento.classList.remove("show");

        carregarContasPagar();

        carregarResumo();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao registrar pagamento.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   RECEBIMENTO
========================================================== */

function abrirModalRecebimentoConta(contaId) {

    document.getElementById("formRecebimento").reset();

    document.getElementById("contaReceberId").value = contaId;

    document.getElementById("dataRecebimento").valueAsDate = new Date();

    modalRecebimento.classList.add("show");

}

async function confirmarRecebimento() {

    try {

        mostrarLoading();

        const contaId = document.getElementById("contaReceberId").value;

        const dados = {
            dataPagamento: document.getElementById("dataRecebimento").value,
            formaPagamento: document.getElementById("formaRecebimento").value
        };

        await api.patch(`/contas-receber/${contaId}/pagamento`, dados);

        Toast.sucesso("Recebimento registrado com sucesso.");

        modalRecebimento.classList.remove("show");

        carregarContasReceber();

        carregarResumo();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao registrar recebimento.");

    } finally {

        ocultarLoading();

    }

}
