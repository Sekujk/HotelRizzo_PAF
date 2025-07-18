<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.TipoHabitacionDTO" %>
<%@ page session="true" %>
<%
    List<TipoHabitacionDTO> tipos = (List<TipoHabitacionDTO>) request.getAttribute("tipos");
    long activas = (long) request.getAttribute("activos");
    int total = (int) request.getAttribute("total");
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
    <title>Tipos de Habitación - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/utils/base.css">
    <link rel="stylesheet" href="css/tipohabitaciones/tipohabitacion.css">
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
            <h1><i class="fas fa-bed"></i> Tipos de Habitación</h1>
            <p class="dashboard-subtitle">Administración de categorías de habitaciones</p>
        </div>

        <div class="card-container">
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-icon">
                        <i class="fas fa-bed"></i>
                    </div>
                    <div class="stat-content">
                        <h3><%= activas %></h3>
                        <p>Tipos Activos</p>
                    </div>
                </div>
                <div class="stat-card success">
                    <div class="stat-icon">
                        <i class="fas fa-list"></i>
                    </div>
                    <div class="stat-content">
                        <h3><%= total %></h3>
                        <p>Total Tipos</p>
                    </div>
                </div>
            </div>

            <div class="card-actions">
                <div class="search-box">
                    <i class="fas fa-search"></i>
                    <input type="text" id="buscador-tipohabitacion" placeholder="Buscar tipo de habitación...">
                </div>
                <div class="action-buttons">
                    <a href="tipoHabitacion_crear" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Nuevo Tipo
                    </a>
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Precio Base</th>
                            <th>Capacidad</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (TipoHabitacionDTO tipo : tipos) { %>
                        <tr>
                            <td><%= tipo.getNombre() %></td>
                            <td><%= tipo.getDescripcion() %></td>
                            <td>S/ <%= String.format("%.2f", tipo.getPrecioBase()) %></td>
                            <td><%= tipo.getCapacidadPersonas() %> personas</td>
                            <td class="action-cell">
                                <a href="tipoHabitacion_detalle?id=<%= tipo.getIdTipo() %>" class="btn-action detail" title="Ver Detalles">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a href="tipoHabitacion_editar?id=<%= tipo.getIdTipo() %>" class="btn-action edit" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <% if (tipo.isActivo()) { %>
                                <form action="tipoHabitacion_desactivar" method="post" class="inline-form">
                                    <input type="hidden" name="id" value="<%= tipo.getIdTipo() %>">
                                    <button type="submit" class="btn-action deactivate" title="Desactivar">
                                        <i class="fas fa-toggle-off"></i>
                                    </button>
                                </form>
                                <% } else { %>
                                <form action="tipoHabitacion_activar" method="post" class="inline-form">
                                    <input type="hidden" name="id" value="<%= tipo.getIdTipo() %>">
                                    <button type="submit" class="btn-action activate" title="Activar">
                                        <i class="fas fa-toggle-on"></i>
                                    </button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
    <p class="version">Versión 1.0.0</p>
</footer>

<script src="${pageContext.request.contextPath}/js/tipohabitacion.js"></script>
</body>
</html>