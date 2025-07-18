/**
 * Gestión de Reservas - Hotel Rizzo (Versión Simplificada)
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('🏨 Sistema de Reservas cargado');
    
    initializeBasicFunctions();
    initializeActionButtons();
    initializeFilters();
});

/**
 * Inicializar funciones básicas
 */
function initializeBasicFunctions() {
    // Actualizar contadores
    updateCounters();
    
    // Destacar reservas de hoy
    highlightTodayReservations();
}

/**
 * Inicializar botones de acción
 */
function initializeActionButtons() {
    // Botones de cancelación
    const cancelButtons = document.querySelectorAll('.btn-action.cancel');
    cancelButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const confirmed = confirm('¿Estás seguro de cancelar esta reserva?');
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });
    
    // Botones de check-in
    const checkinButtons = document.querySelectorAll('.btn-action.checkin');
    checkinButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const confirmed = confirm('¿Realizar check-in para esta reserva?');
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });
    
    // Botones de check-out
    const checkoutButtons = document.querySelectorAll('.btn-action.checkout');
    checkoutButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const confirmed = confirm('¿Realizar check-out para esta reserva?');
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });
}

/**
 * Inicializar filtros y búsqueda
 */
function initializeFilters() {
    // Filtro por estado
    const filterSelect = document.querySelector('.filter-select');
    if (filterSelect) {
        filterSelect.addEventListener('change', function() {
            console.log('Aplicando filtro:', this.value);
            // El formulario se envía automáticamente
        });
    }
    
    // Búsqueda
    const searchForm = document.querySelector('.search-form');
    const searchInput = document.querySelector('.search-box input[name="criterio"]');
    
    if (searchForm && searchInput) {
        searchForm.addEventListener('submit', function(e) {
            const query = searchInput.value.trim();
            if (query.length === 0) {
                e.preventDefault();
                alert('Por favor ingresa un criterio de búsqueda');
                searchInput.focus();
            }
        });
    }
}

/**
 * Actualizar contadores de estadísticas
 */
function updateCounters() {
    const rows = document.querySelectorAll('#reservasTable tbody tr:not(.no-data)');
    
    let counters = {
        total: rows.length,
        pendiente: 0,
        confirmada: 0,
        checkin: 0,
        checkout: 0,
        cancelada: 0
    };
    
    rows.forEach(row => {
        const estado = row.dataset.estado;
        if (estado && counters.hasOwnProperty(estado)) {
            counters[estado]++;
        }
    });
    
    // Actualizar elementos en el DOM
    updateStatElement('stat-total', counters.total);
    updateStatElement('stat-pendientes', counters.pendiente);
    updateStatElement('stat-confirmadas', counters.confirmada);
    updateStatElement('stat-checkedin', counters.checkin);
    updateStatElement('stat-checkedout', counters.checkout);
    updateStatElement('stat-canceladas', counters.cancelada);
    
    console.log('Contadores actualizados:', counters);
}

/**
 * Actualizar elemento de estadística
 */
function updateStatElement(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = value;
    }
}

/**
 * Destacar reservas de hoy
 */
function highlightTodayReservations() {
    const today = new Date();
    const todayStr = formatDate(today);
    
    const rows = document.querySelectorAll('#reservasTable tbody tr');
    
    rows.forEach(row => {
        const dateItems = row.querySelectorAll('.date-item');
        
        dateItems.forEach(dateItem => {
            const dateText = dateItem.textContent.trim();
            if (dateText === todayStr) {
                row.style.backgroundColor = '#fef3c7';
                row.style.borderLeft = '4px solid #f59e0b';
            }
        });
    });
}

/**
 * Formatear fecha a DD/MM/YYYY
 */
function formatDate(date) {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

/**
 * Función para exportar tabla a CSV (opcional)
 */
function exportToCSV() {
    const table = document.getElementById('reservasTable');
    if (!table) return;
    
    const rows = table.querySelectorAll('tr');
    const csvData = [];
    
    rows.forEach((row, index) => {
        if (index === 0) {
            // Cabecera
            const headers = [];
            row.querySelectorAll('th').forEach(th => {
                if (!th.textContent.includes('Acciones')) {
                    headers.push(th.textContent.trim());
                }
            });
            csvData.push(headers.join(','));
        } else if (!row.classList.contains('no-data')) {
            // Datos
            const rowData = [];
            const cells = row.querySelectorAll('td');
            
            for (let i = 0; i < cells.length - 1; i++) { // Excluir última columna (acciones)
                let cellText = cells[i].textContent.trim();
                cellText = cellText.replace(/,/g, ';'); // Reemplazar comas
                rowData.push(`"${cellText}"`);
            }
            csvData.push(rowData.join(','));
        }
    });
    
    // Crear y descargar archivo
    const csvContent = csvData.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', `reservas_${formatDate(new Date()).replace(/\//g, '-')}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
    
    console.log('Archivo CSV exportado');
}

/**
 * Función para imprimir tabla
 */
function printTable() {
    const table = document.getElementById('reservasTable').cloneNode(true);
    
    // Remover columna de acciones
    table.querySelectorAll('th:last-child, td:last-child').forEach(cell => {
        cell.remove();
    });
    
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Reservas - Hotel Rizzo</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                h1 { text-align: center; color: #333; }
                table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; font-weight: bold; }
                .status-badge { padding: 2px 6px; border-radius: 4px; font-size: 12px; }
            </style>
        </head>
        <body>
            <h1>📋 Reservas - Hotel Rizzo</h1>
            <p>Fecha: ${formatDate(new Date())} - Hora: ${new Date().toLocaleTimeString()}</p>
            ${table.outerHTML}
        </body>
        </html>
    `);
    
    printWindow.document.close();
    printWindow.print();
    
    console.log('Tabla enviada a impresión');
}

/**
 * Atajos de teclado básicos
 */
document.addEventListener('keydown', function(e) {
    // Ctrl + F: Enfocar búsqueda
    if (e.ctrlKey && e.key === 'f') {
        e.preventDefault();
        const searchInput = document.querySelector('.search-box input');
        if (searchInput) {
            searchInput.focus();
            searchInput.select();
        }
    }
    
    // Escape: Limpiar búsqueda
    if (e.key === 'Escape') {
        const searchInput = document.querySelector('.search-box input');
        if (searchInput && searchInput.value) {
            searchInput.value = '';
            searchInput.focus();
        }
    }
});

/**
 * Mostrar/ocultar loading simple
 */
function showLoading() {
    document.body.style.cursor = 'wait';
}

function hideLoading() {
    document.body.style.cursor = 'default';
}

// Funciones disponibles globalmente para botones
window.exportToCSV = exportToCSV;
window.printTable = printTable;