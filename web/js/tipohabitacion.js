document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("buscador-tipohabitacion");

    if (!input) {
        console.warn("No se encontró el input de búsqueda.");
        return;
    }

    input.addEventListener("keyup", function () {
        const filtro = input.value.toLowerCase().trim();
        const filas = document.querySelectorAll(".data-table tbody tr");

        filas.forEach(fila => {
            const texto = fila.textContent.toLowerCase();
            fila.style.display = texto.includes(filtro) ? "" : "none";
        });
    });
});
