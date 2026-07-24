/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-produto.js
 * Descrição.....: Controle do Modal de Produtos
 * ==========================================================
 */

let produtoEditando = null;

/* ==========================================================
   INICIALIZAÇÃO
========================================================== */

document.addEventListener("DOMContentLoaded", () => {

    configurarEventosModal();

});

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventosModal(){

    document
        .getElementById("btnFecharModalProduto")
        .addEventListener("click", fecharModalProduto);

    document
        .getElementById("btnCancelarProduto")
        .addEventListener("click", fecharModalProduto);

    document
        .getElementById("btnLimparProduto")
        .addEventListener("click", limparFormularioProduto);

    document
        .getElementById("formProduto")
        .addEventListener("submit", salvarProduto);

    configurarAbas();

    configurarCalculoMargem();

}

/* ==========================================================
   ABRIR MODAL
========================================================== */

function abrirModalProduto(){

    produtoEditando = null;

    limparFormularioProduto();

    document.getElementById("tituloModalProduto").innerHTML =

        '<i class="bi bi-box-seam-fill"></i> Novo Produto';

    document.getElementById("modalProduto").style.display="flex";

}

/* ==========================================================
   FECHAR MODAL
========================================================== */

function fecharModalProduto(){

    document.getElementById("modalProduto").style.display="none";

}

/* ==========================================================
   LIMPAR FORMULÁRIO
========================================================== */

function limparFormularioProduto(){

    document.getElementById("formProduto").reset();

    document.getElementById("produtoId").value="";

    selecionarAba("geral");

}

/* ==========================================================
   CONFIGURAR ABAS
========================================================== */

function configurarAbas(){

    const abas = document.querySelectorAll(".tab-button");

    abas.forEach(botao =>{

        botao.addEventListener("click",()=>{

            selecionarAba(botao.dataset.tab);

        });

    });

}

function selecionarAba(nome){

    document.querySelectorAll(".tab-button")
        .forEach(btn=>btn.classList.remove("active"));

    document.querySelectorAll(".tab-content")
        .forEach(tab=>tab.classList.remove("active"));

    document
        .querySelector(`[data-tab="${nome}"]`)
        .classList.add("active");

    document
        .getElementById(`tab-${nome}`)
        .classList.add("active");

}

/* ==========================================================
   EDITAR PRODUTO
========================================================== */

