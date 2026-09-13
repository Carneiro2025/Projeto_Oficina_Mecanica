/**
 * =====================================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: toast.js
 * Descrição.....: Sistema de notificações rápidas (toast) usado pelas
 *                  telas de Clientes, Produtos, Veículos e Fornecedores.
 * =====================================================================
 */

class ToastManager {

    /**
     * Retorna o container onde os toasts são inseridos,
     * criando-o automaticamente caso a página não o tenha.
     */
    obterContainer() {

        let container = document.getElementById("toastContainer");

        if (!container) {

            container = document.createElement("div");
            container.id = "toastContainer";
            document.body.appendChild(container);

        }

        return container;

    }

    /**
     * Cria e exibe um toast.
     */
    exibir(mensagem, tipo) {

        const container = this.obterContainer();

        const toast = document.createElement("div");

        toast.className = `toast-item toast-${tipo}`;

        toast.textContent = mensagem;

        container.appendChild(toast);

        // Força o reflow antes de animar a entrada.
        requestAnimationFrame(() => {
            toast.classList.add("show");
        });

        const tempo = (typeof CONFIG !== "undefined" && CONFIG.MESSAGE_TIME)
            ? CONFIG.MESSAGE_TIME
            : 3000;

        setTimeout(() => {

            toast.classList.remove("show");

            setTimeout(() => toast.remove(), 300);

        }, tempo);

    }

    sucesso(mensagem) {
        this.exibir(mensagem, "sucesso");
    }

    erro(mensagem) {
        this.exibir(mensagem, "erro");
    }

    aviso(mensagem) {
        this.exibir(mensagem, "aviso");
    }

    info(mensagem) {
        this.exibir(mensagem, "info");
    }

}

const Toast = new ToastManager();
