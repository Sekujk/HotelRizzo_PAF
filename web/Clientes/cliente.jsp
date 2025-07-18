<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.ClienteDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<ClienteDTO> clientes = (List<ClienteDTO>) request.getAttribute("clientes");
    long totalClientes = clientes != null ? clientes.size() : 0;
    long corporativos = clientes != null ? clientes.stream().filter(c -> c.getTipoCliente().equals("Corporativo")).count() : 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Clientes - Hotel Rizzo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/utils/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/clientes/cliente.css">
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
            <h1><i class="fas fa-users"></i> Gestión de Clientes</h1>
            <p class="dashboard-subtitle">Panel de administración de clientes del hotel</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card primary">
                <div class="stat-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-total"><%= totalClientes %></h3>
                    <p>Total Clientes</p>
                </div>
            </div>

            <div class="stat-card success">
                <div class="stat-icon">
                    <i class="fas fa-building"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-corporativos"><%= corporativos %></h3>
                    <p>Corporativos</p>
                </div>
            </div>

            <div class="stat-card info">
                <div class="stat-icon">
                    <i class="fas fa-user"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-individuales"><%= totalClientes - corporativos %></h3>
                    <p>Individuales</p>
                </div>
            </div>
        </div>

        <div class="actions-bar">
            <div class="left-actions">
                <a href="dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver al Panel
                </a>
            </div>
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" id="searchInput" placeholder="Buscar por nombre, DNI o correo...">
            </div>
        </div>

        <div class="table-container">
            <table id="clientesTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>DNI</th>
                        <th>Nombre Completo</th>
                        <th>Teléfono</th>
                        <th>Correo</th>
                        <th>Tipo</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (clientes != null && !clientes.isEmpty()) { 
                        int index = 1;
                        for (ClienteDTO c : clientes) { 
                    %>
                    <tr data-dni="<%= c.getDni().toLowerCase() %>" 
                        data-nombre="<%= (c.getNombre() + " " + c.getApellido()).toLowerCase() %>"
                        data-correo="<%= c.getCorreo().toLowerCase() %>"
                        data-tipo="<%= c.getTipoCliente().toLowerCase() %>">
                        <td><%= index++ %></td>
                        <td><%= c.getDni() %></td>
                        <td><strong><%= c.getNombre() %> <%= c.getApellido() %></strong></td>
                        <td><%= c.getTelefono() %></td>
                        <td><%= c.getCorreo() %></td>
                        <td>
                            <span class="status-badge <%= c.getTipoCliente().equals("Corporativo") ? "corporate" : "individual" %>">
                                <%= c.getTipoCliente() %>
                            </span>
                        </td>
                        <td class="actions-cell">
                            <div class="action-buttons">
                                <a class="btn-action view" href="cliente_detalle?id=<%= c.getId() %>" title="Ver Detalles">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a class="btn-action edit" href="cliente_editar?id=<%= c.getId() %>" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                    <% } 
                    } else { %>
                    <tr class="no-data">
                        <td colspan="7">
                            <div class="empty-state">
                                <i class="fas fa-users"></i>
                                <p>No hay clientes registrados</p>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
    <p class="version">Versión 1.0.0</p>
</footer>

<script src="${pageContext.request.contextPath}/js/cliente.js"></script>
</body>
</html>