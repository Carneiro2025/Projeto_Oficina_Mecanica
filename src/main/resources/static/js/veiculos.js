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

        const resposta = await api.get("/veiculos");

        const veiculos = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

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

        // O back-end não tem um parâmetro genérico "pesquisa" — só
        // placa/modelo/clienteId/ativo — então mandamos o texto como
        // filtro de placa (o mais literal, já que a maioria das buscas
        // aqui é por placa).
        const resposta = await api.get(`/veiculos?placa=${encodeURIComponent(texto)}`);

        const lista = Array.isArray(resposta) ? resposta : (resposta.content ?? []);

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