<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.TipoHabitacionDTO" %>
<%@ page session="true" %>
<%
    TipoHabitacionDTO tipo = (TipoHabitacionDTO) request.getAttribute("tipo");
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
    <title>Detalle Tipo Habitación - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/utils/base.css">
    <link rel="stylesheet" href="css/tipohabitaciones/tipohabitacion_detalle.css">
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
            <h1><i class="fas fa-bed"></i> Detalle de Tipo de Habitación</h1>
            <p class="dashboard-subtitle">Información completa del tipo de habitación</p>
        </div>

        <div class="simple-detail-container">
            <div class="simple-detail-card">
                <div class="simple-detail-header">
                    <h2><%= tipo.getNombre() %></h2>
                    <span class="status-badge <%= tipo.isActivo() ? "active" : "inactive" %>">
                        <%= tipo.isActivo() ? "Activo" : "Inactivo" %>
                    </span>
                </div>

                <div class="simple-detail-content">
                    <div class="detail-row">
                        <span class="detail-label">Descripción:</span>
                        <span class="detail-value"><%= tipo.getDescripcion() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Capacidad:</span>
                        <span class="detail-value"><%= tipo.getCapacidadPersonas() %> personas</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Precio Base:</span>
                        <span class="detail-value price">S/ <%= String.format("%.2f", tipo.getPrecioBase()) %></span>
                    </div>
                </div>

                <div class="simple-detail-actions">
                    <a href="tipoHabitacion_editar?id=<%= tipo.getIdTipo() %>" class="btn btn-primary">
                        <i class="fas fa-edit"></i> Editar
                    </a>
                    
                    <% if (tipo.isActivo()) { %>
                    <form action="tipoHabitacion_desactivar" method="post" class="inline-form">
                        <input type="hidden" name="id" value="<%= tipo.getIdTipo() %>">
                        <button type="submit" class="btn btn-warning">
                            <i class="fas fa-toggle-off"></i> Desactivar
                        </button>
                    </form>
                    <% } else { %>
                    <form action="tipoHabitacion_activar" method="post" class="inline-form">
                        <input type="hidden" name="id" value="<%= tipo.getIdTipo() %>">
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-toggle-on"></i> Activar
                        </button>
                    </form>
                    <% } %>
                    
                    <form action="tipoHabitacion_eliminar" method="post" class="inline-form" onsubmit="return confirm('¿Estás seguro de eliminar este tipo de habitación?');">
                        <input type="hidden" name="id" value="<%= tipo.getIdTipo() %>">
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-trash-alt"></i> Eliminar
                        </button>
                    </form>
                    
                    <a href="tipoHabitacion" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Volver
                    </a>
                </div>
            </div>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
    <p class="version">Versión 1.0.0</p>
</footer>

</body>
</html>