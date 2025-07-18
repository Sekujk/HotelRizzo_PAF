<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.*" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("/login.jsp");
        return;
    }
    
    String step = (String) request.getAttribute("step");
    String error = (String) request.getAttribute("error");
    String dni = (String) request.getAttribute("dni");
    
    ClienteDTO cliente = (ClienteDTO) session.getAttribute("clienteReserva");
    List<TipoHabitacionDTO> tiposHabitacion = (List<TipoHabitacionDTO>) request.getAttribute("tiposHabitacion");
    
    System.out.println("🔍 JSP Debug:");
    System.out.println("   - Step: " + step);
    System.out.println("   - Cliente en sesión: " + (cliente != null ? cliente.getNombre() : "null"));
    System.out.println("   - Tipos habitación: " + (tiposHabitacion != null ? tiposHabitacion.size() : "null"));
    System.out.println("   - Error: " + error);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nueva Reserva - Hotel Rizzo</title>
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
            <h1><i class="fas fa-plus"></i> Nueva Reserva</h1>
            <p class="dashboard-subtitle">Proceso de creación de reserva</p>
        </div>

        <!-- Progress Steps -->
        <div class="progress-steps">
            <div class="step <%= (step == null || "cliente".equals(step)) ? "active" : "completed" %>">
                <div class="step-icon">
                    <i class="fas fa-user"></i>
                </div>
                <span>Cliente</span>
            </div>
            <div class="step <%= "reserva".equals(step) ? "active" : "" %>">
                <div class="step-icon">
                    <i class="fas fa-calendar"></i>
                </div>
                <span>Fechas</span>
            </div>
            <div class="step">
                <div class="step-icon">
                    <i class="fas fa-bed"></i>
                </div>
                <span>Habitaciones</span>
            </div>
        </div>

        <!-- Error Message -->
        <% if (error != null) { %>
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i>
            <span><%= error %></span>
        </div>
        <% } %>

        <!-- Step 1: Búsqueda de Cliente -->
        <% if (step == null || "cliente".equals(step)) { %>
        <div class="form-container">
            <div class="form-header">
                <h2><i class="fas fa-search"></i> Buscar Cliente</h2>
                <p>Ingresa el DNI para verificar si el cliente está registrado</p>
            </div>
            
            <form method="post" action="reserva_crear" class="search-form">
                <input type="hidden" name="action" value="buscar_cliente">
                
                <div class="form-group">
                    <label for="dni">DNI del Cliente:</label>
                    <div class="input-group">
                        <input type="text" id="dni" name="dni" required 
                               placeholder="Ej: 12345678" maxlength="8" 
                               pattern="[0-9]{8}" title="Ingrese 8 dígitos">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-search"></i> Buscar
                        </button>
                    </div>
                </div>
            </form>
        </div>
        <% } %>

        <!-- Step 2: Nuevo Cliente -->
        <% if ("nuevo_cliente".equals(step)) { %>
        <div class="form-container">
            <div class="form-header">
                <h2><i class="fas fa-user-plus"></i> Registrar Nuevo Cliente</h2>
                <p>El cliente no está registrado. Complete los datos para continuar.</p>
            </div>
            
            <form method="post" action="reserva_crear" class="client-form">
                <input type="hidden" name="action" value="crear_cliente">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="dni">DNI:</label>
                        <input type="text" id="dni" name="dni" required readonly
                               value="<%= dni != null ? dni : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="nombre">Nombre:</label>
                        <input type="text" id="nombre" name="nombre" required
                               placeholder="Nombre del cliente">
                    </div>
                    <div class="form-group">
                        <label for="apellido">Apellido:</label>
                        <input type="text" id="apellido" name="apellido" required
                               placeholder="Apellido del cliente">
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="telefono">Teléfono:</label>
                        <input type="tel" id="telefono" name="telefono"
                               placeholder="Ej: 987654321">
                    </div>
                    <div class="form-group">
                        <label for="correo">Correo Electrónico:</label>
                        <input type="email" id="correo" name="correo"
                               placeholder="correo@ejemplo.com">
                    </div>
                    <div class="form-group">
                        <label for="genero">Género:</label>
                        <select id="genero" name="genero">
                            <option value="">Seleccionar...</option>
                            <option value="M">Masculino</option>
                            <option value="F">Femenino</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="direccion">Dirección:</label>
                    <textarea id="direccion" name="direccion" rows="2"
                              placeholder="Dirección del cliente"></textarea>
                </div>
                
                <div class="form-actions">
                    <a href="reserva_crear" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Volver
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Guardar y Continuar
                    </button>
                </div>
            </form>
        </div>
        <% } %>

        <!-- Step 3: Datos de Reserva -->
        <% if ("reserva".equals(step) && cliente != null) { %>
        <div class="form-container">
            <div class="form-header">
                <h2><i class="fas fa-calendar-alt"></i> Datos de la Reserva</h2>
                <p>Cliente: <strong><%= cliente.getNombre() %> <%= cliente.getApellido() %></strong> - DNI: <%= cliente.getDni() %></p>
            </div>
            
            <% if (tiposHabitacion != null && !tiposHabitacion.isEmpty()) { %>
                <div class="habitaciones-info">
                    <h3>Tipos de Habitación Disponibles:</h3>
                    <div class="tipos-grid">
                        <% for (TipoHabitacionDTO tipo : tiposHabitacion) { %>
                        <div class="tipo-card">
                            <h4><%= tipo.getNombre() %></h4>
                            <p><%= tipo.getDescripcion() %></p>
                            <div class="tipo-details">
                                <span class="precio">S/. <%= String.format("%.2f", tipo.getPrecioBase()) %></span>
                                <span class="capacidad">Hasta <%= tipo.getCapacidadPersonas() %> personas</span>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
            <% } %>
            
            <form method="post" action="reserva_crear" class="reservation-form">
                <input type="hidden" name="action" value="crear_reserva">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha_entrada">Fecha de Entrada:</label>
                        <input type="date" id="fecha_entrada" name="fecha_entrada" required
                               min="<%= java.time.LocalDate.now() %>">
                    </div>
                    <div class="form-group">
                        <label for="fecha_salida">Fecha de Salida:</label>
                        <input type="date" id="fecha_salida" name="fecha_salida" required>
                    </div>
                    <div class="form-group">
                        <label for="num_huespedes">Número de Huéspedes:</label>
                        <select id="num_huespedes" name="num_huespedes" required>
                            <option value="">Seleccionar...</option>
                            <option value="1">1 huésped</option>
                            <option value="2">2 huéspedes</option>
                            <option value="3">3 huéspedes</option>
                            <option value="4">4 huéspedes</option>
                            <option value="5">5 huéspedes</option>
                            <option value="6">6+ huéspedes</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="observaciones">Observaciones (opcional):</label>
                    <textarea id="observaciones" name="observaciones" rows="3"
                              placeholder="Solicitudes especiales, comentarios..."></textarea>
                </div>
                
                <div class="form-actions">
                    <a href="reserva_crear" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Cambiar Cliente
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-arrow-right"></i> Seleccionar Habitaciones
                    </button>
                </div>
            </form>
        </div>
        <% } else if ("reserva".equals(step) && cliente == null) { %>
        <div class="form-container">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-triangle"></i>
                <span>Error: No hay cliente en sesión. Por favor reinicie el proceso.</span>
            </div>
            <div class="form-actions">
                <a href="reserva_crear" class="btn btn-primary">
                    <i class="fas fa-refresh"></i> Reiniciar Proceso
                </a>
            </div>
        </div>
        <% } %>

        <!-- Botón para volver -->
        <div class="back-section">
            <a href="reservas" class="btn btn-outline">
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
    console.log('🏨 Reserva Crear JS cargado');
    console.log('Step actual: <%= step %>');
    console.log('Cliente en sesión: <%= cliente != null %>');
    
    // Validación de fechas
    const fechaEntrada = document.getElementById('fecha_entrada');
    const fechaSalida = document.getElementById('fecha_salida');
    
    if (fechaEntrada && fechaSalida) {
        fechaEntrada.addEventListener('change', function() {
            const entrada = new Date(this.value);
            const minSalida = new Date(entrada);
            minSalida.setDate(minSalida.getDate() + 1);
            
            fechaSalida.min = minSalida.toISOString().split('T')[0];
            
            if (fechaSalida.value && new Date(fechaSalida.value) <= entrada) {
                fechaSalida.value = '';
            }
        });
    }
    
    // Validación de DNI
    const dniInput = document.getElementById('dni');
    if (dniInput && !dniInput.readOnly) {
        dniInput.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '');
            if (this.value.length > 8) {
                this.value = this.value.slice(0, 8);
            }
        });
    }
});
</script>

</body>
</html>