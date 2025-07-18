<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
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
    List<ServicioDTO> serviciosDisponibles = (List<ServicioDTO>) request.getAttribute("serviciosDisponibles");
    
    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) session.removeAttribute("mensaje");
    
    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Agregar Servicio - Reserva <%= reserva.getNumeroReserva() %></title>
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
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
            <h1><i class="fas fa-concierge-bell"></i> Agregar Servicio</h1>
            <p class="dashboard-subtitle">Reserva: <%= reserva.getNumeroReserva() %> - <%= reserva.getNombreCliente() %></p>
        </div>

        <!-- Mensajes -->
        <% if (mensaje != null) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            <span><%= mensaje %></span>
        </div>
        <% } %>

        <% if (error != null) { %>
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i>
            <span><%= error %></span>
        </div>
        <% } %>

        <!-- Formulario -->
        <div class="form-container">
            <div class="form-header">
                <h2><i class="fas fa-plus-circle"></i> Seleccionar Servicio</h2>
            </div>

            <form method="post" action="reserva_servicios" class="form-grid">
                <input type="hidden" name="action" value="agregar_servicio">
                <input type="hidden" name="id_reserva" value="<%= reserva.getIdReserva() %>">

                <!-- Selección de Servicio -->
                <div class="form-group span-2">
                    <label for="id_servicio" class="form-label">
                        <i class="fas fa-concierge-bell"></i> Servicio *
                    </label>
                    <select name="id_servicio" id="id_servicio" class="form-input" required onchange="actualizarPrecio()">
                        <option value="">Seleccione un servicio...</option>
                        <% if (serviciosDisponibles != null && !serviciosDisponibles.isEmpty()) { %>
                            <% for (ServicioDTO servicio : serviciosDisponibles) { %>
                            <option value="<%= servicio.getIdServicio() %>" 
                                    data-precio="<%= servicio.getPrecioUnitario() %>"
                                    data-descripcion="<%= servicio.getDescripcion() != null ? servicio.getDescripcion() : "" %>">
                                <%= servicio.getNombre() %> - S/. <%= String.format("%.2f", servicio.getPrecioUnitario()) %>
                            </option>
                            <% } %>
                        <% } else { %>
                            <option value="">No hay servicios disponibles</option>
                        <% } %>
                    </select>
                </div>

                <!-- Cantidad -->
                <div class="form-group">
                    <label for="cantidad" class="form-label">
                        <i class="fas fa-calculator"></i> Cantidad *
                    </label>
                    <input type="number" name="cantidad" id="cantidad" class="form-input" 
                           min="0.1" step="0.1" value="1" required onchange="calcularTotal()">
                </div>

                <!-- Precio Unitario (readonly) -->
                <div class="form-group">
                    <label for="precio_unitario" class="form-label">
                        <i class="fas fa-tag"></i> Precio Unitario
                    </label>
                    <input type="text" id="precio_unitario" class="form-input" readonly placeholder="S/. 0.00">
                </div>

                <!-- Total (calculado) -->
                <div class="form-group span-2">
                    <label for="total" class="form-label">
                        <i class="fas fa-calculator"></i> Total
                    </label>
                    <input type="text" id="total" class="form-input total-field" readonly placeholder="S/. 0.00">
                </div>

                <!-- Descripción del servicio -->
                <div class="form-group span-2" id="descripcion-container" style="display: none;">
                    <label class="form-label">
                        <i class="fas fa-info-circle"></i> Descripción
                    </label>
                    <div id="descripcion-texto" class="form-description"></div>
                </div>

                <!-- Observaciones -->
                <div class="form-group span-2">
                    <label for="observaciones" class="form-label">
                        <i class="fas fa-comment"></i> Observaciones
                    </label>
                    <textarea name="observaciones" id="observaciones" class="form-input" 
                              rows="3" placeholder="Observaciones adicionales (opcional)"></textarea>
                </div>

                <!-- Botones -->
                <div class="form-actions span-2">
                    <a href="reserva_servicios?id=<%= reserva.getIdReserva() %>" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Cancelar
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Agregar Servicio
                    </button>
                </div>
            </form>
        </div>

        <!-- Debug Info -->
        <% if (serviciosDisponibles != null) { %>
        <div class="debug-info" style="margin-top: 20px; padding: 10px; background: #f0f0f0; border-radius: 5px;">
            <strong>Debug:</strong> Se encontraron <%= serviciosDisponibles.size() %> servicios disponibles.
        </div>
        <% } %>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<style>
.total-field {
    background: linear-gradient(135deg, #4CAF50, #45a049) !important;
    color: white !important;
    font-weight: bold !important;
    font-size: 1.1em !important;
}

.form-description {
    background: #f8f9fa;
    border: 1px solid #e0e0e0;
    padding: 10px;
    border-radius: 4px;
    font-style: italic;
    color: #666;
}

.debug-info {
    font-size: 0.9em;
    color: #666;
}

.span-2 {
    grid-column: span 2;
}
</style>

<script>
function actualizarPrecio() {
    const select = document.getElementById('id_servicio');
    const precioInput = document.getElementById('precio_unitario');
    const descripcionContainer = document.getElementById('descripcion-container');
    const descripcionTexto = document.getElementById('descripcion-texto');
    
    if (select.value) {
        const option = select.options[select.selectedIndex];
        const precio = parseFloat(option.dataset.precio);
        const descripcion = option.dataset.descripcion;
        
        precioInput.value = 'S/. ' + precio.toFixed(2);
        
        if (descripcion && descripcion.trim() !== '') {
            descripcionTexto.textContent = descripcion;
            descripcionContainer.style.display = 'block';
        } else {
            descripcionContainer.style.display = 'none';
        }
        
        calcularTotal();
    } else {
        precioInput.value = 'S/. 0.00';
        descripcionContainer.style.display = 'none';
        calcularTotal();
    }
}

function calcularTotal() {
    const select = document.getElementById('id_servicio');
    const cantidadInput = document.getElementById('cantidad');
    const totalInput = document.getElementById('total');
    
    if (select.value && cantidadInput.value) {
        const option = select.options[select.selectedIndex];
        const precio = parseFloat(option.dataset.precio);
        const cantidad = parseFloat(cantidadInput.value);
        const total = precio * cantidad;
        
        totalInput.value = 'S/. ' + total.toFixed(2);
    } else {
        totalInput.value = 'S/. 0.00';
    }
}

// Validación antes de enviar
document.querySelector('form').addEventListener('submit', function(e) {
    const servicio = document.getElementById('id_servicio').value;
    const cantidad = document.getElementById('cantidad').value;
    
    if (!servicio) {
        e.preventDefault();
        alert('Debe seleccionar un servicio');
        return;
    }
    
    if (!cantidad || parseFloat(cantidad) <= 0) {
        e.preventDefault();
        alert('La cantidad debe ser mayor a 0');
        return;
    }
});
</script>

</body>
</html>