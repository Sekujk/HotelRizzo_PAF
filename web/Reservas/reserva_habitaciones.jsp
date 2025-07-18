<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="Model.*" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("/login.jsp");
        return;
    }
    
    // Obtener datos de la sesión
    ClienteDTO cliente = (ClienteDTO) session.getAttribute("clienteReserva");
    LocalDate fechaEntrada = (LocalDate) session.getAttribute("fechaEntrada");
    LocalDate fechaSalida = (LocalDate) session.getAttribute("fechaSalida");
    Integer numHuespedes = (Integer) session.getAttribute("numHuespedes");
    String observaciones = (String) session.getAttribute("observaciones");
    
    String error = (String) request.getAttribute("error");
    List<HabitacionDTO> habitacionesDisponibles = (List<HabitacionDTO>) request.getAttribute("habitacionesDisponibles");
    List<TipoHabitacionDTO> tiposHabitacion = (List<TipoHabitacionDTO>) request.getAttribute("tiposHabitacion");
    
    if (cliente == null || fechaEntrada == null || fechaSalida == null || numHuespedes == null) {
        response.sendRedirect("reserva_crear");
        return;
    }
    
    // Calcular noches
    long noches = fechaEntrada.until(fechaSalida).getDays();
    
    System.out.println("🏨 JSP Habitaciones Debug:");
    System.out.println("   - Cliente: " + cliente.getNombre());
    System.out.println("   - Fechas: " + fechaEntrada + " al " + fechaSalida);
    System.out.println("   - Noches: " + noches);
    System.out.println("   - Huéspedes: " + numHuespedes);
    System.out.println("   - Habitaciones disponibles: " + (habitacionesDisponibles != null ? habitacionesDisponibles.size() : "null"));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Seleccionar Habitaciones - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .habitaciones-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .habitacion-card {
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            padding: 20px;
            background: white;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        
        .habitacion-card:hover {
            border-color: #4CAF50;
            box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
        }
        
        .habitacion-card.selected {
            border-color: #4CAF50;
            background: #f8fff8;
            box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
        }
        
        .habitacion-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .habitacion-numero {
            font-size: 1.2em;
            font-weight: bold;
            color: #333;
        }
        
        .habitacion-checkbox {
            transform: scale(1.2);
        }
        
        .habitacion-tipo {
            font-size: 1em;
            color: #666;
            margin-bottom: 10px;
        }
        
        .habitacion-detalles {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 15px;
        }
        
        .habitacion-precio {
            font-size: 1.1em;
            font-weight: bold;
            color: #4CAF50;
        }
        
        .habitacion-capacidad {
            font-size: 0.9em;
            color: #666;
        }
        
        .reserva-resumen {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 30px;
        }
        
        .resumen-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .resumen-item {
            text-align: center;
        }
        
        .resumen-label {
            font-size: 0.9em;
            color: #666;
            margin-bottom: 5px;
        }
        
        .resumen-valor {
            font-size: 1.1em;
            font-weight: bold;
            color: #333;
        }
        
        .total-estimado {
            background: #4CAF50;
            color: white;
            padding: 15px;
            border-radius: 8px;
            text-align: center;
            margin-top: 20px;
        }
        
        .total-label {
            font-size: 0.9em;
            opacity: 0.9;
        }
        
        .total-valor {
            font-size: 1.5em;
            font-weight: bold;
            margin-top: 5px;
        }
        
        .no-habitaciones {
            text-align: center;
            padding: 40px;
            background: #fff3cd;
            border-radius: 10px;
            border: 1px solid #ffeaa7;
        }
        
        .no-habitaciones i {
            font-size: 3em;
            color: #f39c12;
            margin-bottom: 15px;
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
        <a href="../logout" class="btn-logout">
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

            <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
            <div class="nav-section">
                <h3>Administración</h3>
                <ul>
                    <li><a href="empleado" class="nav-link">
                        <i class="fas fa-users"></i>
                        <span>Empleados</span>
                    </a></li>
                </ul>
            </div>
            <% } %>
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-bed"></i> Seleccionar Habitaciones</h1>
            <p class="dashboard-subtitle">Elige las habitaciones para la reserva</p>
        </div>

        <!-- Progress Steps -->
        <div class="progress-steps">
            <div class="step completed">
                <div class="step-icon">
                    <i class="fas fa-check"></i>
                </div>
                <span>Cliente</span>
            </div>
            <div class="step completed">
                <div class="step-icon">
                    <i class="fas fa-check"></i>
                </div>
                <span>Fechas</span>
            </div>
            <div class="step active">
                <div class="step-icon">
                    <i class="fas fa-bed"></i>
                </div>
                <span>Habitaciones</span>
            </div>
        </div>

        <!-- Resumen de la Reserva -->
        <div class="reserva-resumen">
            <h3><i class="fas fa-info-circle"></i> Resumen de la Reserva</h3>
            <div class="resumen-grid">
                <div class="resumen-item">
                    <div class="resumen-label">Cliente</div>
                    <div class="resumen-valor"><%= cliente.getNombre() %> <%= cliente.getApellido() %></div>
                </div>
                <div class="resumen-item">
                    <div class="resumen-label">Check-in</div>
                    <div class="resumen-valor"><%= fechaEntrada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></div>
                </div>
                <div class="resumen-item">
                    <div class="resumen-label">Check-out</div>
                    <div class="resumen-valor"><%= fechaSalida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></div>
                </div>
                <div class="resumen-item">
                    <div class="resumen-label">Noches</div>
                    <div class="resumen-valor"><%= noches %></div>
                </div>
                <div class="resumen-item">
                    <div class="resumen-label">Huéspedes</div>
                    <div class="resumen-valor"><%= numHuespedes %></div>
                </div>
            </div>
        </div>

        <!-- Error Message -->
        <% if (error != null) { %>
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i>
            <span><%= error %></span>
        </div>
        <% } %>

        <!-- Formulario de Selección -->
        <form method="post" action="reserva_habitaciones" id="formHabitaciones">
            <input type="hidden" name="action" value="finalizar_reserva">
            
            <div class="form-container">
                <div class="form-header">
                    <h2><i class="fas fa-bed"></i> Habitaciones Disponibles</h2>
                    <p>Selecciona las habitaciones para esta reserva (mínimo 1)</p>
                </div>
                
                <% if (habitacionesDisponibles != null && !habitacionesDisponibles.isEmpty()) { %>
                    <div class="habitaciones-grid">
                        <% 
                        for (HabitacionDTO habitacion : habitacionesDisponibles) {
                            // Buscar el tipo de habitación
                            TipoHabitacionDTO tipoHabitacion = null;
                            if (tiposHabitacion != null) {
                                for (TipoHabitacionDTO tipo : tiposHabitacion) {
                                    if (tipo.getIdTipo() == habitacion.getIdTipo()) {
                                        tipoHabitacion = tipo;
                                        break;
                                    }
                                }
                            }
                            
                            double precioNoche = tipoHabitacion != null ? tipoHabitacion.getPrecioBase() : 0.0;
                            double precioTotal = precioNoche * noches;
                        %>
                        <div class="habitacion-card" onclick="toggleHabitacion(<%= habitacion.getIdHabitacion() %>)">
                            <div class="habitacion-header">
                                <div class="habitacion-numero">
                                    <i class="fas fa-door-open"></i> Habitación <%= habitacion.getNumero() %>
                                </div>
                                <input type="checkbox" 
                                       class="habitacion-checkbox" 
                                       name="habitaciones" 
                                       value="<%= habitacion.getIdHabitacion() %>" 
                                       id="hab_<%= habitacion.getIdHabitacion() %>"
                                       onchange="actualizarTotal()">
                            </div>
                            
                            <div class="habitacion-tipo">
                                <i class="fas fa-tag"></i> 
                                <%= tipoHabitacion != null ? tipoHabitacion.getNombre() : "Tipo no disponible" %>
                            </div>
                            
                            <% if (tipoHabitacion != null && tipoHabitacion.getDescripcion() != null) { %>
                            <div class="habitacion-descripcion">
                                <%= tipoHabitacion.getDescripcion() %>
                            </div>
                            <% } %>
                            
                            <div class="habitacion-detalles">
                                <div class="habitacion-precio">
                                    S/. <%= String.format("%.2f", precioNoche) %>/noche
                                    <br>
                                    <small>Total: S/. <%= String.format("%.2f", precioTotal) %></small>
                                </div>
                                <div class="habitacion-capacidad">
                                    <i class="fas fa-users"></i> 
                                    Hasta <%= tipoHabitacion != null ? tipoHabitacion.getCapacidadPersonas() : "N/A" %> personas
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    
                    <!-- Total Estimado -->
                    <div class="total-estimado" id="totalEstimado" style="display: none;">
                        <div class="total-label">Total Estimado</div>
                        <div class="total-valor" id="totalValor">S/. 0.00</div>
                        <small>Por <%= noches %> <%= noches == 1 ? "noche" : "noches" %></small>
                    </div>
                    
                <% } else { %>
                    <div class="no-habitaciones">
                        <i class="fas fa-exclamation-triangle"></i>
                        <h3>No hay habitaciones disponibles</h3>
                        <p>No se encontraron habitaciones disponibles para las fechas seleccionadas con capacidad para <%= numHuespedes %> <%= numHuespedes == 1 ? "huésped" : "huéspedes" %>.</p>
                        <div style="margin-top: 20px;">
                            <a href="reserva_crear?step=reserva" class="btn btn-primary">
                                <i class="fas fa-calendar-alt"></i> Cambiar Fechas
                            </a>
                        </div>
                    </div>
                <% } %>
                
                <!-- Botones de Acción -->
                <div class="form-actions">
                    <a href="reserva_crear?step=reserva" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Cambiar Fechas
                    </a>
                    
                    <% if (habitacionesDisponibles != null && !habitacionesDisponibles.isEmpty()) { %>
                    <button type="submit" class="btn btn-primary" id="btnFinalizar" disabled>
                        <i class="fas fa-check"></i> Finalizar Reserva
                    </button>
                    <% } %>
                </div>
            </div>
        </form>

        <!-- Botón para volver -->
        <div class="back-section">
            <a href="../reservas" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Volver a Reservas
            </a>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<script>
document.addEventListener('DOMContentLoaded', function() {
    console.log('🏨 Reserva Habitaciones JS cargado');
    actualizarTotal();
});

// Datos de precios para JavaScript
const preciosHabitaciones = {
    <% 
    if (habitacionesDisponibles != null && tiposHabitacion != null) {
        for (int i = 0; i < habitacionesDisponibles.size(); i++) {
            HabitacionDTO hab = habitacionesDisponibles.get(i);
            TipoHabitacionDTO tipo = null;
            for (TipoHabitacionDTO t : tiposHabitacion) {
                if (t.getIdTipo() == hab.getIdTipo()) {
                    tipo = t;
                    break;
                }
            }
            double precio = tipo != null ? tipo.getPrecioBase() : 0.0;
            
            out.print(hab.getIdHabitacion() + ": " + precio);
            if (i < habitacionesDisponibles.size() - 1) out.print(",");
        }
    }
    %>
};

const noches = <%= noches %>;

function toggleHabitacion(idHabitacion) {
    const checkbox = document.getElementById('hab_' + idHabitacion);
    const card = checkbox.closest('.habitacion-card');
    
    checkbox.checked = !checkbox.checked;
    
    if (checkbox.checked) {
        card.classList.add('selected');
    } else {
        card.classList.remove('selected');
    }
    
    actualizarTotal();
}

function actualizarTotal() {
    const checkboxes = document.querySelectorAll('input[name="habitaciones"]:checked');
    const btnFinalizar = document.getElementById('btnFinalizar');
    const totalEstimado = document.getElementById('totalEstimado');
    const totalValor = document.getElementById('totalValor');
    
    let total = 0;
    
    checkboxes.forEach(checkbox => {
        const idHabitacion = parseInt(checkbox.value);
        const precioNoche = preciosHabitaciones[idHabitacion] || 0;
        total += precioNoche * noches;
    });
    
    // Mostrar/ocultar total y habilitar botón
    if (checkboxes.length > 0) {
        totalEstimado.style.display = 'block';
        totalValor.textContent = 'S/. ' + total.toFixed(2);
        if (btnFinalizar) btnFinalizar.disabled = false;
    } else {
        totalEstimado.style.display = 'none';
        if (btnFinalizar) btnFinalizar.disabled = true;
    }
    
    console.log('💰 Total actualizado: S/. ' + total.toFixed(2) + ' (' + checkboxes.length + ' habitaciones)');
}

// Prevenir envío del formulario si no hay habitaciones seleccionadas
document.getElementById('formHabitaciones').addEventListener('submit', function(e) {
    const checkboxes = document.querySelectorAll('input[name="habitaciones"]:checked');
    
    if (checkboxes.length === 0) {
        e.preventDefault();
        alert('Debe seleccionar al menos una habitación.');
        return false;
    }
    
    console.log('✅ Enviando reserva con ' + checkboxes.length + ' habitaciones');
    return true;
});
</script>

</body>
</html>