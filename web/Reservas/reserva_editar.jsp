<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="Model.*" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    ReservaDTO reserva = (ReservaDTO) request.getAttribute("reserva");
    ClienteDTO cliente = (ClienteDTO) request.getAttribute("cliente");
    String nombreEmpleado = (String) request.getAttribute("nombreEmpleado");
    List<HabitacionDetalleDTO> habitacionesAsignadas = (List<HabitacionDetalleDTO>) request.getAttribute("habitacionesAsignadas");
    Boolean tieneConsumos = (Boolean) request.getAttribute("tieneConsumos");
    
    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) session.removeAttribute("mensaje");
    
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }
    
    if (tieneConsumos == null) tieneConsumos = false;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Reserva <%= reserva.getNumeroReserva() %> - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/utils/base.css">
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .form-container { max-width: 900px; margin: 0 auto; padding: 20px; }
        .reserva-header { background: linear-gradient(135deg, #ff9800, #f57c00); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; }
        .form-section { background: white; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        .form-label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px; }
        .form-input:focus { border-color: #ff9800; outline: none; }
        .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; }
        .habitacion-item { background: #f8f9fa; padding: 15px; border-radius: 5px; border: 1px solid #e0e0e0; margin-bottom: 10px; }
        .habitacion-numero { font-weight: bold; color: #333; }
        .checkbox-container { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 10px; margin-top: 10px; }
        .checkbox-item { display: flex; align-items: center; padding: 10px; background: #f8f9fa; border-radius: 5px; border: 1px solid #e0e0e0; }
        .checkbox-item input { margin-right: 10px; }
        .alert { padding: 15px; margin-bottom: 20px; border-radius: 5px; }
        .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .alert-warning { background: #fff3cd; color: #856404; border: 1px solid #ffeaa7; }
        .btn { padding: 10px 20px; border: none; border-radius: 5px; text-decoration: none; display: inline-block; margin: 5px; cursor: pointer; }
        .btn-primary { background: #ff9800; color: white; }
        .btn-secondary { background: #6c757d; color: white; }
        .btn:hover { opacity: 0.9; }
        .form-actions { text-align: center; margin-top: 20px; }
        .required { color: red; }
        .hidden { display: none; }
    </style>
</head>
<body>

<header class="navbar">
    <div class="logo-section">
        <i class="fas fa-hotel"></i>
        <span class="logo-text">Hotel Rizzo</span>
    </div>
    <div class="user-section">
        <div class="user-info">
            <i class="fas fa-user-circle"></i>
            <div class="user-details">
                <span class="user-name"><%= usuario %></span>
                <span class="user-role"><%= rolUsuario %></span>
            </div>
        </div>
        <a href="logout" class="btn-logout">
            <i class="fas fa-sign-out-alt"></i>
        </a>
    </div>
</header>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <div class="nav-section">
                <h3>Gestión Principal</h3>
                <ul>
                    <li><a href="reservas" class="nav-link active">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Reservas</span>
                    </a></li>
                    <li><a href="clientes" class="nav-link">
                        <i class="fas fa-users"></i>
                        <span>Clientes</span>
                    </a></li>
                    <li><a href="habitaciones" class="nav-link">
                        <i class="fas fa-bed"></i>
                        <span>Habitaciones</span>
                    </a></li>
                </ul>
            </div>
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-edit"></i> Editar Reserva</h1>
            <p class="dashboard-subtitle">Modificar información de la reserva</p>
        </div>

        <div class="form-container">
            <!-- Mensajes -->
            <% if (mensaje != null) { %>
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> <%= mensaje %>
            </div>
            <% } %>

            <% if (error != null) { %>
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i> <%= error %>
            </div>
            <% } %>

            <!-- Header de la Reserva -->
            <div class="reserva-header">
                <h2><i class="fas fa-edit"></i> Editando: <%= reserva.getNumeroReserva() %></h2>
                <p><strong>Cliente:</strong> <%= cliente != null ? cliente.getNombre() + " " + cliente.getApellido() : "Cliente no encontrado" %></p>
                <p><strong>Empleado:</strong> <%= nombreEmpleado %></p>
                <p><strong>Estado:</strong> <%= reserva.getEstado() %></p>
            </div>

            <!-- Advertencias -->
            <% if (tieneConsumos) { %>
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <strong>¡Atención!</strong> Esta reserva tiene servicios consumidos. Los cambios pueden afectar el cálculo total.
            </div>
            <% } %>

            <!-- Formulario -->
            <form method="post" action="reserva_editar" id="formEditarReserva">
                <input type="hidden" name="id_reserva" value="<%= reserva.getIdReserva() %>">

                <!-- Información Actual -->
                <div class="form-section">
                    <h3><i class="fas fa-info-circle"></i> Información Actual</h3>
                    <div class="form-grid">
                        <div><strong>Entrada:</strong> <%= reserva.getFechaEntrada().format(displayFormatter) %></div>
                        <div><strong>Salida:</strong> <%= reserva.getFechaSalida().format(displayFormatter) %></div>
                        <div><strong>Huéspedes:</strong> <%= reserva.getNumHuespedes() %></div>
                        <div><strong>Total:</strong> S/. <%= String.format("%.2f", reserva.getMontoTotal()) %></div>
                    </div>
                </div>

                <!-- Fechas y Huéspedes -->
                <div class="form-section">
                    <h3><i class="fas fa-calendar-alt"></i> Fechas y Huéspedes</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="fecha_entrada" class="form-label">Fecha de Entrada <span class="required">*</span></label>
                            <input type="date" name="fecha_entrada" id="fecha_entrada" class="form-input" 
                                   value="<%= reserva.getFechaEntrada().format(dateFormatter) %>" required>
                        </div>
                        <div class="form-group">
                            <label for="fecha_salida" class="form-label">Fecha de Salida <span class="required">*</span></label>
                            <input type="date" name="fecha_salida" id="fecha_salida" class="form-input" 
                                   value="<%= reserva.getFechaSalida().format(dateFormatter) %>" required>
                        </div>
                        <div class="form-group">
                            <label for="num_huespedes" class="form-label">Número de Huéspedes <span class="required">*</span></label>
                            <input type="number" name="num_huespedes" id="num_huespedes" class="form-input" 
                                   value="<%= reserva.getNumHuespedes() %>" min="1" max="20" required>
                        </div>
                        <div class="form-group">
                            <label for="descuento" class="form-label">Descuento (%)</label>
                            <input type="number" name="descuento" id="descuento" class="form-input" 
                                   value="0" min="0" max="100" step="0.01">
                        </div>
                    </div>
                </div>

                <!-- Habitaciones Actuales -->
                <div class="form-section">
                    <h3><i class="fas fa-bed"></i> Habitaciones Asignadas</h3>
                    
                    <% if (habitacionesAsignadas != null && !habitacionesAsignadas.isEmpty()) { %>
                        <% for (HabitacionDetalleDTO hab : habitacionesAsignadas) { %>
                        <div class="habitacion-item">
                            <div class="habitacion-numero">Habitación <%= hab.getNumeroHabitacion() %></div>
                            <div>Tipo: <%= hab.getTipoHabitacion() %> | Capacidad: <%= hab.getCapacidadPersonas() %> personas | Precio: S/. <%= String.format("%.2f", hab.getPrecioNoche()) %>/noche</div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <p>No hay habitaciones asignadas</p>
                    <% } %>

                    <!-- Opción para cambiar habitaciones -->
                    <div style="margin-top: 15px;">
                        <label>
                            <input type="checkbox" id="cambiar_habitaciones" name="cambiar_habitaciones" value="true" onchange="toggleHabitaciones()">
                            Cambiar habitaciones asignadas
                        </label>
                    </div>

                    <!-- Selector de nuevas habitaciones -->
                    <div id="selectorHabitaciones" class="hidden">
                        <h4>Habitaciones Disponibles</h4>
                        <div id="habitacionesDisponibles">
                            <p style="text-align: center; color: #666; padding: 20px;">
                                <i class="fas fa-search"></i><br>Las habitaciones se cargarán automáticamente
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Observaciones -->
                <div class="form-section">
                    <h3><i class="fas fa-comment-alt"></i> Observaciones</h3>
                    <div class="form-group">
                        <label for="observaciones" class="form-label">Observaciones de la Reserva</label>
                        <textarea name="observaciones" id="observaciones" class="form-input" rows="3"><%= reserva.getObservaciones() != null ? reserva.getObservaciones() : "" %></textarea>
                    </div>
                    <div class="form-group">
                        <label for="motivo_cambio" class="form-label">Motivo del Cambio <span class="required">*</span></label>
                        <textarea name="motivo_cambio" id="motivo_cambio" class="form-input" rows="3" required placeholder="Explique el motivo de los cambios"></textarea>
                    </div>
                </div>

                <!-- Botones -->
                <div class="form-actions">
                    <a href="reserva_detalle?id=<%= reserva.getIdReserva() %>" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Cancelar
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Guardar Cambios
                    </button>
                </div>
            </form>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<script>
function toggleHabitaciones() {
    const checkbox = document.getElementById('cambiar_habitaciones');
    const selector = document.getElementById('selectorHabitaciones');
    
    if (checkbox.checked) {
        selector.classList.remove('hidden');
        buscarHabitacionesDisponibles();
    } else {
        selector.classList.add('hidden');
        limpiarHabitacionesSeleccionadas();
    }
}

function buscarHabitacionesDisponibles() {
    const fechaEntrada = document.getElementById('fecha_entrada').value;
    const fechaSalida = document.getElementById('fecha_salida').value;
    const numHuespedes = document.getElementById('num_huespedes').value;
    const idReserva = '<%= reserva.getIdReserva() %>';
    
    if (!fechaEntrada || !fechaSalida) {
        mostrarMensajeHabitaciones('Seleccione fechas para buscar habitaciones');
        return;
    }
    
    mostrarMensajeHabitaciones('<i class="fas fa-spinner fa-spin"></i> Buscando habitaciones...');
    
    const params = new URLSearchParams({
        action: 'buscar_habitaciones',
        fecha_entrada: fechaEntrada,
        fecha_salida: fechaSalida,
        num_huespedes: numHuespedes,
        id_reserva: idReserva
    });
    
    fetch('reserva_editar?' + params.toString())
        .then(response => {
            console.log('Response status:', response.status); // Debug
            return response.json();
        })
        .then(data => {
            console.log('Data received:', data); // Debug
            if (data.success) {
                mostrarHabitacionesDisponibles(data.habitaciones);
            } else {
                mostrarMensajeHabitaciones('Error: ' + (data.message || 'No se pudieron cargar habitaciones'));
            }
        })
        .catch(error => {
            console.error('Error completo:', error); // Debug
            mostrarMensajeHabitaciones('Error de conexión');
        });
}

function mostrarHabitacionesDisponibles(habitaciones) {
    const container = document.getElementById('habitacionesDisponibles');
    
    console.log('Habitaciones recibidas:', habitaciones); // Debug
    
    if (!habitaciones || habitaciones.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #666; padding: 20px;"><i class="fas fa-bed"></i><br>No hay habitaciones disponibles</p>';
        return;
    }
    
    let html = '<div class="checkbox-container">';
    habitaciones.forEach((hab, index) => {
        console.log(`Habitación ${index}:`, hab); // Debug
        html += `
            <div class="checkbox-item">
                <input type="checkbox" name="habitaciones" value="${hab.idHabitacion}" id="hab_${hab.idHabitacion}">
                <div>
                    <div><strong>Habitación ${hab.numero || 'N/A'}</strong></div>
                    <div>Tipo: ${hab.tipoHabitacion || 'N/A'}</div>
                    <div>Capacidad: ${hab.capacidad || 'N/A'} personas</div>
                    <div>S/. ${hab.precio || '0.00'}/noche</div>
                </div>
            </div>
        `;
    });
    html += '</div>';
    container.innerHTML = html;
}

function mostrarMensajeHabitaciones(mensaje) {
    document.getElementById('habitacionesDisponibles').innerHTML = `<p style="text-align: center; color: #666; padding: 20px;">${mensaje}</p>`;
}

function limpiarHabitacionesSeleccionadas() {
    const checkboxes = document.querySelectorAll('input[name="habitaciones"]');
    checkboxes.forEach(cb => cb.checked = false);
    mostrarMensajeHabitaciones('<i class="fas fa-search"></i><br>Las habitaciones se cargarán automáticamente');
}

function onFechaChange() {
    const cambiarHabs = document.getElementById('cambiar_habitaciones').checked;
    if (cambiarHabs) {
        buscarHabitacionesDisponibles();
    }
}

// Validaciones del formulario
document.getElementById('formEditarReserva').addEventListener('submit', function(e) {
    const fechaEntrada = new Date(document.getElementById('fecha_entrada').value);
    const fechaSalida = new Date(document.getElementById('fecha_salida').value);
    const motivoCambio = document.getElementById('motivo_cambio').value.trim();
    const cambiarHabs = document.getElementById('cambiar_habitaciones').checked;
    
    if (fechaSalida <= fechaEntrada) {
        e.preventDefault();
        alert('La fecha de salida debe ser posterior a la fecha de entrada');
        return;
    }
    
    if (!motivoCambio) {
        e.preventDefault();
        alert('Debe especificar el motivo del cambio');
        return;
    }
    
    if (cambiarHabs) {
        const habitacionesSeleccionadas = document.querySelectorAll('input[name="habitaciones"]:checked');
        if (habitacionesSeleccionadas.length === 0) {
            e.preventDefault();
            alert('Debe seleccionar al menos una habitación');
            return;
        }
    }
    
    if (!confirm('¿Está seguro de que desea guardar los cambios?')) {
        e.preventDefault();
        return;
    }
});

// Configurar fechas y listeners
document.addEventListener('DOMContentLoaded', function() {
    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById('fecha_entrada').min = hoy;
    
    document.getElementById('fecha_entrada').addEventListener('change', function() {
        const fechaEntrada = this.value;
        if (fechaEntrada) {
            const fechaMinimaSalida = new Date(fechaEntrada);
            fechaMinimaSalida.setDate(fechaMinimaSalida.getDate() + 1);
            document.getElementById('fecha_salida').min = fechaMinimaSalida.toISOString().split('T')[0];
        }
        onFechaChange();
    });
    
    document.getElementById('fecha_salida').addEventListener('change', onFechaChange);
    document.getElementById('num_huespedes').addEventListener('change', onFechaChange);
});
</script>

</body>
</html>