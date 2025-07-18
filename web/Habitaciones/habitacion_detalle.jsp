<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.HabitacionDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    HabitacionDTO hab = (HabitacionDTO) request.getAttribute("habitacion");
    if (hab == null) { 
        response.sendRedirect("habitaciones?mensaje=error"); 
        return; 
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle Habitación - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/habitaciones/habitacion_detalle.css">
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
        <a href="login.jsp" class="btn-logout">
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
        <div class="detail-container">
            <div class="detail-header">
                <h1><i class="fas fa-door-open"></i> Detalle de Habitación</h1>
                <p class="detail-subtitle">Información completa de la habitación <%= hab.getNumero() %></p>
            </div>
            
            <div class="detail-card">
                <div class="detail-section">
                    <h3>Información Básica</h3>
                    <div class="detail-row">
                        <span class="detail-label">Número:</span>
                        <span class="detail-value"><%= hab.getNumero() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Piso:</span>
                        <span class="detail-value"><%= hab.getPiso() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Tipo:</span>
                        <span class="detail-value"><%= hab.getNombreTipo() %></span>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h3>Estado</h3>
                    <div class="detail-row">
                        <span class="detail-label">Estado Actual:</span>
                        <span class="status-badge <%= hab.getEstado().toLowerCase().replace(" ", "-") %>">
                            <%= hab.getEstado() %>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Creada el:</span>
                        <span class="detail-value"><%= hab.getCreatedAt() %></span>
                    </div>
                </div>
                
                <% if (hab.getObservaciones() != null && !hab.getObservaciones().isEmpty()) { %>
                <div class="detail-section">
                    <h3>Observaciones</h3>
                    <div class="detail-row">
                        <span class="detail-label">Notas:</span>
                        <span class="detail-value observations"><%= hab.getObservaciones() %></span>
                    </div>
                </div>
                <% } %>
            </div>
            
            <div class="detail-actions">
                <% if ("Administrador".equalsIgnoreCase(rolUsuario) || "Gerente".equalsIgnoreCase(rolUsuario)) { %>
                <a href="habitacion_editar?id=<%= hab.getIdHabitacion() %>" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Editar Habitación
                </a>
                
                <form action="habitacion_eliminar" method="post" onsubmit="return confirm('¿Está seguro de eliminar esta habitación?');" class="inline-form">
                    <input type="hidden" name="id" value="<%= hab.getIdHabitacion() %>">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash-alt"></i> Eliminar Habitación
                    </button>
                </form>
                <% } %>
                
                <a href="habitaciones" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver al listado
                </a>
            </div>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>
</body>
</html>