<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.EmpleadoDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<EmpleadoDTO> empleados = (List<EmpleadoDTO>) request.getAttribute("empleados");
    long activos = (long) request.getAttribute("activos");
    int total = (int) request.getAttribute("total");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Empleados - Hotel Rizzo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/utils/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/empleados/empleado.css">
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
                    <li><a href="reservas" class="nav-link"><i class="fas fa-calendar-alt"></i><span>Reservas</span></a></li>
                    <li><a href="clientes" class="nav-link"><i class="fas fa-id-card"></i><span>Clientes</span></a></li>
                    <li><a href="habitaciones" class="nav-link"><i class="fas fa-bed"></i><span>Habitaciones</span></a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h3>Servicios</h3>
                <ul>
                    <li><a href="servicio" class="nav-link"><i class="fas fa-concierge-bell"></i><span>Servicios</span></a></li>
                    <li><a href="producto" class="nav-link"><i class="fas fa-shopping-cart"></i><span>Productos</span></a></li>
                </ul>
            </div>
            <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
            <div class="nav-section">
                <h3>Administración</h3>
                <ul>
                    <li><a href="empleado" class="nav-link active"><i class="fas fa-users"></i><span>Empleados</span></a></li>
                </ul>
            </div>
            <% } %>
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-users"></i> Gestión de Empleados</h1>
            <p class="dashboard-subtitle">Panel de administración de empleados del hotel</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card primary">
                <div class="stat-icon"><i class="fas fa-user-check"></i></div>
                <div class="stat-content">
                    <h3><%= activos %></h3>
                    <p>Empleados Activos</p>
                </div>
            </div>
            <div class="stat-card success">
                <div class="stat-icon"><i class="fas fa-users"></i></div>
                <div class="stat-content">
                    <h3><%= total %></h3>
                    <p>Total Empleados</p>
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
                <input type="text" id="buscador-empleado" placeholder="Buscar por nombre, DNI o rol...">
            </div>
            <div class="right-actions">
                <a href="empleado_crear" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Nuevo Empleado
                </a>
            </div>
        </div>

        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>DNI</th>
                        <th>Rol</th>
                        <th>Salario</th>
                        <th>Turno</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (empleados != null && !empleados.isEmpty()) {
                        int i = 1;
                        for (EmpleadoDTO emp : empleados) {
                    %>
                    <tr>
                        <td><%= i++ %></td>
                        <td><%= emp.getNombreCompleto() %></td>
                        <td><%= emp.getDni() %></td>
                        <td><%= emp.getNombreRol() %></td>
                        <td>S/ <%= String.format("%.2f", emp.getSalario()) %></td>
                        <td><%= emp.getTurno() %></td>
                        <td>
                            <span class="status-badge <%= emp.isActivo() ? "active" : "inactive" %>">
                                <%= emp.isActivo() ? "Activo" : "Inactivo" %>
                            </span>
                        </td>
                        <td class="actions-cell">
                            <div class="action-buttons">
                                <a class="btn-action view" href="empleado_detalle?id=<%= emp.getIdEmpleado() %>" title="Ver Detalles">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a class="btn-action edit" href="empleado_editar?id=<%= emp.getIdEmpleado() %>" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <% if (emp.isActivo()) { %>
                                <form action="empleado_desactivar" method="post" class="inline-form">
                                    <input type="hidden" name="id" value="<%= emp.getIdEmpleado() %>">
                                    <button type="submit" class="btn-action deactivate" title="Desactivar">
                                        <i class="fas fa-user-slash"></i>
                                    </button>
                                </form>
                                <% } else { %>
                                <form action="empleado_activar" method="post" class="inline-form">
                                    <input type="hidden" name="id" value="<%= emp.getIdEmpleado() %>">
                                    <button type="submit" class="btn-action activate" title="Activar">
                                        <i class="fas fa-user-check"></i>
                                    </button>
                                </form>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                    <% }} else { %>
                    <tr class="no-data">
                        <td colspan="8">
                            <div class="empty-state">
                                <i class="fas fa-users-slash"></i>
                                <p>No hay empleados registrados</p>
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

<script src="${pageContext.request.contextPath}/js/empleado.js"></script>
</body>
</html>
