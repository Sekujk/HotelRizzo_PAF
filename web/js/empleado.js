window.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("buscador-empleado");
    const tabla = document.querySelector(".data-table");

    if (!input || !tabla) return;

    const filas = tabla.querySelectorAll("tbody tr");

    input.addEventListener("input", () => {
        const texto = input.value.trim().toLowerCase();

        filas.forEach(fila => {
            const contenido = fila.textContent.trim().toLowerCase();
            fila.style.display = contenido.includes(texto) ? "" : "none";
        });
    });
});
