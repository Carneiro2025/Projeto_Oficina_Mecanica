/**
 * Carrega o menu lateral.
 */

document.addEventListener("DOMContentLoaded", async () => {

    const sidebar = document.getElementById("sidebar");

    if (!sidebar) return;

    const resposta = await fetch("components/sidebar.html");

    sidebar.innerHTML = await resposta.text();

});
