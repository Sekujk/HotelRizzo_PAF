document.addEventListener("DOMContentLoaded", () => {
    const filtroPiso = document.getElementById("filtro-piso");
    const filtroTipo = document.getElementById("filtro-tipo");
    const filtroEstado = document.getElementById("filtro-estado");
    const tabla = document.getElementById("habitacionesTable");
    
    // Verificar si los elementos existen antes de continuar
    if (!filtroPiso || !filtroTipo || !filtroEstado || !tabla) {
        console.error("No se encontraron todos los elementos necesarios para los filtros");
        return;
    }

    const aplicarFiltros = () => {
        const piso = filtroPiso.value;
        const tipo = filtroTipo.value.toLowerCase(); // Convertir a minúsculas para comparación insensible
        const estado = filtroEstado.value.toLowerCase();
        
        const filas = tabla.querySelectorAll("tbody tr:not(.no-data)");

        let filasVisibles = 0;

        filas.forEach(fila => {
            const pisoFila = fila.getAttribute("data-piso");
            const tipoFila = fila.getAttribute("data-tipo").toLowerCase();
            const estadoFila = fila.getAttribute("data-estado").toLowerCase();

            const cumplePiso = piso === "" || pisoFila === piso;
            const cumpleTipo = tipo === "" || tipoFila.includes(tipo);
            const cumpleEstado = estado === "" || estadoFila.includes(estado);

            if (cumplePiso && cumpleTipo && cumpleEstado) {
                fila.style.display = "";
                filasVisibles++;
            } else {
                fila.style.display = "none";
            }
        });

        // Manejar estado cuando no hay resultados
        const noDataRow = tabla.querySelector("tr.no-data");
        if (noDataRow) {
            noDataRow.style.display = filasVisibles > 0 ? "none" : "";
        }
    };

    // Aplicar filtros al cambiar cualquier select
    [filtroPiso, filtroTipo, filtroEstado].forEach(select => {
        select.addEventListener("change", aplicarFiltros);
    });

    // Aplicar filtros inicialmente por si hay valores por defecto
    aplicarFiltros();
});