document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("buscador-empleado");
    const filas = document.querySelectorAll(".tabla-empleados tbody tr");

    input.addEventListener("input", () => {
        const texto = input.value.toLowerCase();

        filas.forEach(fila => {
            const contenido = fila.textContent.toLowerCase();
            fila.style.display = contenido.includes(texto) ? "" : "none";
        });
    });
});
