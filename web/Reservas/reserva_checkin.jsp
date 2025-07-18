<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    String habitaciones = (String) request.getAttribute("habitaciones");
    
    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) session.removeAttribute("mensaje");
    
    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }
    
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Check-In - Reserva <%= reserva.getNumeroReserva() %></title>
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .checkin-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .reserva-summary {
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 4px 20px rgba(76, 175, 80, 0.3);
        }
        
        .summary-header {
            text-align: center;
            margin-bottom: 20px;
        }
        
        .summary-header h2 {
            margin: 0;
            font-size: 1.8em;
        }
        
        .summary-subtitle {
            opacity: 0.9;
            font-size: 1.1em;
            margin-top: 5px;
        }
        
        .summary-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .summary-item {
            background: rgba(255, 255, 255, 0.15);
            padding: 15px;
            border-radius: 8px;
            text-align: center;
            backdrop-filter: blur(10px);
        }
        
        .summary-label {
            font-size: 0.9em;
            opacity: 0.9;
            margin-bottom: 5px;
        }
        
        .summary-value {
            font-size: 1.2em;
            font-weight: bold;
        }
        
        .checkin-form {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 20px rgba(0,0,0,0.1);
            border: 1px solid #e0e0e0;
        }
        
        .form-header {
            text-align: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .form-header h3 {
            margin: 0;
            color: #333;
            font-size: 1.4em;
        }
        
        .form-header p {
            color: #666;
            margin: 10px 0 0 0;
        }
        
        .checklist {
            background: #f8f9fa;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 25px;
        }
        
        .checklist h4 {
            margin: 0 0 15px 0;
            color: #333;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .checklist-items {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .checklist-items li {
            padding: 8px 0;
            display: flex;
            align-items: center;
            gap: 10px;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .checklist-items li:last-child {
            border-bottom: none;
        }
        
        .checklist-icon {
            color: #4CAF50;
            font-size: 1.1em;
            width: 20px;
        }
        
        .observaciones-group {
            margin: 25px 0;
        }
        
        .observaciones-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }
        
        .observaciones-group textarea {
            width: 100%;
            min-height: 100px;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-family: inherit;
            resize: vertical;
        }
        
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 500;
            transition: all 0.3s ease;
            cursor: pointer;
            font-size: 1em;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
        }
        
        .btn-primary:hover {
            background: linear-gradient(135deg, #45a049, #3d8b40);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
            transform: translateY(-2px);
        }
        
        .alert {
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 6px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .alert-success {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        
        .alert-error {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
        }
        
        .status-confirmada {
            background: #e8f5e8;
            color: #2e7d32;
            border: 1px solid #4CAF50;
        }
        
        @media (max-width: 768px) {
            .summary-grid {
                grid-template-columns: 1fr;
            }
            
            .form-actions {
                flex-direction: column;
                align-items: center;
            }
            
            .btn {
                width: 100%;
                max-width: 300px;
                justify-content: center;
            }
        }
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
            
            <div class="nav-section">
                <h3>Servicios</h3>
                <ul>
                    <li><a href="servicio" class="nav-link">
                        <i class="fas fa-concierge-bell"></i>
                        <span>Servicios</span>
                    </a></li>
                    <li><a href="producto" class="nav-link">
                        <i class="fas fa-shopping-cart"></i>
                        <span>Productos</span>
                    </a></li>
                </ul>
            </div>
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-door-open"></i> Check-In de Huésped</h1>
            <p class="dashboard-subtitle">Confirmar llegada y registro de huésped</p>
        </div>

        <div class="checkin-container">
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

            <!-- Resumen de la Reserva -->
            <div class="reserva-summary">
                <div class="summary-header">
                    <h2><i class="fas fa-calendar-check"></i> Reserva <%= reserva.getNumeroReserva() %></h2>
                    <div class="summary-subtitle">
                        <span class="status-badge status-confirmada">
                            <i class="fas fa-check"></i> <%= reserva.getEstado() %>
                        </span>
                    </div>
                </div>
                
                <div class="summary-grid">
                    <div class="summary-item">
                        <div class="summary-label">Huésped Principal</div>
                        <div class="summary-value"><%= reserva.getNombreCliente() %></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Fecha de Entrada</div>
                        <div class="summary-value"><%= reserva.getFechaEntrada().format(dateFormatter) %></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Fecha de Salida</div>
                        <div class="summary-value"><%= reserva.getFechaSalida().format(dateFormatter) %></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Número de Huéspedes</div>
                        <div class="summary-value"><%= reserva.getNumHuespedes() %> personas</div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Habitaciones Asignadas</div>
                        <div class="summary-value"><%= habitaciones != null ? habitaciones : "No asignadas" %></div>
                    </div>
                    <div class="summary-item">
                        <div class="summary-label">Total Hospedaje</div>
                        <div class="summary-value">S/. <%= String.format("%.2f", reserva.getMontoTotal()) %></div>
                    </div>
                </div>
            </div>

            <!-- Formulario de Check-In -->
            <div class="checkin-form">
                <div class="form-header">
                    <h3><i class="fas fa-clipboard-check"></i> Confirmar Check-In</h3>
                    <p>Verifique la información y proceda con el registro del huésped</p>
                </div>

                <!-- Lista de Verificación -->
                <div class="checklist">
                    <h4><i class="fas fa-tasks"></i> Lista de Verificación</h4>
                    <ul class="checklist-items">
                        <li>
                            <i class="fas fa-check checklist-icon"></i>
                            <span>Documentos de identidad verificados</span>
                        </li>
                        <li>
                            <i class="fas fa-check checklist-icon"></i>
                            <span>Habitaciones preparadas y limpias</span>
                        </li>
                        <li>
                            <i class="fas fa-check checklist-icon"></i>
                            <span>Información del huésped confirmada</span>
                        </li>
                        <li>
                            <i class="fas fa-check checklist-icon"></i>
                            <span>Llaves de habitación preparadas</span>
                        </li>
                        <li>
                            <i class="fas fa-check checklist-icon"></i>
                            <span>Servicios del hotel explicados</span>
                        </li>
                    </ul>
                </div>

                <!-- Formulario -->
                <form method="post" action="reserva_checkin" id="formCheckin">
                    <input type="hidden" name="id_reserva" value="<%= reserva.getIdReserva() %>">

                    <!-- Observaciones -->
                    <div class="observaciones-group">
                        <label for="observaciones">
                            <i class="fas fa-comment"></i> Observaciones del Check-In
                        </label>
                        <textarea name="observaciones" id="observaciones" 
                                  placeholder="Registre cualquier observación relevante sobre el check-in, solicitudes especiales del huésped, condición de las habitaciones, etc. (opcional)"></textarea>
                    </div>

                    <!-- Información adicional -->
                    <div class="checklist">
                        <h4><i class="fas fa-info-circle"></i> Información Importante</h4>
                        <div style="color: #666; line-height: 1.6;">
                            <p><strong>Al confirmar el check-in:</strong></p>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>La reserva cambiará al estado "CheckIn"</li>
                                <li>Las habitaciones se marcarán como "Ocupada"</li>
                                <li>Se habilitará el registro de servicios y productos</li>
                                <li>Se registrará la hora exacta de entrada</li>
                            </ul>
                        </div>
                    </div>

                    <!-- Botones de Acción -->
                    <div class="form-actions">
                        <a href="reservas" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Cancelar
                        </a>
                        
                        <button type="submit" class="btn btn-primary" id="btnConfirmar">
                            <i class="fas fa-door-open"></i> Confirmar Check-In
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<script>
// Confirmación antes de enviar
document.getElementById('formCheckin').addEventListener('submit', function(e) {
    const huespedNombre = '<%= reserva.getNombreCliente() %>';
    const habitaciones = '<%= habitaciones %>';
    
    const fechaActual = new Date().toLocaleDateString();
    const horaActual = new Date().toLocaleTimeString();
    
    const mensaje = '¿Confirmar el check-in del huésped ' + huespedNombre + '?\n\n' +
                   'Habitaciones: ' + habitaciones + '\n' +
                   'Fecha: ' + fechaActual + '\n' +
                   'Hora: ' + horaActual + '\n\n' +
                   'Esta acción no se puede deshacer.';
    
    if (!confirm(mensaje)) {
        e.preventDefault();
        return;
    }
    
    // Deshabilitar botón para evitar doble envío
    const btnConfirmar = document.getElementById('btnConfirmar');
    btnConfirmar.disabled = true;
    btnConfirmar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
});

// Auto-focus en el textarea si está visible
document.addEventListener('DOMContentLoaded', function() {
    const observaciones = document.getElementById('observaciones');
    if (observaciones) {
        observaciones.focus();
    }
});
</script>

</body>
</html>