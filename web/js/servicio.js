document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("buscador-servicio");
    const filas = document.querySelectorAll(".tabla-servicios tbody tr");

    input.addEventListener("input", () => {
        const texto = input.value.toLowerCase();

        filas.forEach(fila => {
            const contenido = fila.textContent.toLowerCase();
            fila.style.display = contenido.includes(texto) ? "" : "none";
        });
    });
});