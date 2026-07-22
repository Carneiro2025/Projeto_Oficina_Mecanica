/**
 * ==========================================================
 * Projeto.......: OficinaPRO
 * Arquivo.......: loading.js
 * ==========================================================
 */

const loading=document.getElementById("loading");

function mostrarLoading(){

    if(!loading) return;

    loading.classList.add("show");

}

function ocultarLoading(){

    if(!loading) return;

    loading.classList.remove("show");

}
