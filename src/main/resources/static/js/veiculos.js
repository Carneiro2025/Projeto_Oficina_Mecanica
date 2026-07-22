/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: veiculos.js
 * Descrição.....: Controle da tela de Veículos
 * ==========================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    carregarVeiculos();

    configurarEventos();

});

/* ==========================================================
   EVENTOS
========================================================== */

function configurarEventos() {

    document
        .getElementById("btnNovo")
        .addEventListener("click", abrirModalNovoVeiculo);

    document
        .getElementById("btnPesquisar")
        .addEventListener("click", pesquisarVeiculos);

}

/* ==========================================================
   LISTAR VEÍCULOS
========================================================== */

async function carregarVeiculos() {

    try {

        mostrarLoading();

        const veiculos = await api.get("/veiculos");

        preencherTabela(veiculos);

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar veículos.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   TABELA
========================================================== */

function preencherTabela(lista) {

    const tbody = document.getElementById("tbodyVeiculos");

    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align:center;">
                    Nenhum veículo encontrado.
                </td>
            </tr>
        `;

        return;

    }

    lista.forEach(v => {

        tbody.innerHTML += `

        <tr>

            <td>${v.id}</td>

            <td>${v.placa}</td>

            <td>${v.marca}</td>

            <td>${v.modelo}</td>

            <td>${v.ano}</td>

            <td>${v.nomeCliente}</td>

            <td>

                <button
                    class="btn-action btn-view"
                    onclick="visualizarVeiculo(${v.id})">

                    <i class="bi bi-eye"></i>

                </button>

                <button
                    class="btn-action btn-edit"
                    onclick="editarVeiculo(${v.id})">

                    <i class="bi bi-pencil"></i>

                </button>

                <button
                    class="btn-action btn-delete"
                    onclick="excluirVeiculo(${v.id})">

                    <i class="bi bi-trash"></i>

                </button>

            </td>

        </tr>

        `;

    });

}

/* ==========================================================
   PESQUISA
========================================================== */

async function pesquisarVeiculos() {

    const texto = document
        .getElementById("txtPesquisar")
        .value
        .trim();

    try {

        mostrarLoading();

        const lista = await api.get(`/veiculos?pesquisa=${texto}`);

        preencherTabela(lista);

    } catch (error) {

        Toast.erro("Erro na pesquisa.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   NOVO
========================================================== */

function novoVeiculo(){

    abrirModalNovoVeiculo();

}

/* ==========================================================
   VISUALIZAR
========================================================== */

function visualizarVeiculo(id){

    abrirModalVisualizarVeiculo(id);

}

/* ==========================================================
   EDITAR
========================================================== */

function editarVeiculo(id){

    abrirModalEditarVeiculo(id);

}

/* ==========================================================
   EXCLUIR
========================================================== */

async function excluirVeiculo(id) {

    if (!confirm("Deseja excluir este veículo?")) {

        return;

    }

    try {

        await api.delete(`/veiculos/${id}`);

        Toast.sucesso("Veículo excluído.");

        carregarVeiculos();

    } catch (error) {

        Toast.erro("Erro ao excluir veículo.");

    }

}