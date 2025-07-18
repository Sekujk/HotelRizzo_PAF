<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.ReservaDTO" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<ReservaDTO> reservas = (List<ReservaDTO>) request.getAttribute("reservas");
    int total = (Integer) request.getAttribute("total");
    Long pendientes = (Long) request.getAttribute("pendientes");
    Long confirmadas = (Long) request.getAttribute("confirmadas");
    Long checkedIn = (Long) request.getAttribute("checkedIn");
    Long checkedOut = (Long) request.getAttribute("checkedOut");
    Long canceladas = (Long) request.getAttribute("canceladas");
    
    List<ReservaDTO> checkinHoy = (List<ReservaDTO>) request.getAttribute("checkinHoy");
    List<ReservaDTO> checkoutHoy = (List<ReservaDTO>) request.getAttribute("checkoutHoy");
    
    boolean esBusqueda = request.getAttribute("esBusqueda") != null;
    boolean esFiltro = request.getAttribute("esFiltro") != null;
    String criterio = (String) request.getAttribute("criterio");
    String estadoFiltro = (String) request.getAttribute("estadoFiltro");
    
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reservas - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/reservas/reservas.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        /* ✅ ESTILOS PARA BOTONES DE SERVICIOS */

        /* Botón principal de servicios (cuando está en CheckIn) */
        .btn-action.services {
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            color: white;
            border: none;
            transition: all 0.3s ease;
        }

        .btn-action.services:hover {
            background: linear-gradient(135deg, #5b21b6, #7c3aed);
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
        }

        /* Botón agregar producto */
        .btn-action.add-product {
            background: linear-gradient(135deg, #3b82f6, #2563eb);
            color: white;
            border: none;
            transition: all 0.3s ease;
        }

        .btn-action.add-product:hover {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }

        /* Botón ver servicios (solo lectura) */
        .btn-action.services-view {
            background: linear-gradient(135deg, #64748b, #475569);
            color: white;
            border: none;
            transition: all 0.3s ease;
        }

        .btn-action.services-view:hover {
            background: linear-gradient(135deg, #475569, #334155);
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(100, 116, 139, 0.3);
        }

        /* Ajustar el contenedor de acciones para más botones */
        .action-buttons {
            display: flex;
            gap: 4px;
            flex-wrap: wrap;
            justify-content: center;
        }

        /* Estados específicos para diferentes tipos de reserva */
        tr[data-estado="checkin"] .btn-action.services,
        tr[data-estado="checkin"] .btn-action.add-service,
        tr[data-estado="checkin"] .btn-action.add-product {
            animation: pulse-glow 2s infinite;
        }

        @keyframes pulse-glow {
            0%, 100% {
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }
            50% {
                box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
            }
        }

        /* Responsive para dispositivos móviles */
        @media (max-width: 768px) {
            .action-buttons {
                flex-direction: column;
                gap: 2px;
            }
            
            .btn-action {
                font-size: 11px;
                padding: 4px 6px;
            }
        }

        /* Tooltip para mejor UX */
        .btn-action {
            position: relative;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .btn-action:active {
            transform: scale(0.95);
        }
    </style>
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
        <a href="logout" class="btn-logout">
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
                    <li><a href="reservas" class="nav-link active">
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
            <% } %>
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-calendar-alt"></i> Gestión de Reservas</h1>
            <p class="dashboard-subtitle">Panel de administración de reservas del hotel</p>
        </div>

        <!-- Estadísticas Principales -->
        <div class="stats-grid">
            <div class="stat-card info">
                <div class="stat-icon">
                    <i class="fas fa-list"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-total"><%= total %></h3>
                    <p>Total Reservas</p>
                </div>
            </div>

            <div class="stat-card warning">
                <div class="stat-icon">
                    <i class="fas fa-clock"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-pendientes"><%= pendientes != null ? pendientes : 0 %></h3>
                    <p>Pendientes</p>
                </div>
            </div>

            <div class="stat-card success">
                <div class="stat-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-confirmadas"><%= confirmadas != null ? confirmadas : 0 %></h3>
                    <p>Confirmadas</p>
                </div>
            </div>

            <div class="stat-card primary">
                <div class="stat-icon">
                    <i class="fas fa-door-open"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-checkedin"><%= checkedIn != null ? checkedIn : 0 %></h3>
                    <p>Check-In</p>
                </div>
            </div>

            <div class="stat-card complete">
                <div class="stat-icon">
                    <i class="fas fa-door-closed"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-checkedout"><%= checkedOut != null ? checkedOut : 0 %></h3>
                    <p>Check-Out</p>
                </div>
            </div>

            <div class="stat-card danger">
                <div class="stat-icon">
                    <i class="fas fa-times-circle"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-canceladas"><%= canceladas != null ? canceladas : 0 %></h3>
                    <p>Canceladas</p>
                </div>
            </div>
        </div>

        <!-- Alertas de Check-in/Check-out de hoy -->
        <% if ((checkinHoy != null && !checkinHoy.isEmpty()) || (checkoutHoy != null && !checkoutHoy.isEmpty())) { %>
        <div class="alerts-section">
            <% if (checkinHoy != null && !checkinHoy.isEmpty()) { %>
            <div class="alert alert-info">
                <div class="alert-header">
                    <i class="fas fa-calendar-check"></i>
                    <h3>Check-ins para Hoy (<%= checkinHoy.size() %>)</h3>
                </div>
                <div class="alert-body">
                    <% for (ReservaDTO reserva : checkinHoy) { %>
                    <div class="alert-item">
                        <span class="alert-text">
                            <strong><%= reserva.getNumeroReserva() %></strong> - <%= reserva.getNombreCliente() %>
                        </span>
                        <a href="reserva_detalle?id=<%= reserva.getIdReserva() %>" class="btn-alert">Ver</a>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } %>

            <% if (checkoutHoy != null && !checkoutHoy.isEmpty()) { %>
            <div class="alert alert-warning">
                <div class="alert-header">
                    <i class="fas fa-calendar-times"></i>
                    <h3>Check-outs para Hoy (<%= checkoutHoy.size() %>)</h3>
                </div>
                <div class="alert-body">
                    <% for (ReservaDTO reserva : checkoutHoy) { %>
                    <div class="alert-item">
                        <span class="alert-text">
                            <strong><%= reserva.getNumeroReserva() %></strong> - <%= reserva.getNombreCliente() %>
                        </span>
                        <a href="reserva_detalle?id=<%= reserva.getIdReserva() %>" class="btn-alert">Ver</a>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } %>
        </div>
        <% } %>

        <!-- Barra de Acciones -->
        <div class="actions-bar">
            <div class="left-actions">
                <a href="dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver al Panel
                </a>
                <a href="reserva_crear" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Nueva Reserva
                </a>
                <% if (esBusqueda || esFiltro) { %>
                <a href="reservas" class="btn btn-info">
                    <i class="fas fa-refresh"></i> Ver Todas
                </a>
                <% } %>
            </div>
            
            <div class="filters-section">
                <!-- Filtro por Estado -->
                <form method="post" action="reservas" class="filter-form">
                    <input type="hidden" name="action" value="filtrar">
                    <select name="estado" onchange="this.form.submit()" class="filter-select">
                        <option value="Todos" <%= "Todos".equals(estadoFiltro) ? "selected" : "" %>>Todos los Estados</option>
                        <option value="Pendiente" <%= "Pendiente".equals(estadoFiltro) ? "selected" : "" %>>Pendientes</option>
                        <option value="Confirmada" <%= "Confirmada".equals(estadoFiltro) ? "selected" : "" %>>Confirmadas</option>
                        <option value="CheckIn" <%= "CheckIn".equals(estadoFiltro) ? "selected" : "" %>>Check-In</option>
                        <option value="CheckOut" <%= "CheckOut".equals(estadoFiltro) ? "selected" : "" %>>Check-Out</option>
                        <option value="Cancelada" <%= "Cancelada".equals(estadoFiltro) ? "selected" : "" %>>Canceladas</option>
                    </select>
                </form>
                
                <!-- Búsqueda -->
                <form method="post" action="reservas" class="search-form">
                    <input type="hidden" name="action" value="buscar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" name="criterio" placeholder="Buscar reserva, cliente, DNI..." 
                               value="<%= criterio != null ? criterio : "" %>">
                        <button type="submit" class="btn-search">Buscar</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Resultados de búsqueda/filtro -->
        <% if (esBusqueda) { %>
        <div class="search-results">
            <p><i class="fas fa-search"></i> Resultados de búsqueda para: "<strong><%= criterio %></strong>" - <%= total %> encontradas</p>
        </div>
        <% } else if (esFiltro && !"Todos".equals(estadoFiltro)) { %>
        <div class="search-results">
            <p><i class="fas fa-filter"></i> Mostrando reservas con estado: "<strong><%= estadoFiltro %></strong>" - <%= total %> encontradas</p>
        </div>
        <% } %>

        <!-- Tabla de Reservas -->
        <div class="table-container">
            <table id="reservasTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Número Reserva</th>
                        <th>Cliente</th>
                        <th>Fechas</th>
                        <th>Huéspedes</th>
                        <th>Estado</th>
                        <th>Total (S/.)</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (reservas != null && !reservas.isEmpty()) { 
                        int index = 1;
                        for (ReservaDTO r : reservas) { 
                    %>
                    <tr data-estado="<%= r.getEstado().toLowerCase() %>">
                        <td><%= index++ %></td>
                        <td>
                            <strong><%= r.getNumeroReserva() %></strong>
                        </td>
                        <td>
                            <div class="client-info">
                                <strong><%= r.getNombreCliente() %></strong>
                                <small>ID: <%= r.getIdCliente() %></small>
                            </div>
                        </td>
                        <td>
                            <div class="date-range">
                                <div class="date-item">
                                    <i class="fas fa-calendar-plus"></i>
                                    <%= r.getFechaEntrada().format(dateFormatter) %>
                                </div>
                                <div class="date-item">
                                    <i class="fas fa-calendar-minus"></i>
                                    <%= r.getFechaSalida().format(dateFormatter) %>
                                </div>
                                <small class="nights-count">
                                    <%= r.getTotalNoches() %> noche<%= r.getTotalNoches() != 1 ? "s" : "" %>
                                </small>
                            </div>
                        </td>
                        <td>
                            <span class="guests-count">
                                <i class="fas fa-users"></i>
                                <%= r.getNumHuespedes() %>
                            </span>
                        </td>
                        <td>
                            <span class="status-badge <%= r.getEstado().toLowerCase() %>">
                                <% 
                                String icono = "";
                                switch(r.getEstado()) {
                                    case "Pendiente": icono = "fas fa-clock"; break;
                                    case "Confirmada": icono = "fas fa-check"; break;
                                    case "CheckIn": icono = "fas fa-door-open"; break;
                                    case "CheckOut": icono = "fas fa-door-closed"; break;
                                    case "Cancelada": icono = "fas fa-times"; break;
                                    default: icono = "fas fa-question";
                                }
                                %>
                                <i class="<%= icono %>"></i>
                                <%= r.getEstado() %>
                            </span>
                        </td>
                        <td>
                            <div class="amount-info">
                                <strong>S/. <%= String.format("%.2f", r.getMontoTotal()) %></strong>
                                <% if (r.getSubtotal().compareTo(r.getMontoTotal()) != 0) { %>
                                <small>+ S/. <%= String.format("%.2f", r.getImpuestos()) %> IGV</small>
                                <% } %>
                            </div>
                        </td>
                        <td class="actions-cell">
                            <div class="action-buttons">
                                <a class="btn-action view" href="reserva_detalle?id=<%= r.getIdReserva() %>" title="Ver Detalles">
                                    <i class="fas fa-eye"></i>
                                </a>
                                
                                <!-- ✅ BOTONES DE SERVICIOS Y PRODUCTOS -->
                                <% if ("CheckIn".equals(r.getEstado())) { %>
                                <a class="btn-action services" href="reserva_servicios?id=<%= r.getIdReserva() %>" title="Gestionar Servicios">
                                    <i class="fas fa-concierge-bell"></i>
                                </a>
                                <a class="btn-action add-service" href="reserva_servicios?action=agregar_servicio&id=<%= r.getIdReserva() %>" title="Agregar Servicio">
                                    <i class="fas fa-plus-circle"></i>
                                </a>
                                <a class="btn-action add-product" href="reserva_servicios?action=agregar_producto&id=<%= r.getIdReserva() %>" title="Agregar Producto">
                                    <i class="fas fa-shopping-cart"></i>
                                </a>
                                <% } else if (!"Pendiente".equals(r.getEstado()) && !"Cancelada".equals(r.getEstado())) { %>
                                <a class="btn-action services-view" href="reserva_servicios?id=<%= r.getIdReserva() %>" title="Ver Servicios">
                                    <i class="fas fa-list-alt"></i>
                                </a>
                                <% } %>
                                
                                <% if ("Pendiente".equals(r.getEstado()) || "Confirmada".equals(r.getEstado())) { %>
                                <a class="btn-action edit" href="reserva_editar?id=<%= r.getIdReserva() %>" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <% } %>
                                
                                <% if ("Confirmada".equals(r.getEstado()) && r.esCheckInHoy()) { %>
                                <a class="btn-action checkin" href="reserva_checkin?id=<%= r.getIdReserva() %>" title="Check-In">
                                    <i class="fas fa-door-open"></i>
                                </a>
                                <% } %>
                                
                                <% if ("CheckIn".equals(r.getEstado()) && r.esCheckOutHoy()) { %>
                                <a class="btn-action checkout" href="reserva_checkout?id=<%= r.getIdReserva() %>" title="Check-Out">
                                    <i class="fas fa-door-closed"></i>
                                </a>
                                <% } %>
                                
                                <% if ("Pendiente".equals(r.getEstado()) || "Confirmada".equals(r.getEstado())) { %>
                                <form method="post" action="reserva_cancelar" class="inline-form">
                                    <input type="hidden" name="id" value="<%= r.getIdReserva() %>">
                                    <button type="submit" class="btn-action cancel" title="Cancelar" 
                                            onclick="return confirm('¿Cancelar esta reserva?');">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </form>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                    <% } 
                    } else { %>
                    <tr class="no-data">
                        <td colspan="8">
                            <div class="empty-state">
                                <i class="fas fa-calendar-alt"></i>
                                <% if (esBusqueda) { %>
                                    <p>No se encontraron reservas para el criterio: "<%= criterio %>"</p>
                                <% } else if (esFiltro) { %>
                                    <p>No hay reservas con estado: "<%= estadoFiltro %>"</p>
                                <% } else { %>
                                    <p>No hay reservas registradas</p>
                                <% } %>
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
</footer>

<script src="${pageContext.request.contextPath}/js/reserva.js"></script>
</body>
</html>