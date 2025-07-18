<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ClienteDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");

    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    ClienteDTO cliente = (ClienteDTO) request.getAttribute("cliente");
    if (cliente == null) {
        response.sendRedirect("clientes");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle Cliente - Hotel Rizzo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/utils/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/clientes/cliente_detalle.css">
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
                    <li><a href="clientes" class="nav-link active">
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
            <h1><i class="fas fa-user"></i> Detalle del Cliente</h1>
            <p class="dashboard-subtitle">Información completa del cliente registrado</p>
        </div>

        <div class="detail-container">
            <div class="detail-actions">
                <a href="clientes" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver a Clientes
                </a>
                <a href="cliente_editar?id=<%= cliente.getId() %>" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Editar Cliente
                </a>
            </div>

            <div class="detail-grid">
                <div class="detail-card">
                    <div class="detail-icon">
                        <i class="fas fa-id-card"></i>
                    </div>
                    <div class="detail-content">
                        <h3>DNI</h3>
                        <p><%= cliente.getDni() %></p>
                    </div>
                </div>

                <div class="detail-card">
                    <div class="detail-icon">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="detail-content">
                        <h3>Nombre Completo</h3>
                        <p><%= cliente.getNombre() %> <%= cliente.getApellido() %></p>
                    </div>
                </div>

                <div class="detail-card">
                    <div class="detail-icon">
                        <i class="fas fa-phone"></i>
                    </div>
                    <div class="detail-content">
                        <h3>Teléfono</h3>
                        <p><%= cliente.getTelefono() %></p>
                    </div>
                </div>

                <div class="detail-card">
                    <div class="detail-icon">
                        <i class="fas fa-envelope"></i>
                    </div>
                    <div class="detail-content">
                        <h3>Correo Electrónico</h3>
                        <p><%= cliente.getCorreo() %></p>
                    </div>
                </div>

                <div class="detail-card">
                    <div class="detail-icon">
                        <i class="fas fa-user-tag"></i>
                    </div>
                    <div class="detail-content">
                        <h3>Tipo de Cliente</h3>
                        <p>
                            <span class="status-badge <%= cliente.getTipoCliente().equals("Corporativo") ? "corporate" : "individual" %>">
                                <%= cliente.getTipoCliente() %>
                            </span>
                        </p>
                    </div>
                </div>

                <% if (cliente.getTipoCliente().equals("Corporativo") && cliente.getEmpresa() != null) { %>
                <div class="detail-card">
                    <div class="detail-icon">
                        <i class="fas fa-building"></i>
                    </div>
                    <div class="detail-content">
                        <h3>Empresa</h3>
                        <p><%= cliente.getEmpresa() %></p>
                    </div>
                </div>
                <% } %>
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