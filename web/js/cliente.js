document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const clientesTable = document.getElementById('clientesTable');
    const rows = clientesTable.querySelectorAll('tbody tr');
    
    // Actualizar estadísticas en tiempo real
    function updateStats(filteredCount) {
        const totalElement = document.getElementById('stat-total');
        const corporativosElement = document.getElementById('stat-corporativos');
        const individualesElement = document.getElementById('stat-individuales');
        
        // Contar clientes corporativos e individuales en los resultados filtrados
        let corporativos = 0;
        let individuales = 0;
        
        rows.forEach(row => {
            if (!row.classList.contains('no-data') && row.style.display !== 'none') {
                const tipo = row.getAttribute('data-tipo');
                if (tipo === 'corporativo') {
                    corporativos++;
                } else {
                    individuales++;
                }
            }
        });
        
        // Actualizar los elementos del DOM
        if (filteredCount !== undefined) {
            totalElement.textContent = filteredCount;
        }
        corporativosElement.textContent = corporativos;
        individualesElement.textContent = individuales;
    }
    
    // Función de búsqueda
    function searchClientes() {
        const searchTerm = searchInput.value.toLowerCase();
        let visibleCount = 0;
        
        rows.forEach(row => {
            if (row.classList.contains('no-data')) return;
            
            const dni = row.getAttribute('data-dni');
            const nombre = row.getAttribute('data-nombre');
            const correo = row.getAttribute('data-correo');
            
            if (dni.includes(searchTerm) || nombre.includes(searchTerm) || correo.includes(searchTerm)) {
                row.style.display = '';
                visibleCount++;
            } else {
                row.style.display = 'none';
            }
        });
        
        // Mostrar mensaje si no hay resultados
        const noDataRow = clientesTable.querySelector('tr.no-data');
        if (visibleCount === 0 && !searchTerm) {
            noDataRow.style.display = '';
        } else if (visibleCount === 0) {
            noDataRow.style.display = '';
            noDataRow.querySelector('p').textContent = 'No se encontraron clientes';
        } else {
            noDataRow.style.display = 'none';
        }
        
        updateStats(visibleCount);
    }
    
    // Event listener para el campo de búsqueda
    searchInput.addEventListener('input', searchClientes);
    
    // Inicializar estadísticas
    updateStats();
});