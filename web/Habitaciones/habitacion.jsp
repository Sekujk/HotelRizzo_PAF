<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.HabitacionDTO" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");

    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<HabitacionDTO> habitaciones = (List<HabitacionDTO>) request.getAttribute("habitaciones");
    long activas = (long) request.getAttribute("activas");
    int total = (int) request.getAttribute("total");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Habitaciones - Hotel Rizzo</title>
        <link rel="stylesheet" href="css/habitaciones/habitacion.css">
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
                        <span class="user-name"><%= usuario%></span>
                        <span class="user-role"><%= rolUsuario%></span>
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
                    <% }%>
                </nav>
            </aside>

            <main class="main-content">
                <div class="dashboard-header">
                    <h1><i class="fas fa-bed"></i> Gestión de Habitaciones</h1>
                    <p class="dashboard-subtitle">Panel de administración de habitaciones del hotel</p>
                </div>

                <div class="stats-grid">
                    <div class="stat-card info">
                        <div class="stat-icon">
                            <i class="fas fa-list"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="stat-total"><%= total%></h3>
                            <p>Total Habitaciones</p>
                        </div>
                    </div>

                    <div class="stat-card success">
                        <div class="stat-icon">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="stat-activas"><%= activas%></h3>
                            <p>Disponibles</p>
                        </div>
                    </div>

                    <div class="stat-card warning">
                        <div class="stat-icon">
                            <i class="fas fa-exclamation-circle"></i>
                        </div>
                        <div class="stat-content">
                            <h3 id="stat-ocupadas"><%= total - activas%></h3>
                            <p>Ocupadas/Mantenimiento</p>
                        </div>
                    </div>
                </div>

                <div class="actions-bar">
                    <div class="left-actions">
                        <a href="dashboard" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Volver al Panel
                        </a>
                        <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
                        <a href="habitacion_crear" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Nueva Habitación
                        </a>
                        <% } %>
                    </div>
                    <div class="filter-section">
                        <%
                            java.util.Set<Integer> pisosUnicos = new java.util.HashSet<>();
                            java.util.Set<String> tiposUnicos = new java.util.HashSet<>();
                        %>
                        <select id="filtro-piso" class="filter-select">
                            <option value="">Todos los Pisos</option>
                            <% for (HabitacionDTO h : habitaciones) {
                                    int piso = h.getPiso();
                                    if (pisosUnicos.add(piso)) {%>
                            <option value="<%= piso%>">Piso <%= piso%></option>
                            <%  }
                                } %>
                        </select>

                        <select id="filtro-tipo" class="filter-select">
                            <option value="">Todos los Tipos</option>
                            <% for (HabitacionDTO h : habitaciones) {
                                    String tipo = h.getNombreTipo();
                                    if (tiposUnicos.add(tipo)) {%>
                            <option value="<%= tipo%>"><%= tipo%></option>
                            <%  }
                                } %>
                        </select>

                        <select id="filtro-estado" class="filter-select">
                            <option value="">Todos los Estados</option>
                            <option value="Disponible">Disponible</option>
                            <option value="Ocupado">Ocupado</option>
                            <option value="Mantenimiento">Mantenimiento</option>
                        </select>
                    </div>
                </div>

                <% if ("Administrador".equalsIgnoreCase(rolUsuario)) { %>
                <div class="admin-actions">
                    <a href="tipoHabitacion" class="btn btn-secondary">
                        <i class="fas fa-list-alt"></i> Tipos de Habitación
                    </a>
                    <a href="tarifaespecial" class="btn btn-secondary">
                        <i class="fas fa-tags"></i> Tarifas Especiales
                    </a>
                </div>
                <% } %>

                <div class="table-container">
                    <table id="habitacionesTable">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Número</th>
                                <th>Piso</th>
                                <th>Tipo</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (habitaciones != null && !habitaciones.isEmpty()) {
                                    int index = 1;
                                    for (HabitacionDTO hab : habitaciones) {
                                        String estadoClase = (hab.getEstado() != null) ? hab.getEstado().toLowerCase().replace(" ", "-") : "estado-desconocido";
                            %>
                            <tr data-piso="<%= hab.getPiso()%>"
                                data-tipo="<%= hab.getNombreTipo()%>"
                                data-estado="<%= hab.getEstado()%>">
                                <td><%= index++%></td>
                                <td><strong><%= hab.getNumero()%></strong></td>
                                <td><%= hab.getPiso()%></td>
                                <td><%= hab.getNombreTipo()%></td>
                                <td>
                                    <span class="status-badge <%= estadoClase%>">
                                        <%= hab.getEstado()%>
                                    </span>
                                </td>
                                <td class="actions-cell">
                                    <div class="action-buttons">
                                        <a class="btn-action view" href="habitacion_detalle?id=<%= hab.getIdHabitacion()%>" title="Ver Detalles">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
                                        <% if ("Disponible".equalsIgnoreCase(hab.getEstado())) {%>
                                        <a class="btn-action edit" href="habitacion_editar?id=<%= hab.getIdHabitacion()%>" title="Editar">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <% } else { %>
                                        <span class="btn-action disabled" title="Solo se pueden editar habitaciones disponibles">
                                            <i class="fas fa-ban"></i>
                                        </span>
                                        <% } %>

                                        <% if ("Disponible".equals(hab.getEstado())) {%>
                                        <a class="btn-action maintenance" href="habitacion_mantenimiento?id=<%= hab.getIdHabitacion()%>" title="Poner en Mantenimiento" onclick="return confirm('¿Poner esta habitación en mantenimiento?')">
                                            <i class="fas fa-tools"></i>
                                        </a>
                                        <% } else if ("Mantenimiento".equals(hab.getEstado())) {%>
                                        <a class="btn-action available" href="habitacion_disponible?id=<%= hab.getIdHabitacion()%>" title="Marcar como Disponible" onclick="return confirm('¿Marcar esta habitación como disponible?')">
                                            <i class="fas fa-check"></i>
                                        </a>
                                        <% } %>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>
                            <% }
                            } else { %>
                            <tr class="no-data">
                                <td colspan="6">
                                    <div class="empty-state">
                                        <i class="fas fa-door-open"></i>
                                        <p>No hay habitaciones registradas</p>
                                    </div>
                                </td>
                            </tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>

        <footer class="footer">
            <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
        </footer>

        <script src="${pageContext.request.contextPath}/js/habitacion.js"></script>
    </body>
</html>