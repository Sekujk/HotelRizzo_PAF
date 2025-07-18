<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.TipoHabitacionDTO" %>
<%@ page session="true" %>
<%
    List<TipoHabitacionDTO> tipos = (List<TipoHabitacionDTO>) request.getAttribute("tipos");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nueva Tarifa Especial - Hotel Rizzo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/utils/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tarifasespeciales/tarifaespecial_crear.css">
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
                <span class="user-name"><%= session.getAttribute("usuarioLogueado") %></span>
                <span class="user-role"><%= rolUsuario %></span>
            </div>
        </div>
        <form action="logout" method="post" class="logout-form">
            <button type="submit" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i>
            </button>
        </form>
    </div>
</header>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <div class="nav-section">
                <h3>Gestión Principal</h3>
                <ul>
                    <li><a href="reservas" class="nav-link">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Reservas</span>
                    </a></li>
                    <li><a href="clientes" class="nav-link">
                        <i class="fas fa-id-card"></i>
                        <span>Clientes</span>
                    </a></li>
                    <li><a href="habitaciones" class="nav-link active">
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
            <h1><i class="fas fa-tag"></i> Nueva Tarifa Especial</h1>
            <p class="dashboard-subtitle">Registre una nueva tarifa promocional o de temporada</p>
        </div>

        <div class="create-form-container">
            <form action="tarifaespecial_crear" method="post" class="create-form">
                <div class="form-group">
                    <label for="nombre" class="form-label">
                        <i class="fas fa-tag"></i> Nombre de la Tarifa
                    </label>
                    <input type="text" id="nombre" name="nombre" class="form-input" 
                           required placeholder="Ej. Promo Verano 2023">
                </div>

                <div class="form-group">
                    <label for="idTipoHabitacion" class="form-label">
                        <i class="fas fa-bed"></i> Tipo de Habitación
                    </label>
                    <select id="idTipoHabitacion" name="idTipoHabitacion" class="form-input" required>
                        <option value="" disabled selected>Seleccione un tipo</option>
                        <% for (TipoHabitacionDTO tipo : tipos) { %>
                            <option value="<%= tipo.getIdTipo() %>"><%= tipo.getNombre() %></option>
                        <% } %>
                    </select>
                </div>

                <div class="form-columns">
                    <div class="form-group">
                        <label for="fechaInicio" class="form-label">
                            <i class="fas fa-calendar-day"></i> Fecha Inicio
                        </label>
                        <input type="date" id="fechaInicio" name="fechaInicio" 
                               class="form-input" required min="<%= java.time.LocalDate.now() %>">
                    </div>

                    <div class="form-group">
                        <label for="fechaFin" class="form-label">
                            <i class="fas fa-calendar-times"></i> Fecha Fin
                        </label>
                        <input type="date" id="fechaFin" name="fechaFin" 
                               class="form-input" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="precioEspecial" class="form-label">
                        <i class="fas fa-money-bill-wave"></i> Precio Especial (S/)
                    </label>
                    <div class="input-with-icon">
                        <span class="input-icon">S/</span>
                        <input type="number" id="precioEspecial" name="precioEspecial" 
                               class="form-input" step="0.01" required min="0" placeholder="0.00">
                    </div>
                </div>

                <div class="form-group">
                    <label for="tipoTarifa" class="form-label">
                        <i class="fas fa-star"></i> Tipo de Tarifa
                    </label>
                    <select id="tipoTarifa" name="tipoTarifa" class="form-input" required>
                        <option value="" disabled selected>Seleccione un tipo</option>
                        <option value="Promoción">Promoción</option>
                        <option value="Temporada Alta">Temporada Alta</option>
                        <option value="Temporada Baja">Temporada Baja</option>
                        <option value="Evento Especial">Evento Especial</option>
                    </select>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Guardar Tarifa
                    </button>
                    <a href="tarifaespecial" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Cancelar
                    </a>
                </div>
            </form>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
    <p class="version">Versión 1.0.0</p>
</footer>

<script>
    // Validación para asegurar que fecha fin sea posterior a fecha inicio
    document.getElementById('fechaInicio').addEventListener('change', function() {
        document.getElementById('fechaFin').min = this.value;
    });
</script>
</body>
</html>