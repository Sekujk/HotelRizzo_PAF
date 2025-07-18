<%@page import="Utils.Conexion"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ServicioDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    ServicioDTO servicio = (ServicioDTO) request.getAttribute("servicio");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle de Servicio - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/servicios/servicio_detalle.css">
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
                    <li><a href="habitaciones" class="nav-link">
                        <i class="fas fa-bed"></i>
                        <span>Habitaciones</span>
                    </a></li>
                </ul>
            </div>
            
            <div class="nav-section">
                <h3>Servicios</h3>
                <ul>
                    <li><a href="servicio" class="nav-link active">
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
                <h1><i class="fas fa-concierge-bell"></i> Detalle del Servicio</h1>
                <p class="detail-subtitle">Información completa del servicio registrado</p>
            </div>
            
            <div class="detail-card">
                <div class="detail-section">
                    <h3>Información Básica</h3>
                    <div class="detail-row">
                        <span class="detail-label">Nombre:</span>
                        <span class="detail-value"><%= servicio.getNombre() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Descripción:</span>
                        <span class="detail-value"><%= servicio.getDescripcion() %></span>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h3>Información Económica</h3>
                    <div class="detail-row">
                        <span class="detail-label">Precio Unitario:</span>
                        <span class="detail-value">S/ <%= String.format("%.2f", servicio.getPrecioUnitario()) %></span>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h3>Estado</h3>
                    <div class="detail-row">
                        <span class="detail-label">Estado:</span>
                        <span class="detail-value <%= servicio.isActivo() ? "text-success" : "text-danger" %>">
                            <i class="fas <%= servicio.isActivo() ? "fa-check-circle" : "fa-times-circle" %>"></i>
                            <%= servicio.isActivo() ? "Activo" : "Inactivo" %>
                        </span>
                    </div>
                </div>
            </div>
            
            <div class="detail-actions">
                <% if ("Administrador".equalsIgnoreCase(rolUsuario) || "Gerente".equalsIgnoreCase(rolUsuario)) { %>
                <a href="servicio_editar?id=<%= servicio.getIdServicio() %>" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Editar Servicio
                </a>
                
                <form action="servicio_eliminar" method="post" onsubmit="return confirm('¿Está seguro que desea eliminar este servicio?');" class="inline-form">
                    <input type="hidden" name="id" value="<%= servicio.getIdServicio() %>">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash-alt"></i> Eliminar Servicio
                    </button>
                </form>
                <% } %>
                
                <a href="servicio" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver a la lista
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