/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: modal-usuario.js
 * Descrição.....: Modal de Usuários
 *
 * Observação: ao contrário de modal-veiculo.js (que acessa o DOM direto
 * no topo do arquivo — bug, porque o HTML do modal é injetado depois),
 * aqui tudo fica dentro de configurarEventosModalUsuario(), chamada pelo
 * usuarios.html só depois que modal-usuario.html já foi injetado no DOM.
 * ==========================================================
 */

let modalUsuario;
let formUsuario;
let btnSalvarUsuario;
let btnCancelarUsuario;
let btnFecharModalUsuario;

let usuarioEditando = null;

function configurarEventosModalUsuario() {

    modalUsuario = document.getElementById("modalUsuario");
    formUsuario = document.getElementById("formUsuario");

    btnSalvarUsuario = document.getElementById("btnSalvarUsuario");
    btnCancelarUsuario = document.getElementById("btnCancelarUsuario");
    btnFecharModalUsuario = document.getElementById("btnFecharModalUsuario");

    btnSalvarUsuario.addEventListener("click", salvarUsuario);

    btnCancelarUsuario.addEventListener("click", fecharModalUsuario);

    btnFecharModalUsuario.addEventListener("click", fecharModalUsuario);

}

/* ==========================================================
   NOVO
========================================================== */

function abrirModalNovoUsuario() {

    usuarioEditando = null;

    formUsuario.reset();

    document.getElementById("tituloModalUsuario").innerText = "Novo Usuário";

    exibirCampoSenha(true, true);

    modalUsuario.classList.add("show");

}

/* ==========================================================
   EDITAR
   (a API não permite alterar a senha pela edição — só o e-mail/nome/
   perfil; troca de senha é feita por endpoint separado, fora de escopo
   desta tela por enquanto)
========================================================== */

async function abrirModalEditarUsuario(id) {

    try {

        mostrarLoading();

        const usuario = await api.get(`/usuarios/${id}`);

        preencherFormularioUsuario(usuario);

        usuarioEditando = id;

        document.getElementById("tituloModalUsuario").innerText = "Editar Usuário";

        exibirCampoSenha(false, false);

        modalUsuario.classList.add("show");

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao carregar usuário.");

    } finally {

        ocultarLoading();

    }

}

/* ==========================================================
   FECHAR
========================================================== */

function fecharModalUsuario() {

    modalUsuario.classList.remove("show");

}

/* ==========================================================
   FORMULÁRIO
========================================================== */

function preencherFormularioUsuario(u) {

    document.getElementById("usuarioId").value = u.id;

    document.getElementById("nome").value = u.nome;

    document.getElementById("email").value = u.email;

    document.getElementById("perfil").value = u.perfil;

}

function exibirCampoSenha(exibir, obrigatorio) {

    document.getElementById("grupoSenha").style.display = exibir ? "block" : "none";

    document.getElementById("senha").required = obrigatorio;

    document.getElementById("senha").value = "";

}

function obterDadosUsuario() {

    return {
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        senha: document.getElementById("senha").value,
        perfil: document.getElementById("perfil").value
    };

}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarUsuario() {

    try {

        mostrarLoading();

        const dados = obterDadosUsuario();

        if (usuarioEditando == null) {

            await api.post("/usuarios", dados);

            Toast.sucesso("Usuário cadastrado com sucesso.");

        } else {

            // atualizar() não usa o campo senha
            const { senha, ...dadosAtualizar } = dados;

            await api.put(`/usuarios/${usuarioEditando}`, dadosAtualizar);

            Toast.sucesso("Usuário atualizado com sucesso.");

        }

        fecharModalUsuario();

        carregarUsuarios();

    } catch (error) {

        console.error(error);

        Toast.erro("Erro ao salvar usuário.");

    } finally {

        ocultarLoading();

    }

}
