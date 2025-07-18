<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");

    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Long empleadosActivos = (Long) request.getAttribute("empleadosActivos");
    if (empleadosActivos == null) {
        empleadosActivos = 0L;
    }

    Integer stockBajo = (Integer) request.getAttribute("stockBajo");
    if (stockBajo == null) {
        stockBajo = 0;
    }

    Long serviciosActivos = (Long) request.getAttribute("serviciosActivos");
    if (serviciosActivos == null) {
        serviciosActivos = 0L;
    }

    Integer reservasTotales = (Integer) request.getAttribute("reservasTotales");
    if (reservasTotales == null) {
        reservasTotales = 0;
    }


%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel de Administración - Hotel Rizzo</title>
        <link rel="stylesheet" href="css/utils/base.css">
        <link rel="stylesheet" href="css/auth/dashboard.css">
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
                    <% }%>
                </nav>
            </aside>

            <main class="main-content">
                <div class="dashboard-header">
                    <h1><i class="fas fa-tachometer-alt"></i> Panel de Control</h1>
                    <p class="dashboard-subtitle">Resumen del estado actual del hotel</p>
                </div>

                <div class="stats-grid">
                    <div class="stat-card primary">
                        <div class="stat-icon">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="stat-content">
                            <h3><%= reservasTotales%></h3>
                            <p>Total de Reservas</p>

                        </div>
                    </div>

                    <div class="stat-card info">
                        <div class="stat-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-content">
                            <h3><%= empleadosActivos%></h3>
                            <p>Empleados Activos</p>
                        </div>
                    </div>

                    <div class="stat-card info">
                        <div class="stat-icon">
                            <i class="fas fa-concierge-bell"></i>
                        </div>
                        <div class="stat-content">
                            <h3><%= serviciosActivos%></h3>
                            <p>Servicios Activos</p>
                        </div>
                    </div>

                    <div class="stat-card <%= stockBajo > 0 ? "danger" : "success"%>">
                        <div class="stat-icon">
                            <i class="fas fa-boxes"></i>
                        </div>
                        <div class="stat-content">
                            <% if (stockBajo > 0) {%>
                            <h3><%= stockBajo%></h3>
                            <p>Productos con Stock Bajo</p>
                            <% } else { %>
                            <h3>OK</h3>
                            <p>Stock Suficiente</p>
                            <% }%>
                        </div>
                    </div>
                </div>

                <div class="quick-actions">
                    <h2>Acciones Rápidas</h2>
                    <div class="action-buttons">
                        <a href="reserva_buscar_cliente" class="action-btn primary">
                            <i class="fas fa-plus"></i>
                            Nueva Reserva
                        </a>
                    </div>
                    <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
                    <a href="producto_crear" class="action-btn success">
                        <i class="fas fa-box-open"></i>
                        Añadir Producto
                    </a>
                    <a href="servicio_crear" class="action-btn info">
                        <i class="fas fa-hand-holding-heart"></i>
                        Añadir Servicio
                    </a>
                    <% }%>
                </div>

            </main>
        </div>

        <footer class="footer">
            <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
        </footer>

    </body>
</html>