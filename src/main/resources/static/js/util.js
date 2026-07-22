/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: util.js
 * Descrição.....: Biblioteca de funções utilitárias.
 * Autor.........: Rafael Carneiro
 * Versão........: 1.0.0
 * =====================================================================
 */

class Util {

    /**
     * Exibe mensagem de sucesso.
     */
    sucesso(mensagem) {
        alert("✔ " + mensagem);
    }

    /**
     * Exibe mensagem de erro.
     */
    erro(mensagem) {
        alert("❌ " + mensagem);
    }

    /**
     * Exibe mensagem de alerta.
     */
    alerta(mensagem) {
        alert("⚠ " + mensagem);
    }

    /**
     * Exibe confirmação.
     */
    confirmar(mensagem) {
        return confirm(mensagem);
    }

    /**
     * Formata valor monetário.
     */
    moeda(valor) {

        return Number(valor).toLocaleString("pt-BR", {

            style: "currency",

            currency: "BRL"

        });

    }

    /**
     * Formata data.
     */
    data(data) {

        return new Date(data).toLocaleDateString("pt-BR");

    }

    /**
     * Formata data e hora.
     */
    dataHora(data) {

        return new Date(data).toLocaleString("pt-BR");

    }

    /**
     * Formata CPF.
     */
    cpf(cpf) {

        cpf = cpf.replace(/\D/g, "");

        return cpf.replace(

            /(\d{3})(\d{3})(\d{3})(\d{2})/,

            "$1.$2.$3-$4"

        );

    }

    /**
     * Formata telefone.
     */
    telefone(numero) {

        numero = numero.replace(/\D/g, "");

        if (numero.length === 11) {

            return numero.replace(

                /(\d{2})(\d{5})(\d{4})/,

                "($1) $2-$3"

            );

        }

        return numero;

    }

    /**
     * Remove espaços.
     */
    limpar(texto) {

        return texto.trim();

    }

    /**
     * Verifica e-mail.
     */
    emailValido(email) {

        return /\S+@\S+\.\S+/.test(email);

    }

    /**
     * Verifica CPF simples.
     */
    cpfValido(cpf) {

        cpf = cpf.replace(/\D/g, "");

        return cpf.length === 11;

    }

    /**
     * Mostra Loading.
     */
    mostrarLoading() {

        const loading = document.getElementById("loading");

        if (loading) {

            loading.style.display = "flex";

        }

    }

    /**
     * Oculta Loading.
     */
    esconderLoading() {

        const loading = document.getElementById("loading");

        if (loading) {

            loading.style.display = "none";

        }

    }

    /**
     * Limpa formulário.
     */
    limparFormulario(idFormulario) {

        const formulario = document.getElementById(idFormulario);

        if (formulario) {

            formulario.reset();

        }

    }

    /**
     * Obtém parâmetro da URL.
     */
    parametro(nome) {

        const params = new URLSearchParams(window.location.search);

        return params.get(nome);

    }

}

const util = new Util();