async function abrirModalEditarProduto(id){

    try{

        mostrarLoading();

        const produto = await api.get(`/produtos/${id}`);

        produtoEditando = id;

        preencherFormulario(produto);

        document.getElementById("tituloModalProduto").innerHTML =

            '<i class="bi bi-pencil-square"></i> Editar Produto';

        habilitarFormulario(true);

        document.getElementById("btnSalvarProduto").style.display="inline-flex";

        document.getElementById("modalProduto").style.display="flex";

    }catch(erro){

        console.error(erro);

        Toast.erro("Erro ao carregar produto.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   VISUALIZAR PRODUTO
========================================================== */

async function abrirModalVisualizarProduto(id){

    try{

        mostrarLoading();

        const produto = await api.get(`/produtos/${id}`);

        preencherFormulario(produto);

        document.getElementById("tituloModalProduto").innerHTML =

            '<i class="bi bi-eye-fill"></i> Visualizar Produto';

        habilitarFormulario(false);

        document.getElementById("btnSalvarProduto").style.display="none";

        document.getElementById("modalProduto").style.display="flex";

    }catch(erro){

        console.error(erro);

        Toast.erro("Erro ao carregar produto.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   PREENCHER FORMULÁRIO
========================================================== */

function preencherFormulario(produto){

    document.getElementById("produtoId").value =
        produto.id ?? "";

    document.getElementById("codigo").value =
        produto.codigo ?? "";

    document.getElementById("referencia").value =
        produto.referencia ?? "";

    document.getElementById("codigoBarras").value =
        produto.codigoBarras ?? "";

    document.getElementById("descricao").value =
        produto.descricao ?? "";

    document.getElementById("categoriaId").value =
        produto.categoriaId ?? "";

    document.getElementById("marca").value =
        produto.marca ?? "";

    document.getElementById("unidade").value =
        produto.unidade ?? "UN";

    document.getElementById("ativo").value =
        String(produto.ativo);

    document.getElementById("estoqueAtual").value =
        produto.estoqueAtual ?? 0;

    document.getElementById("estoqueMinimo").value =
        produto.estoqueMinimo ?? 0;

    document.getElementById("estoqueMaximo").value =
        produto.estoqueMaximo ?? 0;

    document.getElementById("localizacao").value =
        produto.localizacao ?? "";

    document.getElementById("valorCompra").value =
        produto.valorCompra ?? 0;

    document.getElementById("valorVenda").value =
        produto.valorVenda ?? 0;

    document.getElementById("fornecedorId").value =
        produto.fornecedorId ?? "";

    document.getElementById("ncm").value =
        produto.ncm ?? "";

    document.getElementById("cest").value =
        produto.cest ?? "";

    document.getElementById("cfop").value =
        produto.cfop ?? "";

    document.getElementById("cst").value =
        produto.cst ?? "";

    document.getElementById("origem").value =
        produto.origem ?? "";

    document.getElementById("aliquotaIcms").value =
        produto.aliquotaIcms ?? "";

    document.getElementById("observacoes").value =
        produto.observacoes ?? "";

    document.getElementById("informacoesInternas").value =
        produto.informacoesInternas ?? "";

    calcularMargemLucro();

}

/* ==========================================================
   HABILITAR / DESABILITAR FORMULÁRIO
========================================================== */

function habilitarFormulario(habilitado){

    const campos = document.querySelectorAll(

        "#formProduto input, #formProduto select, #formProduto textarea"

    );

    campos.forEach(campo=>{

        if(campo.id==="produtoId") return;

        campo.disabled = !habilitado;

    });

}


/* ==========================================================
   VALIDAR FORMULÁRIO
========================================================== */

function validarFormularioProduto(){

    if(document.getElementById("codigo").value.trim()===""){

        Toast.aviso("Informe o código do produto.");

        document.getElementById("codigo").focus();

        return false;

    }

    if(document.getElementById("descricao").value.trim()===""){

        Toast.aviso("Informe a descrição.");

        document.getElementById("descricao").focus();

        return false;

    }

    if(document.getElementById("categoriaId").value===""){

        Toast.aviso("Selecione uma categoria.");

        document.getElementById("categoriaId").focus();

        return false;

    }

    if(document.getElementById("valorVenda").value===""){

        Toast.aviso("Informe o valor de venda.");

        document.getElementById("valorVenda").focus();

        return false;

    }

    return true;

}

/* ==========================================================
   CALCULAR LUCRO E MARGEM
========================================================== */

function configurarCalculoMargem(){

    document
        .getElementById("valorCompra")
        .addEventListener("input",calcularMargemLucro);

    document
        .getElementById("valorVenda")
        .addEventListener("input",calcularMargemLucro);

}

function calcularMargemLucro(){

    const compra =
        parseFloat(document.getElementById("valorCompra").value)||0;

    const venda =
        parseFloat(document.getElementById("valorVenda").value)||0;

    const lucro = venda-compra;

    const margem = compra>0
        ? (lucro/compra)*100
        :0;

    document.getElementById("lucro").value=

        lucro.toLocaleString("pt-BR",{

            style:"currency",

            currency:"BRL"

        });

    document.getElementById("margemLucro").value=

        margem.toFixed(2)+" %";

}

/* ==========================================================
   SALVAR PRODUTO
========================================================== */

async function salvarProduto(event){

    event.preventDefault();

    if(!validarFormularioProduto()){

        return;

    }

    const dto = montarDTOProduto();

    try{

        mostrarLoading();

        if(produtoEditando){

            await api.put(

                `/produtos/${produtoEditando}`,

                dto

            );

            Toast.sucesso("Produto atualizado com sucesso.");

        }else{

            await api.post(

                "/produtos",

                dto

            );

            Toast.sucesso("Produto cadastrado com sucesso.");

        }

        fecharModalProduto();

        carregarProdutos();

    }catch(erro){

        console.error(erro);

        Toast.erro("Erro ao salvar produto.");

    }finally{

        ocultarLoading();

    }

}

/* ==========================================================
   MONTAR DTO
========================================================== */

function montarDTOProduto(){

    return{

        codigo:
            document.getElementById("codigo").value,

        referencia:
            document.getElementById("referencia").value,

        codigoBarras:
            document.getElementById("codigoBarras").value,

        descricao:
            document.getElementById("descricao").value,

        categoriaId:
            document.getElementById("categoriaId").value,

        marca:
            document.getElementById("marca").value,

        unidade:
            document.getElementById("unidade").value,

        ativo:
            document.getElementById("ativo").value==="true",

        estoqueAtual:
            Number(document.getElementById("estoqueAtual").value),

        estoqueMinimo:
            Number(document.getElementById("estoqueMinimo").value),

        estoqueMaximo:
            Number(document.getElementById("estoqueMaximo").value),

        localizacao:
            document.getElementById("localizacao").value,

        valorCompra:
            Number(document.getElementById("valorCompra").value),

        valorVenda:
            Number(document.getElementById("valorVenda").value),

        fornecedorId:
            document.getElementById("fornecedorId").value || null,

        ncm:
            document.getElementById("ncm").value,

        cest:
            document.getElementById("cest").value,

        cfop:
            document.getElementById("cfop").value,

        cst:
            document.getElementById("cst").value,

        origem:
            document.getElementById("origem").value,

        aliquotaIcms:
            Number(document.getElementById("aliquotaIcms").value),

        observacoes:
            document.getElementById("observacoes").value,

        informacoesInternas:
            document.getElementById("informacoesInternas").value

    };

}

/* ==========================================================
   FECHAR MODAL AO CLICAR FORA
========================================================== */

window.addEventListener("click", (event) => {

    const modal = document.getElementById("modalProduto");

    if (event.target === modal) {

        fecharModalProduto();

    }

});

/* ==========================================================
   ATALHO ESC
========================================================== */

document.addEventListener("keydown", (event) => {

    if (event.key === "Escape") {

        fecharModalProduto();

    }

});

/* ==========================================================
   CARREGAR CATEGORIAS
========================================================== */

async function carregarCategoriasModal() {

    try {

        const categorias = await api.get("/categorias");

        const select = document.getElementById("categoriaId");

        select.innerHTML = `
            <option value="">Selecione...</option>
        `;

        categorias.forEach(categoria => {

            select.innerHTML += `
                <option value="${categoria.id}">
                    ${categoria.nome}
                </option>
            `;

        });

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao carregar categorias.");

    }

}

/* ==========================================================
   CARREGAR FORNECEDORES
========================================================== */

async function carregarFornecedoresModal() {

    try {

        const fornecedores = await api.get("/fornecedores");

        const select = document.getElementById("fornecedorId");

        select.innerHTML = `
            <option value="">Selecione...</option>
        `;

        fornecedores.forEach(fornecedor => {

            select.innerHTML += `
                <option value="${fornecedor.id}">
                    ${fornecedor.nome}
                </option>
            `;

        });

    } catch (erro) {

        console.error(erro);

        Toast.erro("Erro ao carregar fornecedores.");

    }

}

/* ==========================================================
   RESET COMPLETO
========================================================== */

function resetFormularioProduto() {

    document.getElementById("formProduto").reset();

    document.getElementById("produtoId").value = "";

    produtoEditando = null;

    selecionarAba("geral");

    habilitarFormulario(true);

    document.getElementById("btnSalvarProduto").style.display = "inline-flex";

}

/* ==========================================================
   ABRIR MODAL
========================================================== */

async function abrirModalProduto() {

    resetFormularioProduto();

    await carregarCategoriasModal();

    await carregarFornecedoresModal();

    document.getElementById("tituloModalProduto").innerHTML = `
        <i class="bi bi-box-seam-fill"></i>
        Novo Produto
    `;

    document.getElementById("modalProduto").style.display = "flex";

}

/* ==========================================================
   FECHAR MODAL
========================================================== */

function fecharModalProduto() {

    document.getElementById("modalProduto").style.display = "none";

    resetFormularioProduto();

}

/* ==========================================================
   INICIALIZAÇÃO
========================================================== */

document.addEventListener("DOMContentLoaded", () => {

    configurarEventosModal();

});

/* ==========================================================
   FIM DO ARQUIVO
========================================================== */
